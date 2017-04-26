package pe

import comm.HttpRequestHelper
import org.apache.commons.logging.LogFactory
import pe.job.ProgramJob
import pe.servlet.ProgramServlet
import util.{RestNameUtils, SysEnv}

object PeClient {

  val LOG = LogFactory.getLog(getClass)

  def addProgramJob(programJob: ProgramJob): String = {
    val jobId = HttpRequestHelper.doPut[ProgramJob, String](SysEnv.PE_URL + "/" + RestNameUtils.value(classOf[ProgramServlet]),  programJob)
    if(jobId.isDefined) {
      jobId.get
    } else {
      throw new RuntimeException(s"Not jobId is returned")
    }
  }

  def getProgramJob(jobId: String, isCompleted: Boolean): ProgramJob = {
    val pj = HttpRequestHelper.doGet[ProgramJob](SysEnv.PE_URL + "/" + RestNameUtils.value(classOf[ProgramServlet]) + "?jobId=" + jobId + "&isCompleted=" + isCompleted)
    if(pj.isDefined) {
      pj.get
    } else {
      throw new RuntimeException(s"Job with id [${jobId}] is not found")
    }
  }

}
