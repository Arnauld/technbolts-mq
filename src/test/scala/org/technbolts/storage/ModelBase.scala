package org.technbolts.storage

import collection.mutable.ListBuffer

trait HasTags {
  val tags:ListBuffer[String] = new ListBuffer[String]
}

class ModelBase {
  var subsScala:List[ModelBase] = _
  var subsJCL:java.util.List[ModelBase] = _
  val size = 17
}

class ModelExt extends ModelBase with HasTags {
  var name:String = _
}