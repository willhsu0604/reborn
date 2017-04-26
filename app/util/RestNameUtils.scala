package util

import annotation.RestName

object RestNameUtils {

  def value[T](clazz: Class[T]): String = {
    val restNameAnno = clazz.getDeclaredAnnotation(classOf[RestName])
    if(restNameAnno != null) {
      restNameAnno.value()
    } else {
      throw new RuntimeException("Class Must be declared with annotation @" + classOf[RestName].getSimpleName)
    }
  }

}
