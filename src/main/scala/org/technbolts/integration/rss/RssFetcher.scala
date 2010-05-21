package org.technbolts.integration.rss

import org.springframework.stereotype.Service
import com.sun.syndication.feed.synd.SyndFeed
import java.net.URL
import java.io.IOException
import com.sun.syndication.io.FeedException
import org.apache.log4j.Logger
import com.sun.syndication.fetcher.{FeedFetcher, FetcherListener, FetcherException}
import com.sun.syndication.fetcher.impl.{FeedFetcherCache, HttpURLFeedFetcher, HashMapFeedInfoCache}
import org.springframework.integration.core.Message
import org.springframework.integration.message.{MessageBuilder, MessageSource}
import javax.annotation.PostConstruct

@Service("rssFetcher")
class RssFetcher extends MessageSource[SyndFeed] {

  private var logger: Logger = Logger.getLogger(classOf[RssFetcher])
  
  private var feedInfoCache: FeedFetcherCache = null
  private var feedFetcher: FeedFetcher = null
  private var fetcherListener: FetcherListener = null
  private var url: String = "http://feeds.feedburner.com/nosql"

  def receive: Message[SyndFeed] = {
    if (logger.isDebugEnabled)
      logger.debug("readRssFeed method is called")
    var feed: SyndFeed = obtainFeedItems
    return MessageBuilder.withPayload(feed).setHeader("feed-url", url).build
  }

  @PostConstruct
	def postConstruct: Unit = {
    feedInfoCache = HashMapFeedInfoCache.getInstance
    feedFetcher = new HttpURLFeedFetcher(feedInfoCache)
    if (fetcherListener != null) {
      feedFetcher.addFetcherEventListener(fetcherListener)
    }
  }

  private def obtainFeedItems: SyndFeed = {
    var feed: SyndFeed = null
    try {
      feed = feedFetcher.retrieveFeed(new URL(url))

      if(logger.isDebugEnabled)
        logger.debug(">"+feed)
    }
    catch {
      case e: IOException => {
        logger.error("IO Problem while retrieving feed", e)
      }
      case e: FeedException => {
        logger.error("Feed Problem while retrieving feed", e)
      }
      case e: FetcherException => {
        logger.error("Fetcher Problem while retrieving feed", e)
      }
    }
    return feed
  }

}
