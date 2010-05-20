package org.technbolts.integration.rss

import org.apache.log4j.Logger
import org.springframework.integration.core.Message
import org.technbolts.model.MsgData
import org.springframework.integration.message.MessageBuilder
import com.sun.syndication.feed.synd.{SyndContent, SyndEntry, SyndFeed}
import org.springframework.stereotype.Component

@Component("syndFeedToMsgDataTransformer")
class SyndFeedToMsgDataTransformer {
  private var logger: Logger = Logger.getLogger(classOf[SyndFeedToMsgDataTransformer])

  def transform(syndFeedMessage: Message[SyndFeed]): Message[List[MsgData]] = {
    if (logger.isDebugEnabled)
      logger.debug(format("Received a feed from the blog %1$s", syndFeedMessage.getPayload.getTitle))

    val syndFeed: SyndFeed = syndFeedMessage.getPayload
    val msgs:List[MsgData] =
      syndFeed.getEntries.asInstanceOf[List[SyndEntry]].map {
        syndEntry => transform(syndEntry)
      }
    var newMessage: Message[List[MsgData]] =
      MessageBuilder
              .withPayload(msgs.toList)
              .copyHeaders(syndFeedMessage.getHeaders)
              .build
    return newMessage
  }

  def transform(syndEntry: SyndEntry): MsgData = {
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
    for (syndContent <- syndEntry.getContents.asInstanceOf[List[SyndContent]]) {
      return syndContent.getValue
    }
    null;
  }
}