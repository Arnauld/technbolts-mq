package org.technbolts.integration

import org.springframework.stereotype.Service
import org.apache.log4j.Logger
import org.springframework.integration.core.Message
import org.technbolts.model.MsgData
import java.util.Date

/**
 * Created by IntelliJ IDEA.
 * User: arnauld
 * Date: 20 mai 2010
 * Time: 00:18:04
 * To change this template use File | Settings | File Templates.
 */
@Service("msgDataMessageHandler")
class MsgDataMessageHandler {
  private var logger: Logger = Logger.getLogger(classOf[MsgDataMessageHandler])


  def handleMessage(message: Message[List[MsgData]]): Unit = {
    var builder: StringBuilder = new StringBuilder("\n")
    for (msg <- message.getPayload)
      builder.append(msg.contentAsText).append("\n")

    val date:Date = new Date(message.getHeaders.getTimestamp.asInstanceOf[Long])
    if (logger.isDebugEnabled)
      logger.debug(format("At %1$tT-%1$tL I received a message with feedid <%2$s> and payload: '%3$s'", date, message.getHeaders.get("feedid", classOf[String]), builder.toString))
  }
}