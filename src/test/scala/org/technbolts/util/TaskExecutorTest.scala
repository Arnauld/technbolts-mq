package org.technbolts.util

import java.util.concurrent.atomic.AtomicInteger
import org.junit.{Test, Before}

class TaskExecutorTest {

  var taskExecutor:TaskExecutor = _

  @Before
  def setUp = {
    taskExecutor = new TaskExecutor
  }

  @Test
  def simpleCase = {
    val counter = new AtomicInteger
    val task:Task = taskExecutor.newTask
    task.spawn( (owner:Task)=> {
      for(i <- 0 to 5) {
        owner.spawn( (o) => {
          fib(5*i)
          counter.incrementAndGet })
      }
    })
    task.waitTermination
    println(counter.get)
    println(task.numberOfSpawnedSubs)
  }

  def fib(v:Int):Int = v match {
    case 0 => 0
    case 1 => 1
    case _ => fib(v-1)+fib(v-2)
  }

}