package util

class ShellStdoutIterator(it:Iterator[String]){

  def hasNext():Boolean={
    it.hasNext
  }

  def next():String={
    it.next()
  }

  def eof():Unit={
  }
}