package org.technbolts.util

import java.util.concurrent.TimeUnit

/**
 */

object TimeValue {
  def milliseconds(value:Long) = { new TimeValue(value, TimeUnit.MILLISECONDS)}
  def seconds(value:Long) = { new TimeValue(value, TimeUnit.SECONDS)}
  def minutes(value:Long) = { new TimeValue(value, TimeUnit.MINUTES)}
  def hour(value:Long)    = { new TimeValue(value, TimeUnit.HOURS)}
  def days(value:Long)    = { new TimeValue(value, TimeUnit.DAYS)}
}

class TimeValue(val value:Long, val unit:TimeUnit)