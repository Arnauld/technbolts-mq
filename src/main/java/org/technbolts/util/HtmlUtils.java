package org.technbolts.util;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.tidy.Tidy;

public class HtmlUtils {

	public static String htmlToText(String html) {
		StringWriter out = new StringWriter();
		StringWriter err = new StringWriter();
		Tidy tidy = new Tidy();
		tidy.setErrout(new PrintWriter(err));
		tidy.setPrintBodyOnly(true);
		tidy.setMakeClean(true);
		tidy.setHideComments(true);
		tidy.setNumEntities(false);
		tidy.setAsciiChars(true);
		//Node node = 
		tidy.parse(new StringReader(html), out);
		String str = out.toString();
		str = str.replaceAll("<[^>]+>", " ");
		str = StringEscapeUtils.unescapeHtml(str);
		return str;
	}
}
