package org.technbolts.integration.rss;

import org.technbolts.util.HtmlUtils;

public class NewsItem {

	private String title;
	private String description;
	private String content;
	private String author;

	public NewsItem() {
	}
	
	public NewsItem(String title, String description, String content) {
		this.title = title;
		this.description = description;
		this.content = content;
	}
	
	public NewsItem withAuthor(String author) {
		this.author = author;
		return this;
	}
	public NewsItem withContent(String content) {
		this.content = content;
		return this;
	}
	public NewsItem withDescription(String description) {
		this.description = description;
		return this;
	}
	public NewsItem withTitle(String title) {
		this.title = title;
		return this;
	}
	public String getContentAsText () {
		String string = content;
		if(string==null)
			string = description;
		return HtmlUtils.htmlToText(string);
	}

	@Override
	public String toString() {
		return "NewsItem [" +//
				"author=" + author + ", " +//
				"content=" + content + ", " +//
				"description=" + description + ", " +//
				"title=" + title + "]";
	}
	
}
