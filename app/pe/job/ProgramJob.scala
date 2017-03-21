package pe.job

import java.util.Date

import pe.enumeration.JobStatus
import pe.model.{ProgramBean, ProgramStatusBean}
import util.{EnvConstants, EnvProperty}

class ProgramJob(var module: String, var programs: List[ProgramBean], var isTestCase: Boolean, var programStatus: ProgramStatusBean) {

  var id = java.util.UUID.randomUUID.toString.substring(0, 8)
  var initTime: Option[Long] = Option(new Date().getTime)
  var startTime: Option[Long] = None
  var endTime: Option[Long] = None

  def this(module: String, programs: List[ProgramBean], isTestCase: Boolean) {
    this(module, programs, isTestCase, new ProgramStatusBean(JobStatus.WAITING, null))
  }

  def getBasePath(): String = {
    EnvProperty.get(EnvConstants.PE_BASE_PATH) + "/" + module
  }

  def getCompilePath(pb: ProgramBean): String = {
    EnvProperty.get(EnvConstants.PE_BASE_PACKAGE_NAME) + "/" + module + "/" + pb.name + ".java"
  }

  def getExecutePath(pb: ProgramBean): String = {
    EnvProperty.get(EnvConstants.PE_BASE_PACKAGE_NAME) + "." + module + "." + pb.name
  }

  def getFilePath(pb: ProgramBean): String = {
    getBasePath() + "/" + pb.name + ".java"
  }

}
