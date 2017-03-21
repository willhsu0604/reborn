package util

import scala.sys.process._

object ProcessUtils {

  val unixProcessClass=Class.forName("java.lang.UNIXProcess")
  val pidField=unixProcessClass.getDeclaredField("pid")
  pidField.setAccessible(true)

  def getPidsByPattern(pattern:String): List[Long] ={
    var result=""
    try{
      val lines=("ps aux") !!

      lines.split("\n").foreach(x=>{
        if(!x.contains("grep") && x.contains(pattern)){
          if(result.length>0){
            result=result+","
          }
          result=result+x.split("\\s+")(1).toLong
        }
      })
    }catch{
      case ex:Exception=>
    }
    result.split(",").map(x=>x.trim).filter(x=>x.length>0).map(x=>x.toLong).toList
  }

  def killPidWithChildren(pid:Long):Unit={
    try{
      val children=("pgrep -P "+pid) !!

      children.trim.split("\n").foreach(x=>killPidWithChildren(x.toLong))
    }catch{
      case ex:Exception=>
    }
    try{
      ("kill -9 "+pid) !
    }catch{
      case ex:Exception=>
    }
  }
}
