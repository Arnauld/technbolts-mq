package org.technbolts.integration.rss;

import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageSource;
import org.springframework.stereotype.Service;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.fetcher.FetcherListener;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.io.FeedException;

@Service("rssReader")
public class RssReader implements MessageSource<SyndFeed> {

	private static Logger logger = Logger.getLogger(RssReader.class);

	private FeedFetcherCache feedInfoCache;
	private FeedFetcher feedFetcher;
	private FetcherListener fetcherListener;

	private String url = "http://www.gridshore.nl/feed/";

	public Message<SyndFeed> receive() {
		if (logger.isDebugEnabled())
			logger.debug("readRssFeed method is called");

		SyndFeed feed = obtainFeedItems();
		return MessageBuilder//
				.withPayload(feed)//
				.setHeader("feedid", "gridshore")//
				.build();
	}

	@PostConstruct
	public void postConstruct() {
		feedInfoCache = HashMapFeedInfoCache.getInstance();
		feedFetcher = new HttpURLFeedFetcher(feedInfoCache);
		if (fetcherListener != null) {
			feedFetcher.addFetcherEventListener(fetcherListener);
		}
	}

	private SyndFeed obtainFeedItems() {
		SyndFeed feed = null;
		try {
			feed = feedFetcher.retrieveFeed(new URL(url));
		} catch (IOException e) {
			logger.error("IO Problem while retrieving feed", e);
		} catch (FeedException e) {
			logger.error("Feed Problem while retrieving feed", e);
		} catch (FetcherException e) {
			logger.error("Fetcher Problem while retrieving feed", e);
		}
		return feed;
	}

	public void setFetcherListener(FetcherListener fetcherListener) {
		this.fetcherListener = fetcherListener;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
