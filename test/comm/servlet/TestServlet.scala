package comm.servlet

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import annotation.RestName
import comm.HttpServletWrapper
import comm.model.TestBean

@RestName("test")
class TestServlet extends HttpServletWrapper {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val params = parseRequestParams(req)
    val map = params.get
    returnResponseJson(resp, new TestBean(map.get("name").get + "doGet:TestServlet", map.get("content").get, map.get("isEntryPoint").get.toBoolean))
  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val e = parseRequestBody[TestBean](req)
    returnResponseJson(resp, new TestBean(e.get.name + "doPost:TestServlet", e.get.content, e.get.isEntryPoint))
  }

  override def doPut(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val e = parseRequestBody[TestBean](req)
    returnResponseJson(resp, new TestBean(e.get.name + "doPut:TestServlet", e.get.content, e.get.isEntryPoint))
  }

  override def doDelete(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val params = parseRequestParams(req)
    val map = params.get
    returnResponseJson(resp, new TestBean(map.get("name").get + "doDelete:TestServlet", map.get("content").get, map.get("isEntryPoint").get.toBoolean))
  }

}