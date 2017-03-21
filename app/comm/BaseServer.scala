package comm

import java.net.InetSocketAddress

import annotation.RestName
import org.apache.commons.logging.LogFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.reflections.Reflections

import scala.collection.JavaConversions._

trait BaseServer {

  val LOG = LogFactory.getLog(getClass)
  val PACKAGE_NAME = "servlet"
  var server: Server = null

  def start(host: String, port: Int): Unit = {
    val serverName = getClass.getSimpleName
    LOG.info(s"Starting [${serverName}] with address [${host}:${port}]")
    val address = new InetSocketAddress(host, port)
    server = new Server(address)
    val handler = new ServletContextHandler(server, "/")
    server.setHandler(handler)
    val basePackage = getClass.getName.substring(0, getClass.getName.lastIndexOf(".") + 1)
    val reflections = new Reflections(basePackage + PACKAGE_NAME)
    val classes = reflections.getSubTypesOf(new HttpServletWrapper().getClass)
    classes.foreach(cls => {
      val restName = cls.getDeclaredAnnotation(classOf[RestName])
      handler.addServlet(cls, "/" + restName.value())
      LOG.info(s"Servlet [${cls.getSimpleName}] is added with rest path [${restName.value()}]")
    })
    server.start()
    server.join()
    LOG.info(s"[${serverName}] is started with address [${host}:${port}]")
  }

  def stop(): Unit = {
    if(server!= null) {
      val serverName = getClass.getSimpleName
      LOG.info(s"Shutting down [${serverName}] ")
      server.stop()
      LOG.info(s"[${serverName}] is shut down")
    }
  }

  def isStarted(): Boolean = {
    server != null && server.isStarted
  }

}
