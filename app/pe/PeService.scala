package pe

import pe.job.ProgramJob
import play.api.mvc.{Action, Controller}
import util.JsonUtils

object PeService extends Controller {

  def addProgramJob = Action {
    request => {
      val programJob = JsonUtils.fromJson[ProgramJob](request.body.asJson.get.toString())
      val jobId = PeClient.addProgramJob(programJob)
      Ok(jobId)
    }
  }

  def getProgramJob(jobId: String, isCompleted: Boolean) = Action {
    val programJob = PeClient.getProgramJob(jobId, isCompleted)
    Ok(JsonUtils.toJson(programJob))
  }

}
