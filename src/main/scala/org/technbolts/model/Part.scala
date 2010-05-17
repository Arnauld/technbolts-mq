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
  def contentType():ContentType = {
    getHeader(Part.CONTENT_TYPE) match {
      case Some(c:ContentType) => c
      case _ => null
    }
  }
  def visibleTextWeight ():Int = {
    ContentType.visibleTextWeight(contentType)
  }
}

class TextPart extends Part {
  var text: Option[String] = None
}