import comm.HttpRequestHelper
import comm.model.TestBean
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._

@RunWith(classOf[JUnitRunner])
class HttpRequestServletHandleSpec extends Specification {

  val HOST = "localhost"
  val PORT = 8181

  trait Context extends BeforeAfter {
    def before: Any = {
      new Thread(new Runnable {
        override def run(): Unit = {
          comm.TestServer.start(HOST, 8181)
        }
      }).start()
      while(!comm.TestServer.isStarted()) {
        Thread.sleep(1000)
      }
    }
    def after: Any = {
      comm.TestServer.stop()
    }
  }


  "HttpRequestHelper" should {

    "send a POST request with entity to TestServer and return a response entity" in new Context {
      val testBean = new TestBean("1", "2", true)
      val result = HttpRequestHelper.doPost[TestBean, TestBean]("http://localhost:8181/test", testBean)
      result.get.name must beEqualTo(testBean.name + "doPost:TestServlet")
      result.get.content must beEqualTo(testBean.content)
      result.get.isEntryPoint must beEqualTo(testBean.isEntryPoint)
    }

    "send a PUT request with entity to TestServer and return a response entity" in new Context {
      val testBean = new TestBean("1", "2", true)
      val result = HttpRequestHelper.doPut[TestBean, TestBean]("http://localhost:8181/test", testBean)
      result.get.name must beEqualTo(testBean.name + "doPut:TestServlet")
      result.get.content must beEqualTo(testBean.content)
      result.get.isEntryPoint must beEqualTo(testBean.isEntryPoint)
    }

    "send a GET request with map to TestServer and return a response entity" in new Context {
      val result = HttpRequestHelper.doGet[TestBean]("http://localhost:8181/test?name=1&content=2&isEntryPoint=true")
      result.get.name must beEqualTo("1doGet:TestServlet")
      result.get.content must beEqualTo("2")
      result.get.isEntryPoint must beEqualTo(true)
    }

    "send a DELETE request with map to TestServer and return a response entity" in new Context {
      val result = HttpRequestHelper.doDelete[TestBean]("http://localhost:8181/test?name=1&content=2&isEntryPoint=true")
      result.get.name must beEqualTo("1doDelete:TestServlet")
      result.get.content must beEqualTo("2")
      result.get.isEntryPoint must beEqualTo(true)
    }

  }
}
