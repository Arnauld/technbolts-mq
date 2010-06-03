package org.technbolts.storage

import collection.mutable.ListBuffer

trait HasTags {
  val tags = new ListBuffer
}

class ModelBase {
  var subs:List[ModelBase] = _
  val size = 17
}

class ModelExt extends ModelBase with HasTags {
  var name:String = _
}