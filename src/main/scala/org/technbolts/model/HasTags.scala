package org.technbolts.model

import collection.mutable.ListBuffer

trait HasTags {
  var tags = new ListBuffer[Tag]
}

sealed class Tag(val value:String)