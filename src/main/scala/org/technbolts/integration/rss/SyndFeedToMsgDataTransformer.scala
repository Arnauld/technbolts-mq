package org.technbolts.integration.rss

import org.apache.log4j.Logger
import org.springframework.integration.core.Message
import org.springframework.integration.message.MessageBuilder
import org.springframework.stereotype.Component
import org.springframework.integration.annotation.Transformer
import collection.mutable.ListBuffer
import com.sun.syndication.feed.synd.{SyndCategory, SyndContent, SyndEntry, SyndFeed}
import java.util.Date
import org.technbolts.model.{TextPart, Tag, MsgDataSet, MsgData}
import org.technbolts.util.HtmlUtils
import org.technbolts.model.contenttype.{TextPlain, ContentType}
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
    var title: String  = syndEntry.getTitle
    var author: String = syndEntry.getAuthor
    var description: String = syndEntry.getDescription.getValue
    var publishedDate: Date = syndEntry.getPublishedDate
    var categories: List[Tag] = findCategories(syndEntry)
    var content: String = findContent(syndEntry)

    val msg = MsgData()
    msg.setTitle(title)
    msg.setDate(publishedDate)
    msg.setFrom(author)
    msg.tags ++ categories
    msg.parts += new TextPart(HtmlUtils.htmlToText(description)).contentType(TextPlain)

    msg;
  }

  def findCategories(syndEntry:SyndEntry):List[Tag] = {
    //if (logger.isDebugEnabled)
    //  logger.debug("Analysing entry categories: " + categories)
    val tags = new ListBuffer[Tag]
    for (o <- syndEntry.getCategories.iterator) {
      val syndCategory:SyndCategory = o.asInstanceOf[SyndCategory]
      tags += Tag(syndCategory.getName)
    }
    tags.toList
  }

  def findContent(syndEntry:SyndEntry):String = {
    //if (logger.isDebugEnabled)
    //  logger.debug("Analysing entry contents: " + syndEntry.getContents)
    for (syndContent <- syndEntry.getContents.iterator) {
      return syndContent.asInstanceOf[SyndContent].getValue
    }
    null;
  }
}