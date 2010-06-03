package org.technbolts.model

import collection.mutable.HashMap

trait HasHeaders {
  val headers = new HashMap[String,Any]

  def getHeader(headerKey:String):Option[Any] = {
    headers.get(headerKey)
  }

  def getHeaderAsString(headerKey:String):Option[String] = {
    headers.get(headerKey) match {
      case Some(s:String) => Some(s)
      case Some(o) => Some(o.toString)
      case None => None
    }
  }

  def setHeader(headerKey:String, headerValue:Any):Option[Any] = {
    headers.put(headerKey, headerValue)
  }
}