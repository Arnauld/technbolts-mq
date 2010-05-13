package org.technbolts.integration.rss;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.stereotype.Component;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

@Component("syndFeedNewsItemTransformer")
public class SyndFeedToNewsItemTransformer {
	private Logger logger = Logger
			.getLogger(SyndFeedToNewsItemTransformer.class);

	public Message<List<NewsItem>> transform(Message<SyndFeed> syndFeedMessage) {
		if (logger.isDebugEnabled())
			logger.debug(format("Received a feed from the blog %1$s",
					syndFeedMessage.getPayload().getTitle()));

		SyndFeed syndFeed = syndFeedMessage.getPayload();

		List<NewsItem> newsItems = new ArrayList<NewsItem>();
		List<?> syndFeedItems = syndFeed.getEntries();
		for (Object syndFeedEntry : syndFeedItems) {
			SyndEntry syndEntry = (SyndEntry) syndFeedEntry;
			String title = syndEntry.getTitle();
			String author = syndEntry.getAuthor();
			String description = syndEntry.getDescription().getValue();

			if (logger.isDebugEnabled())
				logger.debug("Analysing entry: " + syndEntry.getContents());

			String content = null;
			for (Object o : syndEntry.getContents()) {
				SyndContent syndContent = (SyndContent) o;
				content = syndContent.getValue();
				break;
			}
			// a lot of other information is possible
			newsItems.add(new NewsItem()//
					.withTitle(title)//
					.withDescription(description)//
					.withAuthor(author)//
					.withContent(content));
		}
		Message<List<NewsItem>> newMessage = //
		MessageBuilder//
				.withPayload(newsItems)//
				.copyHeaders(syndFeedMessage.getHeaders())//
				.build();
		return newMessage;
	}
}
