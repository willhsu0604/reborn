package pe

import org.apache.commons.logging.LogFactory

object PeClient {

  val LOG = LogFactory.getLog(getClass)

  def testHelloServletPost(): Unit = {

//    val pb = new ProgramBean("123", "456", true)
//
//    val url = "http://localhost:8080/hello"
//    val httpClient = HttpClientBuilder.create().build()
//    val request = new HttpPost(url)
//    val entityStr = new StringEntity(JsonUtils.toJson(pb))
//    request.setEntity(entityStr)
//    request.setHeader("Content-type", "application/json")
//    try {
//      val response = httpClient.execute(request)
//      if (response != null) {
//        val in = response.getEntity().getContent()
//        val str = convertStreamToString(in)
//        LOG.info(str)
//      }
//    } catch {
//      case e: HttpHostConnectException => {
//        LOG.error(s"Failed to create connection to [${url}]")
//        throw e
//      }
//    }


  }

}
