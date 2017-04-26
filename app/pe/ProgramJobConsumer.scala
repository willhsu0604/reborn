package pe

import java.io.{File, FileWriter, IOException}
import java.util.Date
import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue}
import java.util.concurrent.atomic.AtomicBoolean

import org.apache.commons.logging.LogFactory
import pe.enumeration.JobStatus
import pe.job.ProgramJob
import pe.model.ProgramStatusBean
import util._


object ProgramJobConsumer {

  val LOG = LogFactory.getLog(getClass)
  val programJobQueue = new ConcurrentLinkedQueue[ProgramJob]()
  val completedJobHistory = new ConcurrentHashMap[String, ProgramJob]()

  var currentThread: Thread = null
  var keepAlive = new AtomicBoolean(false)

  def start(): Unit = {
    LOG.info("Starting ProgramJobConsumer")
    currentThread = new Thread(new Runnable{
      override def run(): Unit = {
        while(true && keepAlive.get) {
          GlobalSync.get(this.getClass.getName).synchronized {
            val job = programJobQueue.peek()
            if(job != null) {
              LOG.info(s"Starting to execute job [${job.id}]")
              job.startTime = Option(new Date().getTime())
              execute(job)
              LOG.info(s"Job [${job.id}] is completed, poll it from the queue, current queue size is ${programJobQueue.size()}")
              completedJobHistory.put(job.id, job)
              programJobQueue.poll()
              job.endTime = Option(new Date().getTime())
            } else {
              Thread.sleep(500)
            }
          }
        }
      }
    })
    keepAlive.set(true)
    currentThread.start()
    LOG.info("ProgramJobConsumer started")
  }

  def isAlive(): Boolean = {
    keepAlive.get()
  }

  def stop(): Unit = {
    keepAlive.set(false)
  }

  def waitAndGet(jobId: String): ProgramJob = {
    val it = programJobQueue.iterator()
    while(it.hasNext) {
      val programJob = it.next()
      if(jobId.equals(programJob.id)) {
        return programJob
      }
    }
    throw new RuntimeException("Job with id [" + jobId + "] is not existed")
  }

  def getCompleted(jobId: String): Option[ProgramJob] = {
    val completedJob = completedJobHistory.get(jobId)
    if(completedJob != null) {
      Option(completedJob)
    } else {
      None
    }
  }

  def get(jobId: String): ProgramJob = {
    val completed = getCompleted(jobId)
    if(completed.isDefined) {
      return completed.get
    }
    val it = programJobQueue.iterator()
    while(it.hasNext) {
      val programJob = it.next()
      if(jobId.equals(programJob.id)) {
        return programJob
      }
    }
    throw new RuntimeException("Job with id [" + jobId + "] is not existed")
  }

  def add(programJob: ProgramJob): String = {
    if(!this.isAlive()) {
      this.start()
    }
    programJobQueue.add(programJob)
    LOG.info(s"ProgramJob with id [${programJob.id}] is added, current queue size is ${programJobQueue.size()}")
    programJob.id
  }

  def execute(programJob: ProgramJob): ProgramJob = {
    try {
      programJobQueue.peek()
      getClass.synchronized()
      makeFiles(programJob)
      compileFiles(programJob)
      executeClassFiles(programJob)
    } finally {
      deleteFiles(programJob)
    }
    programJob
  }

  private def makeFiles(programJob: ProgramJob): Unit = {
    deleteFiles(programJob)
    val file = new File(programJob.getBasePath())
    file.mkdirs()

    programJob.programs.foreach(p => {
      val pPath = programJob.getFilePath(p)
      val pFile = new File(pPath)

      var fileWriter: FileWriter = null
      try {
        fileWriter = new FileWriter(pFile)
        fileWriter.write(p.content)
        fileWriter.flush()
      } catch {
        case e: IOException => {
          LOG.error("Write file failed on path [" + pPath + "], name [" + p.name +  "], exception: " + e)
        }
      } finally {
        if(fileWriter != null) {
          fileWriter.close()
        }
      }
    })
  }

