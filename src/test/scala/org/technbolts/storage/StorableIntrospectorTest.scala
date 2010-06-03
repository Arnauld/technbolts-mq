package org.technbolts.storage

import org.junit.{Before, Test}

/**
 *
 *
 */
class StorableIntrospectorTest {

  var introspector: StorableIntrospector = _

  @Before
  def setUp:Unit = {
    introspector = new StorableIntrospector
  }

  @Test
  def sampleCase : Unit = {
    introspector.examine(classOf[ModelExt])
  }

}