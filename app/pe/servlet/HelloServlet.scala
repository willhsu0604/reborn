package pe.servlet

import annotation.RestName
import pe.model.ProgramBean
import comm.HttpServletWrapper

@RestName("hello")
class HelloServlet extends HttpServletWrapper {

//  override def doGet(params: Map[String, String]): ProgramBean = {
//    new ProgramBean(e.name + "doGet:HelloServlet", e.content, e.isEntryPoint)
//  }
//
//  override def doPost(e: ProgramBean): ProgramBean = {
//    new ProgramBean(e.name + "doPost:HelloServlet", e.content, e.isEntryPoint)
//  }
//
//  override def doPut(e: ProgramBean): ProgramBean = {
//    new ProgramBean(e.name + "doPut:HelloServlet", e.content, e.isEntryPoint)
//  }
//
//  override def doDelete(e: ProgramBean): ProgramBean = {
//    new ProgramBean(e.name + "doDelete:HelloServlet", e.content, e.isEntryPoint)
//  }
}
