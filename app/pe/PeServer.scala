package pe

import comm.BaseServer
import util.{EnvConstants, EnvProperty}

object PeServer extends BaseServer {

  def main(args: Array[String]): Unit = {
    this.start(EnvProperty.get(EnvConstants.PE_SERVER_HOST), EnvProperty.get(EnvConstants.PE_SERVER_PORT).toInt)
  }

  override def stop(): Unit = {
    super.stop()
    ProgramJobConsumer.stop()
  }
}
