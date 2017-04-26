package pe.servlet

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import annotation.RestName
import comm.HttpServletWrapper
import pe.ProgramJobConsumer
import pe.job.ProgramJob

@RestName("program")
class ProgramServlet extends HttpServletWrapper {


  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val params = parseRequestParams(req)
    val map = params.get
    val jobId = map.get("jobId")
    val job = if(jobId.isDefined) {
      val isCompleted = map.get("isCompleted").getOrElse("false").toBoolean
      if(isCompleted) {
        var job = ProgramJobConsumer.getCompleted(jobId.get)
        while(job.isEmpty) {
          Thread.sleep(1000)
          job = ProgramJobConsumer.getCompleted(jobId.get)
        }
        job
      } else {
        ProgramJobConsumer.get(jobId.get)
      }
    } else {
      throw new RuntimeException("The param [jobId] should be passed in")
    }
    returnResponseJson(resp, job)
  }

  override def doPut(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val job = parseRequestBody[ProgramJob](req)
    val jobId = if(job.isDefined) {
      ProgramJobConsumer.add(job.get)
    } else {
      throw new RuntimeException("Input entity is empty")
    }
    returnResponseJson(resp, jobId)
  }

}
