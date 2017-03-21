package pe

import comm.BaseServer
import util.{EnvConstants, EnvProperty}

object peServer extends BaseServer {

  def main(args: Array[String]): Unit = {
    this.start(EnvProperty.get(EnvConstants.PE_SERVER_HOST), EnvProperty.get(EnvConstants.PE_SERVER_PORT).toInt)
  }

}
