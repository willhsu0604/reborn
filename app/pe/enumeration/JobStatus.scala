package pe.enumeration

object JobStatus extends Enumeration {
  type Key = String

  val PASS = "pass"
  val FAILED = "failed"
  val WAITING = "waiting"
  val PROCESSING = "processing"

}
