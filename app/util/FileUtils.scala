package util

import java.io.File

object FileUtils {

  def deleteRecursive(file: File): Unit = {
    val contents = file.listFiles()
    if (contents != null) {
      for (f <- contents) {
        deleteRecursive(f)
      }
    }
    file.delete()
  }

}
