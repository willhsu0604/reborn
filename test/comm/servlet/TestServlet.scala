package comm.servlet

import annotation.RestName
import comm.HttpServletWrapper
import comm.model.TestBean

@RestName("test")
class TestServlet extends HttpServletWrapper[TestBean, TestBean] {

  override def doGet(params: Option[Map[String, String]]): TestBean = {
    val map = params.get
    new TestBean(map.get("name").get + "doGet:TestServlet", map.get("content").get, map.get("isEntryPoint").get.toBoolean)
  }

  override def doPost(e: Option[TestBean], params: Option[Map[String, String]]): TestBean = {
    new TestBean(e.get.name + "doPost:TestServlet", e.get.content, e.get.isEntryPoint)
  }

  override def doPut(e: Option[TestBean], params: Option[Map[String, String]]): TestBean = {
    new TestBean(e.get.name + "doPut:TestServlet", e.get.content, e.get.isEntryPoint)
  }

  override def doDelete(params: Option[Map[String, String]]): TestBean = {
    val map = params.get
    new TestBean(map.get("name").get + "doDelete:TestServlet", map.get("content").get, map.get("isEntryPoint").get.toBoolean)
  }
}