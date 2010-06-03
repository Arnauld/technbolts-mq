package org.technbolts.sample.htmlunit

import collection.jcl.MutableIterator.Wrapper
import java.net.URL
import com.gargoylesoftware.htmlunit.{Page, RefreshHandler, WebClient}
import com.gargoylesoftware.htmlunit.html.{HtmlTableCell, HtmlTableRow, HtmlPage, HtmlTable}
import org.junit.{Ignore, Test}

/**
 *
 */
@Ignore
class BoursoramaTest {

  // this is the magic implicit bit
  implicit def javaIteratorToScalaIterator[A](it : java.util.Iterator[A]) = new Wrapper(it)

  @Test
  def loadFirstPage(): Unit = {
    val webClient = new WebClient();
    webClient.setCssEnabled(false)
    webClient.setJavaScriptEnabled(false)
    webClient.setRefreshHandler(new RefreshHandler (){
      def handleRefresh(page: Page, url: URL, p3: Int) = {
        println (">"+url)
      }
    });

    for(i <- 0 to 8) {
      val page:HtmlPage = webClient.getPage("http://www.boursorama.com/tableaux/secteurs.phtml?SECTEUR=9&OFFSET="+(i*20));
      val tables: java.util.List[_]  = page.getByXPath("//table[starts-with(@class,'btable')]")
      for(e <- tables.iterator) {
        parseTable(e.asInstanceOf[HtmlTable])
      }
    }
  }

  def parseTable(table: HtmlTable):Unit = {
    val count = table.getRowCount
    if(count<2)
      return

    val headers = parseRow(table.getRow(0))
    headers match {
      case (x:String)::"Libellé"::"Dernier"::tail =>
        for(i <- 1 to table.getRowCount - 1) {
          val row = parseRow(table.getRow(i));
          println(row(1)+ " '" +row(2)+"' --")
        }
      case _ =>
          println("cannot parse: "+headers)
    }
  }
  
  def parseRow(row:HtmlTableRow):List[String] = {
    row.getCells.iterator.toList.map[String] { c =>
      val cell:HtmlTableCell = c.asInstanceOf[HtmlTableCell]
      val string:String = cell.asText
      string.replaceAll("[ \t\r\n]+", "")
    }
  }

}