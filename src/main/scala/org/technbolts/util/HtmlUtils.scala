package org.technbolts.util

import org.apache.commons.lang.StringEscapeUtils
import org.w3c.tidy.Tidy
import java.io.{StringWriter, PrintWriter, StringReader}

/**
 * 
 */
object HtmlUtils {

  def htmlToText(html: String): String = {
    var out: StringWriter = new StringWriter
    var err: StringWriter = new StringWriter
    var tidy: Tidy = new Tidy
    tidy.setErrout(new PrintWriter(err))
    tidy.setPrintBodyOnly(true)
    tidy.setMakeClean(true)
    tidy.setHideComments(true)
    tidy.setNumEntities(false)
    tidy.setAsciiChars(true)
    tidy.parse(new StringReader(html), out)

    var str: String = out.toString
    // remove html elements
    str = str.replaceAll("<[^>]+>", " ")
    str = StringEscapeUtils.unescapeHtml(str)
    return str
  }
}