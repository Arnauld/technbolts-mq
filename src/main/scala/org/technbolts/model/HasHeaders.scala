package org.technbolts.model

import collection.mutable.HashMap

trait HasHeaders {
  val headers = new HashMap[String,AnyRef]

  def getHeader(headerKey:String):Option[AnyRef] = {
    headers.get(headerKey)
  }

  def setHeader(headerKey:String, headerValue:AnyRef):AnyRef = {
    headers.put(headerKey, headerValue)
  }

  def getHeaderAsString(headerKey:String):Option[String] = {
    getHeader(headerKey) match {
        case Some(s:String) => Some(s)
        case _ => None
      }
  }
}