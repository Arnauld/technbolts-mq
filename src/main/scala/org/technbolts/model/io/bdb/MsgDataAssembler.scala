package org.technbolts.model.io.bdb

import com.sleepycat.bind.tuple.{TupleOutput, TupleInput, TupleBinding}
import org.springframework.beans.factory.annotation.{Qualifier, Autowired}
import org.technbolts.model._
import java.util.Date

/**
 *
 */
object MsgDataAssembler {
  val version: Int = 1
}

/**
 *
 */
class MsgDataAssembler extends TupleBinding[MsgData] {

  var hasTagsAssembler:HasTagsAssembler = null
  @Autowired
  @Qualifier("hasTagAssembler")
  def setHasTagsAssembler(hasTagsAssembler:HasTagsAssembler) = this.hasTagsAssembler = hasTagsAssembler

  var hasPartsAssembler:HasPartsAssembler = null
  @Autowired
  @Qualifier("hasPartsAssembler")
  def setHasPartsAssembler(hasPartsAssembler:HasPartsAssembler) = this.hasPartsAssembler = hasPartsAssembler


  def objectToEntry(data: MsgData, output: TupleOutput): Unit = {
    output.writeInt(MsgDataAssembler.version)
    hasTagsAssembler.objectToEntry(data, output)
    hasPartsAssembler.objectToEntry(data, output)
  }

  def entryToObject(input: TupleInput): MsgData = {
    val version = input.readInt
    val data = new MsgData ()
    hasTagsAssembler.entryToObject(data, input)
    hasPartsAssembler.entryToObject(data, input)
    data
  }

}

class HasTagsAssembler {
  var tagAssembler:TupleBinding[Tag] = _
  @Autowired
  @Qualifier("tagAssembler")
  def setTagAssembler(tagAssembler:TupleBinding[Tag]) = this.tagAssembler = tagAssembler

  def objectToEntry(tags: HasTags, output: TupleOutput): Unit = {
    val count = tags.tags.size
    output.writeInt(count)
    tags.tags.foreach { e => tagAssembler.objectToEntry(e, output) }
  }

  def entryToObject(tags: HasTags, input: TupleInput): Unit = {
    val count = input.readInt
    val range = 0.until(count)
    for (i <- range) {
      val tag = tagAssembler.entryToObject(input)
      if(tag!=null)
        tags.tags += tag
    }
  }
}

/**
 *
 */
class TagAssembler extends TupleBinding[Tag] {

  def objectToEntry(tag: Tag, output: TupleOutput): Unit = {
    output.writeString(tag.value)
  }

  def entryToObject(input: TupleInput): Tag = {
    Tag(input.readString)
  }

}


class HasPartsAssembler {

  var partAssembler: TupleBinding[Part] = null
  @Autowired
  @Qualifier("partAssembler")
  def setPartAssembler(partAssembler:TupleBinding[Part]) = this.partAssembler = partAssembler

  def objectToEntry(parts: HasParts, output: TupleOutput): Unit = {
    val count = parts.parts.size
    output.writeInt(count)
    parts.parts.foreach { e => partAssembler.objectToEntry(e, output) }
  }

  def entryToObject(parts: HasParts, input: TupleInput): Unit = {
    val count = input.readInt
    val range = 0.until(count)
    for (i <- range) {
      val part = partAssembler.entryToObject(input)
      if(part!=null)
        parts.parts += part
    }
  }
}

/**
 *
 */
object PartAssembler {
  val version: Int  = 1
  //
  val textPart: Int = 1
}

/**
 *
 */
class PartAssembler extends TupleBinding[Part] {

  def objectToEntry(part: Part, output: TupleOutput): Unit = {
    part match {
      case t:TextPart => {

      }
      case _ => throw new UnsupportedOperationException("Type not supported: "+part.getClass)
    }
  }

  def entryToObject(input: TupleInput): Part = {
    val partType = input.readInt
    partType match {
      case PartAssembler.textPart =>
        val text = input.readString
        new TextPart(text)
      case _ => throw new UnsupportedOperationException("Type not supported: "+partType)
    }
  }

}

object RawType {
  val integer: Short = 1
  val decimal: Short = 2
  val string : Short = 3
  val date   : Short = 4
  val binary : Short = 5

  def readValue(input:TupleInput):(Short,Any) = {
    val rawType = input.readShort
    val value:Any = rawType match {
      case RawType.date    => new Date(input.readLong)
      case RawType.string  => input.readLong
      case RawType.decimal => input.readDouble
      case RawType.integer => input.readLong
      case RawType.binary  =>
        val length = input.readInt
        input.readBytes(length)
    }
    (rawType,value)
  }
}

/**
 *
 */
class HasHeadersAssembler {

  def objectToEntry(headers: HasHeaders, output: TupleOutput): Unit = {
    val count = headers.headers.size
    output.writeInt(count)
    headers.headers.foreach { case (k,v) =>
      output.writeString(k)
      v match {
        case s:String =>
          output.writeShort(RawType.string)
          output.writeString(s)
        case d:Date =>
          output.writeShort(RawType.date)
          output.writeLong(d.getTime)
      }
    }
  }

  def entryToObject(headers: HasHeaders, input: TupleInput): Unit = {
    val count = input.readInt
    val range = 0.until(count)
    for (i <- range) {
      val hdrKey = input.readString
      val (valType,hdrVal) = RawType.readValue(input)
      headers.setHeader(hdrKey, hdrVal)
    }
  }
}