  private def deleteFiles(programJob: ProgramJob): Unit = {
    val file = new File(programJob.getBasePath())
    if(file.exists()) {
      FileUtils.deleteRecursive(file)
    }
  }

  private def compileFiles(programJob: ProgramJob): Unit = {
    val cmd = new StringBuilder(s"cd ${EnvProperty.get(EnvConstants.APPLICATION_TMP_BASE_PATH)} && javac -classpath .:")
    cmd.append(EnvProperty.get(EnvConstants.APPLICATION_LIB_PATH) + "/* ")
    programJob.programs.foreach(p => {
      cmd.append(programJob.getCompilePath(p) + " ")
    })
    LOG.info("Starting to compile java files: " + cmd.toString)
    val exitCode = ShellUtils.exec(cmd.toString)
    if(exitCode == 0) {
      LOG.info("Java files Compiled Successfully")
      programJob.programStatus = new ProgramStatusBean(JobStatus.PROCESSING, "Java Files Compiled")
    } else {
      throw new RuntimeException("Error occurred with exitcode [" + exitCode + "] when run java compile script: " + cmd.toString)
    }
  }

  private def executeClassFiles(programJob: ProgramJob): Unit = {
    val cmd = new StringBuilder(s"cd ${EnvProperty.get(EnvConstants.APPLICATION_TMP_BASE_PATH)} && java -cp .:")
    cmd.append(EnvProperty.get(EnvConstants.APPLICATION_LIB_PATH) + "/* ")
    val entryPointP = programJob.programs.find(_.isEntryPoint)
    if(entryPointP.isDefined) {
      if(programJob.isTestCase) {
        cmd.append("org.junit.platform.console.ConsoleLauncher --select-class " + programJob.getExecutePath(entryPointP.get) + " ")
      } else {
        cmd.append(programJob.getExecutePath(entryPointP.get) + " ")
      }
    } else {
      throw new RuntimeException("One Class should be defined as entry point")
    }
    LOG.info("Starting to execute classes: " + cmd.toString)
    val (exitCode, shellStdoutIterator) = if(programJob.isTestCase) {
      ShellUtils.execAndGetStdoutOnly(cmd.toString)
    } else {
      ShellUtils.execAndGetStdoutOrErrout(cmd.toString)
    }
    programJob.programStatus = parseToProgramStatus(programJob.isTestCase, exitCode: Int,  shellStdoutIterator)
  }

  private def parseToProgramStatus(isTestCase: Boolean, exitCode: Int, it: ShellStdoutIterator): ProgramStatusBean = {
    val message = new StringBuilder()
    val hasError = false
    var isFirst = true
    while(it.hasNext()) {
      val line = it.next()
      LOG.debug(line)
      if(isFirst) {
        message.append(line)
        isFirst = false
      } else {
        message.append("\n" + line)
      }
    }
    if(isTestCase) {
      val tmpMessage = message.toString()
      message.setLength(0)
      message.append(resolveExecutedLog(exitCode, tmpMessage))
    }
    if(hasError || exitCode != 0) {
      new ProgramStatusBean(JobStatus.FAILED, message.toString(), exitCode)
    } else {
      new ProgramStatusBean(JobStatus.PASS, message.toString(), exitCode)
    }
  }

  private val SUCCEED_MATCH_STRING = "Test execution started. Number of static tests:"
  private val FAILED_MATCH_STRING = "org.opentest4j."
  private def resolveExecutedLog(exitCode: Int, log: String): String = {
    if(exitCode == 0) {
      if(log.indexOf(SUCCEED_MATCH_STRING) != -1) {
        var passedTestsNum = log.substring(log.indexOf(SUCCEED_MATCH_STRING) + SUCCEED_MATCH_STRING.length + 1)
        passedTestsNum = passedTestsNum.substring(0, passedTestsNum.indexOf("\n"))
        "All passed tests: " + passedTestsNum
      } else {
        "All tests passed."
      }
    } else {
      var passedErrorLog = log.substring(log.indexOf(":", log.indexOf(FAILED_MATCH_STRING) + 1) + 2)
      passedErrorLog = passedErrorLog.substring(0, passedErrorLog.indexOf("\n"))
      passedErrorLog
    }
  }

}
