package org.technbolts.util

import java.util.concurrent._
import atomic.AtomicInteger
import collection.jcl.MutableIterator.Wrapper
import scala.Option

trait Task {
  val executor:TaskExecutor
  private[this] val spawned = new LinkedBlockingDeque[FutureTask[_]]
  private[this] val spawnedCounter = new AtomicInteger

  def spawn[A](block: (Task)=> A):FutureTask[A] = {
    val sub:FutureTask[A] = executor.spawn(this, block)
    spawned.addLast(sub)
    spawnedCounter.incrementAndGet
    sub
  }

  def numberOfSpawnedSubs:Int = spawnedCounter.get()

  // this is the magic implicit bit
  implicit def javaIteratorToScalaIterator[A](it : java.util.Iterator[A]) = new Wrapper(it)

  def waitTermination:Unit = {

    var found:Option[FutureTask[_]] = null
    do{
      // traverse from last to head in order to help in the sub execution
      found = spawned.descendingIterator.find {t => !t.isDone }
      if(found.isDefined)
        found.get.run
    }
    while(found.isDefined)

    for(sub <- spawned.iterator) {
      // this will wait until the result is available
      // this should also handle the fact that a sub-task is spawn
      // and then added in the 'jobSpawned' before its parent is done
      sub.get
    }
  }
}

/**
 *
 */
class TaskExecutor(val nbWorkers:Int) {

  // create a thread pool according to the number of proc.
  def this() = this(java.lang.Runtime.getRuntime().availableProcessors())

  val workers:ExecutorService = Executors.newFixedThreadPool(nbWorkers);

  def newTask: Task = {
    new TaskClass(this)
  }

  def spawn[A](owner:Task, block: (Task)=> A):FutureTask[A] = {
    val futureTask = new FutureTask(new Callable[A]() {
      def call:A = { block(owner) }
    })
    workers.submit(futureTask)
    futureTask
  }
}

sealed case class TaskClass(val executor:TaskExecutor) extends Task;


