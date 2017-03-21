package program

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import pe.ProgramJobHandler
import pe.enumeration.JobStatus
import pe.job.ProgramJob
import pe.model.ProgramBean

@RunWith(classOf[JUnitRunner])
class ProgramHandlerSpec extends Specification {

  "ProgramHandler" should {

    "run with TestClass main method, then return ok and executed result" in {
      val pb1 = new ProgramBean("TestClass", "package program.other;\n\npublic class TestClass {\n\n    public static void main(String[] args) {\n        System.out.print(\"tttttttttt\");\n    }\n}", true)
      val pbs = List[ProgramBean](pb1)
      val pj = ProgramJobHandler.execute(new ProgramJob("other", pbs, false))
      pj.programStatus.exitCode must equalTo(0)
      pj.programStatus.message must equalTo("tttttttttt")
      pj.programStatus.status must equalTo(JobStatus.PASS)
    }

    "run with FirstTest class, then return true and test result" in {
      val pb1 = new ProgramBean("FirstTest", "package program.firstTest;\n\nimport org.junit.jupiter.api.DisplayName;\nimport org.junit.jupiter.api.Test;\n\nimport static org.junit.jupiter.api.Assertions.assertEquals;\n\nclass FirstTest {\n\n    private TestExecutable testExecutable = new TestExecutable();\n\n    @Test\n    @DisplayName(\"TestExecutable.testMethod pass in 123 should return 123123\")\n    void testMethod() {\n        String s = testExecutable.append123(\"123\");\n        assertEquals(s, \"123123\");\n    }\n\n}", true)
      val pb2 = new ProgramBean("TestExecutable", "package program.firstTest;\n\npublic class TestExecutable {\n\n    public String append123(String value) {\n        return value + \"123\";\n    }\n\n}")
      val pbs = List[ProgramBean](pb1, pb2)
      var pj = ProgramJobHandler.execute(new ProgramJob("firstTest", pbs, true))
      pj.programStatus.exitCode must equalTo(0)
      pj.programStatus.message must equalTo("All passed tests: 1")
      pj.programStatus.status must equalTo(JobStatus.PASS)
    }

    "run with SecondTest class, then return false and test error log" in {
      val pb1 = new ProgramBean("SecondTest", "package program.secondTest;\n\nimport org.junit.jupiter.api.DisplayName;\nimport org.junit.jupiter.api.Test;\n\nimport static org.junit.jupiter.api.Assertions.assertEquals;\n\nclass SecondTest {\n\n    private TestExecutable testExecutable = new TestExecutable();\n\n    @Test\n    @DisplayName(\"Test if true holds\")\n    void testMethod() {\n        String s = testExecutable.append123(\"123\");\n        assertEquals(s, \"123123\");\n    }\n\n}", true)
      val pb2 = new ProgramBean("TestExecutable", "package program.secondTest;\n\npublic class TestExecutable {\n\n    public String append123(String value) {\n        return value + \"1231\";\n    }\n\n}")
      val pbs = List[ProgramBean](pb1, pb2)
      var pj = ProgramJobHandler.execute(new ProgramJob("secondTest", pbs, true))
      pj.programStatus.exitCode must equalTo(1)
      pj.programStatus.message must equalTo("expected: <1231231> but was: <123123>")
      pj.programStatus.status must equalTo(JobStatus.FAILED)
    }

  }

}
