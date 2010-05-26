package org.technbolts.model

import java.util.Date

object MsgDataSet {
  def apply(msgs:List[MsgData]) {
    new MsgDataSet(msgs)
  }
  def apply(msg:MsgData) {
    new MsgDataSet(List(msg))
  }
}

class MsgDataSet (val msgs:List[MsgData])

class MsgData extends HasParts with HasHeaders with HasTags {

  def getDate():Date = getHeader(MsgData.DATE).asInstanceOf[Date]
  def setDate(date:Date) = setHeader(MsgData.DATE, date)
  def getTitle():String = getHeaderAsString(MsgData.TITLE).getOrElse(null)
  def setTitle(title:String) = setHeader(MsgData.TITLE, title)
  def getFrom():String = getHeaderAsString(MsgData.FROM).getOrElse(null)
  def setFrom(from:String) = setHeader(MsgData.FROM, from)

  def textParts():Seq[TextPart] = {
    parts.filter {p => p match {
            case t:TextPart => true
            case _ => false
      }}.map {p => p.asInstanceOf[TextPart] }
  }

  def bestVisiblePart ():Option[TextPart] = {
    def found = textParts().reduceLeft((a, b) => {
      val weightA = a.visibleTextWeight()
      val weightB = b.visibleTextWeight()
      if(weightB>weightA)
        b;
      else
        a;
    });
    if(found==null)
      None
    else
      Some(found)
  }

  def contentAsText (): Option[String] = {
    bestVisiblePart () match {
      case Some(t) => Some(t.text)
      case _ => None
    }
  }
}

import org.technbolts.di.RichDomainObjectFactory._

object MsgData {
  val DATE  = "date"
  val TITLE = "title"
  val FROM = "from"

   def apply() = {
       autoWireFactory.autowire(new MsgData())
   }
}
