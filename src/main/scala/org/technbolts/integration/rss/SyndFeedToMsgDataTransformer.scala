package org.technbolts.integration.rss

import org.apache.log4j.Logger
import org.springframework.integration.core.Message
import org.springframework.integration.message.MessageBuilder
import com.sun.syndication.feed.synd.{SyndContent, SyndEntry, SyndFeed}
import org.springframework.stereotype.Component
import org.technbolts.model.{MsgDataSet, MsgData}
import org.springframework.integration.annotation.Transformer
import collection.mutable.ListBuffer
// this is the wrapper
import scala.collection.jcl.MutableIterator.Wrapper

@Component("syndFeedToMsgDataTransformer")
class SyndFeedToMsgDataTransformer {
  private var logger: Logger = Logger.getLogger(classOf[SyndFeedToMsgDataTransformer])

  // this is the magic implicit bit
  implicit def javaIteratorToScalaIterator[A](it : java.util.Iterator[A]) = new Wrapper(it)

  @Transformer
  def transform(syndFeedMessage: Message[SyndFeed]): Message[MsgDataSet] = {
    if (logger.isDebugEnabled)
      logger.debug(format("Received a feed from the blog %1$s", syndFeedMessage.getPayload.getTitle))

    val syndFeed: SyndFeed = syndFeedMessage.getPayload
    val msgs = new ListBuffer[MsgData]
    for(e <- syndFeed.getEntries.iterator)
        msgs += convertEntry(e.asInstanceOf[SyndEntry])

    var newMessage: Message[MsgDataSet] =
      MessageBuilder
              .withPayload(new MsgDataSet(msgs.toList))
              .copyHeaders(syndFeedMessage.getHeaders)
              .build
    return newMessage
  }

  def convertEntry(syndEntry: SyndEntry): MsgData = {
    var title: String = syndEntry.getTitle
    var author: String = syndEntry.getAuthor
    var description: String = syndEntry.getDescription.getValue
    var publishedDate = syndEntry.getPublishedDate
    var categories = syndEntry.getCategories

    if (logger.isDebugEnabled)
      logger.debug("Analysing entry categories: " + categories)

    var content: String = findContent(syndEntry)

    val msg = new MsgData()
    msg;
  }

  def findContent(syndEntry:SyndEntry):String = {
    if (logger.isDebugEnabled)
      logger.debug("Analysing entry contents: " + syndEntry.getContents)
    for (syndContent <- syndEntry.getContents.iterator) {
      return syndContent.asInstanceOf[SyndContent].getValue
    }
    null;
  }
}