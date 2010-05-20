package org.technbolts.model

import java.util.Date

object MsgData {
  val DATE  = "date"
  val TITLE = "title"
}

class MsgDataSet (val msgs:List[MsgData])

class MsgData extends HasParts with HasHeaders with HasTags {

  def date():Date =    { getHeader(MsgData.DATE).asInstanceOf[Date]}
  def title():String = { getHeaderAsString(MsgData.TITLE).getOrElse(null)}

  def textParts():Seq[TextPart] = {
    parts.filter {p => p match {
            case t:TextPart => true
            case _ => false
      }}.map {p => p.asInstanceOf[TextPart] }
  }

  def bestVisiblePart ():TextPart = {
    textParts().reduceLeft((a, b) => {
      val weightA = a.visibleTextWeight()
      val weightB = b.visibleTextWeight()
      if(weightB>weightA)
        b;
      else
        a;
    });
  }

  def contentAsText (): String = {
    "text"
  }
}