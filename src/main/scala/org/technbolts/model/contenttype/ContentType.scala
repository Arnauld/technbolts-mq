package org.technbolts.model.contenttype

sealed abstract class ContentType(body:String) {
  def isText():Boolean = {
    body.startsWith("text/")
  }
}

case class TextPlain                extends ContentType("text/plain")
case class TextHtml                 extends ContentType("text/html")
case class Text  (val body: String) extends ContentType("text/"+body)
case class Other (val body: String) extends ContentType(body)

object ContentType {
  def visibleTextWeight (c: ContentType) = c match {
    case c:TextPlain => +2
    case c:TextHtml  => +3
    case Text (body) => +1
    case Other(body) =>  0
    case _ => 0
  }
}