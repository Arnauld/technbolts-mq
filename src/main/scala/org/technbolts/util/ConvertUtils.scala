package org.technbolts.util

/**
 *
 */
object ConvertUtils {
  def toObjectArray(values:Any*) = values.map(_.asInstanceOf[Object]).toArray
}