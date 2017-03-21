package pe.model

class ProgramStatusBean(var status: String, var message: String, var exitCode: Int) {

  def this(status: String, message: String) {
    this(status, message, -1)
  }

}
