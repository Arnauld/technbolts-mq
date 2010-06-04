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
  val logger: Logger = LoggerFactory.getLogger(classOf[StorableIntrospector])

  val types = new HashMap[JClass[_], TypeDesc]

  def examine(klass: JClass[_]): TypeDesc = {
    types.get(klass) match {
      case Some(typeDesc) => typeDesc
      case None =>
        val typeDesc: TypeDesc = new TypeDesc(klass)
        // store it first in case of reentrant call of such class while
        // analysing its dependencies
        types.put(klass, typeDesc)
        // then fill it
        fillType(typeDesc)
        typeDesc
    }
  }

  def fillType(typeDesc: TypeDesc): Unit = {
    val klass: JClass[_] = typeDesc.klass

    // analyse its parent first...
    val parentDesc: TypeDesc = if (klass.getSuperclass != null)
      examine(klass.getSuperclass)
    else
      null

    logger.debug("Generating description for type {}", klass)

    if (klass.isPrimitive) {
      typeDesc.omitted = false
      typeDesc.serializer = klass match {
        case Int => new IntSerializer()
        case _ => throw new UnsupportedOperationException
      }
      return typeDesc
    }

    val pkg = klass.getPackage.getName
    val skippedPkg: Array[String] = Array("java", "sun", "com", "scala")
    if (skippedPkg.find {pkg.startsWith(_)}.isDefined) {
      logger.debug("Class belongs to a *system* package: " + pkg)
      return typeDesc
    }

    typeDesc.fields = klass.getDeclaredFields.map {
      f: JField =>
        generateField(f)
    }

    typeDesc
  }

  def generateField(field: JField): FieldDesc = {
    logger.debug("Examining field <{}>: {}", field.getName, field)

    val fd: FieldDesc = new FieldDesc(field)
    fd.omitted = field.getAnnotation(classOf[Property]) == null

    traceFieldTypes(field)

    if (field.getType.isArray) {
      val itemClass = field.getType.getComponentType

      logger.debug("Field <{}> considered as Array of {}", field.getName, itemClass)
      fd.mode = FieldDesc.Array
      fd.itemType = examine(itemClass)
    }
    else if (classOf[java.util.Collection[_]].isAssignableFrom(field.getType)) {
      lookupItemType(field) match {
        case Some(itemClass: JClass[_]) =>
          logger.debug("Field <{}> considered as <java/Collection> of {}", field.getName, itemClass)
          fd.mode = FieldDesc.JavaCollection
          fd.itemType = examine(itemClass)
        case None =>
          logger.warn("Field <{}> considered as <java/Collection> but no type could be retrieved, field will not be persisted. Field: {}", field.getName, field)
      }
    }
    else if (classOf[scala.Iterable[_]].isAssignableFrom(field.getType)) {
      lookupItemType(field) match {
        case Some(itemClass: JClass[_]) =>
          logger.debug("Field <{}> considered as <scala/Iterable> of {}", field.getName, itemClass)
          fd.mode = FieldDesc.ScalaCollection
          fd.itemType = examine(itemClass)
        case None =>
          logger.warn("Field <{}> considered as <scala/Iterable> but no type could be retrieved, field will not be persisted. Field: {}", field.getName, field)
      }
    }
    else {
      val itemClass: JClass[_] = field.getType
      logger.debug("Field <{}> considered as direct value of {}", field.getName, itemClass)
      fd.mode = FieldDesc.Direct
      fd.itemType = examine(itemClass)
    }

    fd
  }

  def traceFieldTypes(field: JField): Unit = {
    if (!logger.isTraceEnabled)
      return;

    logger.trace("Field <{}> type............: {}", field.getName, field.getType)
    logger.trace("Field <{}> generic type....: {}", field.getName, field.getGenericType)
    logger.trace("Field <{}> class...........: {}", field.getName, field.getGenericType.getClass)
    field.getGenericType match {
      case p: ParameterizedType =>
        logger.trace("Field <{}> arguments.......: {}", field.getName, field.getGenericType.asInstanceOf[ParameterizedType].getActualTypeArguments)
      case _ => //nothing special
    }
    logger.trace("Field <{}> component type..: {}", field.getName, field.getType.getComponentType)
  }

  def lookupItemType(field: JField): Option[JClass[_]] = field.getGenericType() match {
    case t: ParameterizedType =>
      val typeArg = t.getActualTypeArguments()(0)
      Some(getJClass(typeArg))
    case _ => None
  }

  def getJClass(typeArg: Type): JClass[_] = {
    typeArg match {
      case p: ParameterizedType => p.getRawType.asInstanceOf[JClass[_]]
      case k: JClass[_] => k
      case _ => null
    }
  }
}

class TypeDesc(val klass: java.lang.Class[_]) {
  var fields: Array[FieldDesc] = _
  var omitted = false
  var serializer:Serializer[_] = _
}

object FieldDesc {
  val Direct = 1
  val ScalaCollection = 10
  val JavaCollection = 11
  val Array = 20
}

class FieldDesc(val field: JField) {
  var omitted = false
  var itemType: TypeDesc = _
  var mode = FieldDesc.Direct
  var serializer:Serializer[_] = _
}

trait BytesWriter {
  def writeInt(v:Int):BytesWriter
}

trait BytesReader {
  def readInt():Int
}

trait Serializer[A] {
  def write(x:A, writer:BytesWriter):Unit
  def read (reader:BytesReader):A
}

class IntSerializer extends Serializer[Int] {
  override def write(x:Int, writer:BytesWriter):Unit = { writer.writeInt(x) }
  override def read(reader:BytesReader):Int = { reader.readInt }
}
