package org.technbolts.model

import collection.mutable.ListBuffer

trait HasParts {
  var parts = new ListBuffer[Part]
}