package org.technbolts.util

import java.util.concurrent.atomic.AtomicInteger
import org.springframework.stereotype.Service
import java.util.concurrent.{ThreadFactory, Executors}
import java.lang.Runnable
import org.slf4j.{Logger, LoggerFactory}

/**
 *
 */
object ExecutionService {
}

@Service("sharedExecutionService")
class ExecutionService {
  private var logger: Logger = LoggerFactory.getLogger(classOf[ExecutionService])

  var scheduledExecutor = Executors.newScheduledThreadPool(1, new DaemonThreadFactory("SharedWorker-"))

  // define conversion from any parameterless function
  // to java.lang.Runnable
  implicit def asRunnable(func: () => Unit): Runnable = {
    new Runnable() {
      def run() {
        if(logger.isDebugEnabled)
          logger.debug("\n*\n*\n*Executing nested function\n*\n*");

        try{
          func()
        }catch{
          case e:InterruptedException => throw e;
          case t:Throwable => logger.error("Execution failure", t);
        }

        if(logger.isDebugEnabled)
          logger.debug("\n*\n*\n*Nested function executed\n*\n*");
      }
    }
  }

  def scheduleAtFixedRate(func: () => Unit, period: TimeValue) {
    scheduledExecutor.scheduleAtFixedRate(func, period.value, period.value, period.unit)
  }
}

class DaemonThreadFactory(val prefix: String) extends ThreadFactory {
  val idGen = new AtomicInteger()

  def newThread(r: Runnable) = {
    val thread = new Thread(r, prefix + idGen.incrementAndGet);
    thread.setDaemon(true)
    thread
  }
}