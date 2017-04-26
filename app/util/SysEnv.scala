package util

import org.slf4j.LoggerFactory

object SysEnv {

  val LOG = LoggerFactory.getLogger(getClass)

  val PE_URL = "http://" + EnvProperty.get(EnvConstants.PE_SERVER_HOST) + ":" + EnvProperty.get(EnvConstants.PE_SERVER_PORT)


}