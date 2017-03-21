package util

object EnvConstants extends Enumeration {
  type Key = Value

  val APPLICATION_TMP_BASE_PATH = Value("application.tmp.base.path")
  val APPLICATION_LIB_PATH = Value("application.lib.path")
  val PE_BASE_PATH = Value("pe.base.path")
  val PE_BASE_PACKAGE_NAME = Value("pe.base.package.name")
  val PE_SERVER_HOST = Value("pe.server.host")
  val PE_SERVER_PORT = Value("pe.server.port")

}