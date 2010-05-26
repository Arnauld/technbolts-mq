package org.technbolts.model

import collection.mutable.ListBuffer

trait HasTags {
  var tags = new ListBuffer[Tag]
}

sealed class Tag(val value:String)


import org.technbolts.di.RichDomainObjectFactory._

object Tag {
   def apply(value:String) = {
       autoWireFactory.autowire(new Tag(value))
    }
}