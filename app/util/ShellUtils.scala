package util

import java.io._

import org.slf4j.LoggerFactory

import scala.sys.process._

object ShellUtils {

  private val LOG = LoggerFactory.getLogger(getClass)

  def exec(script: String): Int = {
    val path = "/tmp/reborn-script-" + System.currentTimeMillis() + "-" + System.nanoTime() + ".sh"
    try {
      new PrintWriter(path) {
        write(script)
        close
      }

      ("chmod 777 " + path) !

      val cmd = path
      Process(cmd).run(new ProcessIO(stdin => {}, stdout => {}, stderr => {})).exitValue()
    } finally {
      new File(path).delete()
    }
  }

  def execAndGetStdoutOnly(script: String): (Int, ShellStdoutIterator) = {
    execAndGetOut(script, true)
  }

  def execAndGetStdoutOrErrout(script: String): (Int, ShellStdoutIterator) = {
    execAndGetOut(script, false)
  }

  def execAndGetOut(script: String, isStdoutOnly: Boolean): (Int, ShellStdoutIterator) = {
    getClass.synchronized {
      val path = "/tmp/reborn-script-" + System.currentTimeMillis() + "-" + System.nanoTime() + ".sh"

      new PrintWriter(path) {
        write(script)
        close
      }

      ("chmod 777 " + path) !

      val cmd = path
      @volatile
      var passOut: InputStream = null
      @volatile
      var errorOut: InputStream = null
      var it: Iterator[String] = null

      val processIO = new ProcessIO(
        stdin => {
          stdin.close()
        },
        stdout => {
          passOut = stdout
        },
        stderr => {
          errorOut = stderr
        }
      )
      val proc = Process(cmd).run(processIO)
      val exitCode = proc.exitValue()
      val out = if(isStdoutOnly || exitCode == 0) {
        errorOut.close()
        passOut
      }  else {
        passOut.close()
        errorOut
      }

      //wait until output is ready
      for(i<-0 to 100 if (out == null)){
        Thread.sleep(100)
      }

      it = scala.io.Source.fromInputStream(out).getLines()

      try {

        (exitCode, new ShellStdoutIterator(it) {

          override def eof(): Unit = {

            try {
              out.close()
            } catch {
              case ex: Exception =>
            }

            try{
              proc.destroy()
            } catch{
              case ex:Exception =>
            }

            val pids = ProcessUtils.getPidsByPattern(path)

            //try kill local user
            pids.foreach(x => {
              ProcessUtils.killPidWithChildren(x)
            })
            new File(path).delete()
          }
        })
      } catch {
        case ex: Exception => {
          throw new RuntimeException("Error occurred when parsed stdout or stderror from running script: " + cmd)
        }
      }
    }
  }
}