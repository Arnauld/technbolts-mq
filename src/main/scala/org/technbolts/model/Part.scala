package org.technbolts.model

import contenttype.ContentType

object Part {
  val CONTENT_TYPE = "content-type"
  def contentType(part:Part):ContentType = {
    if(part!=null)
      part.contentType
    else
      null
  }
}

trait Part extends HasHeaders {

  def contentType:ContentType = {
    getHeader(Part.CONTENT_TYPE) match {
      case Some(c:ContentType) => c
      case Some(s:String) => ContentType(s)
      case _ => null
    }
  }

  def contentType(contentType:ContentType) = {
    setHeader(Part.CONTENT_TYPE,  contentType)
    this
  }

  def visibleTextWeight ():Int = {
    ContentType.visibleTextWeight(contentType)
  }
}

object TextPart {
  def apply(text:String) = {
    new TextPart(text)
  }
}

class TextPart(var text:String) extends Part {
}