package org.technbolts.integration.rss;

import static java.lang.String.format;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.integration.core.Message;
import org.springframework.stereotype.Service;

@Service("rssFeedMessageHandler")
public class RssFeedMessageHandler {

	private static Logger logger = Logger.getLogger(RssFeedMessageHandler.class);

	public void handleMessage(Message<List<NewsItem>> message) {
		StringBuilder  builder = new StringBuilder("\n");
		for(NewsItem news : message.getPayload())
			builder.append(news.getContentAsText()).append("\n");
		
		if (logger.isDebugEnabled())
			logger.debug(//
					format("At %1$tT-%1$tL I received a message with feedid <%2$s> and payload: '%3$s'", new Date(message.getHeaders()
							.getTimestamp()),//
							message.getHeaders().get("feedid", String.class), //
							builder.toString()));
	}
}
