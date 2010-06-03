package org.technbolts.model.contenttype

sealed abstract class ContentType(body:String) {
  def isText():Boolean = {
    body.startsWith("text/")
  }
}

case object TextPlain               extends ContentType("text/plain")
case object TextHtml                extends ContentType("text/html")
case class Text  (val body: String) extends ContentType("text/"+body)
case class Other (val body: String) extends ContentType(body)

object ContentType {
  def visibleTextWeight (c: ContentType) = c match {
    case TextPlain => +2
    case TextHtml  => +3
    case Text (body) => +1
    case Other(body) =>  0
    case _ => 0
  }

  val TextRE = """text/(.+)""".r

  def apply(s:String):ContentType = s match {
    case "text/plain" => TextPlain
    case "text/html"  => TextHtml
    case TextRE(sub)  => Text(sub)
    case _            => Other(s)
  }
}