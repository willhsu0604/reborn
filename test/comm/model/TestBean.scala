package comm.model

class TestBean(var name: String, var content: String, var isEntryPoint: Boolean) {
  def this(name: String, content: String) {
    this(name, content, false)
  }
}
