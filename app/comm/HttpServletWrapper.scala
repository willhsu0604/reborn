package comm

import java.io.IOException
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import org.apache.commons.logging.LogFactory
import util.JsonUtils

import scala.collection.mutable

class HttpServletWrapper[IN: Manifest, OUT: Manifest] extends HttpServlet {

  val LOG = LogFactory.getLog(getClass)

  def doGet(params: Option[Map[String, String]]): OUT = {
    throw new UnsupportedOperationException("Method [doGet] must be override")
  }

  def doPost(in: Option[IN], params: Option[Map[String, String]]): OUT = {
    throw new UnsupportedOperationException("Method [doPost] must be override")
  }

  def doPut(in: Option[IN], params: Option[Map[String, String]]): OUT = {
    throw new UnsupportedOperationException("Method [doPut] must be override")
  }

  def doDelete(params: Option[Map[String, String]]): OUT = {
    throw new UnsupportedOperationException("Method [doDelete] must be override")
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val params = parseRequestParams(req)
    val outObj = doGet(params)
    returnResponseJson(resp, outObj)
  }

  override def doPut(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val params = parseRequestParams(req)
    val outObj = doPut(parseRequestBody(req), params)
    returnResponseJson(resp, outObj)
  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val params = parseRequestParams(req)
    val outObj = doPost(parseRequestBody(req), params)
    returnResponseJson(resp, outObj)
  }

  override def doDelete(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val params = parseRequestParams(req)
    val outObj = doDelete(params)
    returnResponseJson(resp, outObj)
  }

  private def parseRequestParams(req: HttpServletRequest): Option[Map[String, String]] = {
    val queryParameters = mutable.Map[String, String]()
    val queryString = req.getQueryString()
    if (queryString != null && queryString.trim.length > 0) {
      val parameters = queryString.split("&")
      parameters.foreach(p => {
        val keyValuePair = p.split("=")
        val value = if(keyValuePair.length == 1) "" else keyValuePair(1)
        queryParameters += (keyValuePair(0) -> value)
      })
      Option(queryParameters.toMap)
    } else {
      None
    }
  }

  private def parseRequestBody(req: HttpServletRequest): Option[IN] = {
    val jb = new StringBuffer()
    var line: String = null
    try {
      val reader = req.getReader()
      line = reader.readLine()
      while(line != null) {
        jb.append(line)
        line = reader.readLine()
      }
    } catch {
      case e: IOException => {
        LOG.error("Unable to read data from http request, e: " + e)
      }
    }

    LOG.info(jb.toString)

    if(jb.length() > 0) {
      try {
        Option(JsonUtils.fromJson[IN](jb.toString))
      } catch {
        case e: Throwable => {
          throw new RuntimeException("Unable to parse to an input object from json string [" + jb.toString + "], e: " + e)
        }
      }
    } else {
      None
    }
  }

  private def returnResponseJson(resp: HttpServletResponse, outObj: OUT): Unit = {
    resp.setContentType("application/json; charset=utf-8")
    resp.setStatus(HttpServletResponse.SC_OK)
    val out = resp.getWriter()
    out.print(JsonUtils.toJson(outObj))
  }
}
