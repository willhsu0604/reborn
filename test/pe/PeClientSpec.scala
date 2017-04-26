package pe

import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import pe.enumeration.JobStatus
import pe.job.ProgramJob
import pe.model.ProgramBean

@RunWith(classOf[JUnitRunner])
class PeClientSpec extends Specification {

  trait Context extends BeforeAfter {
    def before: Any = {
      new Thread(new Runnable {
        override def run(): Unit = {
          PeServer.main(null)
        }
      }).start()
      while(!PeServer.isStarted()) {
        Thread.sleep(1000)
      }
    }
    def after: Any = {
      PeServer.stop()
    }
  }

  "PeClient" should {

    "send a programJob to Server, get jobId, and use id to get jobStatus" in new Context {
      val pb1 = new ProgramBean("TestClass", "package program.other;\n\npublic class TestClass {\n\n    public static void main(String[] args) {\n        System.out.print(\"tttttttttt\");\n    }\n}", true)
      val pbs = List[ProgramBean](pb1)
      val pj = new ProgramJob("other", pbs, false)
      val jobId = PeClient.addProgramJob(pj)
      val returnPj = PeClient.getProgramJob(jobId, true)
      returnPj.programStatus.exitCode must equalTo(0)
      returnPj.programStatus.message must equalTo("tttttttttt")
      returnPj.programStatus.status must equalTo(JobStatus.PASS)
    }

  }

}
