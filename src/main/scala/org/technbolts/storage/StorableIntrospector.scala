package org.technbolts.storage

import annotation.Property
import java.lang.{Class => JClass}
import org.slf4j.{Logger, LoggerFactory}
import collection.mutable.{HashMap, ListBuffer}
import java.lang.reflect.{Type, ParameterizedType, Field => JField}

/**
 *
 *
 */
class StorableIntrospector {
  val logger:Logger = LoggerFactory.getLogger(classOf[StorableIntrospector])

  val types = new HashMap[JClass[_],TypeDesc]

  def examine(klass : JClass[_]):TypeDesc = {
    types.get(klass) match {
      case Some(typeDesc) => typeDesc
      case None =>
        val typeDesc = generateType(klass)
        types.put(klass, typeDesc)
        typeDesc
    }
  }

  def generateType(klass : JClass[_]):TypeDesc = {
    val typeDesc:TypeDesc = new TypeDesc(klass)

    logger.debug("Generating type {}", klass)

    if(klass.isPrimitive) {
      typeDesc.omitted = false
      //typeDesc.serializer = getPrimitiveSerializer
      return typeDesc
    }

    val pkg = klass.getPackage.getName
    if(pkg.startsWith("java")) {

      return typeDesc
    }

    val skippedPkg:Array[String] = Array("sun","com")
    if(skippedPkg.forall { pkg.startsWith(_) }) {
      logger.debug("Class belongs to an unsupported package: "+pkg)
      return typeDesc
    }

    typeDesc.fields = klass.getDeclaredFields.map { f:JField =>
      generateField(f)
    }

    typeDesc
  }

  def generateField(field:JField): FieldDesc = {
    logger.debug("Examining field {}", field)

    val fd: FieldDesc = new FieldDesc(field)
    fd.omitted = field.getAnnotation(classOf[Property])==null

    if(field.getType.isArray) {
      val itemClass = field.getType.getComponentType

      logger.debug("Field considered as Array of {}", itemClass)
      fd.mode = FieldDesc.Array
      fd.itemType = examine(itemClass)
    }
    else if(classOf[java.util.Collection[_]].isAssignableFrom(field.getType)) {
      val itemClass = getJClass(field.getGenericType)

      logger.debug("Field considered as Collection of {}", itemClass)
      fd.mode = FieldDesc.Collection
      fd.itemType = examine(itemClass)
    }
    else {
      val itemClass = field.getGenericType() match {
        case p:ParameterizedType =>
          val typeArg = p.getActualTypeArguments()(0)
          getJClass(typeArg)
        case _ =>
          getJClass(field.getType)
      }

      logger.debug("Field considered as direct value of {}", itemClass)
      fd.mode = FieldDesc.Direct
      fd.itemType = examine(itemClass)
    }

    fd
  }

  def getJClass(typeArg: Type):JClass[_] = {
    typeArg match {
      case p:ParameterizedType => p.getRawType.asInstanceOf[JClass[_]]
      case k:JClass[_] => k
      case _ => null
    }
  }
}

class TypeDesc(val klass : java.lang.Class[_]) {
  var fields:Array[FieldDesc] = _
  var omitted = false

}

object FieldDesc {
  val Direct = 1
  val Collection = 2
  val Array = 3
}

class FieldDesc(val field: JField) {
  var omitted = false
  var itemType: TypeDesc = _
  var mode = FieldDesc.Direct

}