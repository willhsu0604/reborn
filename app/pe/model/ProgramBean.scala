package pe.model

class ProgramBean(var name: String, var content: String, var isEntryPoint: Boolean) {
  def this(name: String, content: String) {
    this(name, content, false)
  }
}
