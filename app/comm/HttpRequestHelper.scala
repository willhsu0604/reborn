package comm

import java.io.InputStream

import org.apache.commons.logging.LogFactory
import org.apache.http.client.methods.{HttpDelete, HttpGet, HttpPost, HttpPut}
import org.apache.http.conn.HttpHostConnectException
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import util.JsonUtils

object HttpRequestHelper {

  object REQUEST_METHOD extends Enumeration {
    type Key = Value
    val GET = Value("GET")
    val POST = Value("POST")
    val PUT = Value("PUT")
    val DELETE = Value("DELETE")
  }

  val LOG = LogFactory.getLog(getClass)

  def doGet[OUT: Manifest](url: String): Option[OUT] = {
    send[OUT](url, REQUEST_METHOD.GET)
  }

  def doDelete[OUT: Manifest](url: String): Option[OUT] = {
    send[OUT](url, REQUEST_METHOD.DELETE)
  }

  def doPost[IN: Manifest, OUT: Manifest](url: String, in: IN): Option[OUT] = {
    sendWithEntity[IN, OUT](url, in, REQUEST_METHOD.POST)
  }

  def doPut[IN: Manifest, OUT: Manifest](url: String, in: IN): Option[OUT] = {
    sendWithEntity[IN, OUT](url, in, REQUEST_METHOD.PUT)
  }

  def sendWithEntity[IN: Manifest, OUT: Manifest](url: String, in: IN, method: REQUEST_METHOD.Value): Option[OUT] = {
    val httpClient = HttpClientBuilder.create().build()
    val request =  if(method.equals(REQUEST_METHOD.POST)) {
      new HttpPost(url)
    } else if (method.equals(REQUEST_METHOD.PUT)) {
      new HttpPut(url)
    } else {
      throw new RuntimeException(s"The request method send with Entity must be specified as [${REQUEST_METHOD.POST}] or [${REQUEST_METHOD.PUT}]")
    }

    val entityStr = new StringEntity(JsonUtils.toJson(in))
    request.setEntity(entityStr)
    request.setHeader("Content-Type", "application/json")
    try {
      val response = httpClient.execute(request)
      if (response != null) {
        val in = response.getEntity().getContent()
        Option(JsonUtils.fromJson[OUT](convertStreamToString(in)))
      } else {
        None
      }
    } catch {
      case e: HttpHostConnectException => {
        LOG.error(s"Failed to create connection to [${url}]")
        throw e
      }
    }
  }

  def send[OUT: Manifest](url: String, method: REQUEST_METHOD.Value): Option[OUT] = {
    val httpClient = HttpClientBuilder.create().build()
    val request = if (method.equals(REQUEST_METHOD.DELETE)) {
      new HttpDelete(url)
    } else {
      new HttpGet(url)
    }
    request.setHeader("Content-Type", "application/json")
    try {
      val response = httpClient.execute(request)
      if (response != null) {
        val in = response.getEntity().getContent()
        Option(JsonUtils.fromJson[OUT](convertStreamToString(in)))
      } else {
        None
      }
    } catch {
      case e: HttpHostConnectException => {
        LOG.error(s"Failed to create connection to [${url}]")
        throw e
      }
    }
  }

  def convertStreamToString(is: InputStream): String = {
    val s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A")
    return if(s.hasNext()) s.next() else ""
  }

}
