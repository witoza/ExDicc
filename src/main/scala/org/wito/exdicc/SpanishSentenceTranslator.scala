package org.wito.exdicc

import java.net.URLEncoder

import org.apache.log4j.LogManager

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage

class SpanishSentenceTranslator {
  private val logger = LogManager.getLogger(getClass)

  private val webClient = new WebClient(BrowserVersion.CHROME)

  private def getTranslation(page: HtmlPage, client: WebClient, transType: String = "microsoft_translation_result"): Option[String] = {
    for (i <- 1 to 3) {
      val nd = page.getElementById(transType)
      if (!nd.asXml.contains("alt=\"Loading\"")) {
        return Some(nd.asText)
      }
      client.waitForBackgroundJavaScript(1000)
    }
    return None
  }

  def translate(sentence: String): Option[String] = {
    val encodedURL = "http://www.spanishdict.com/translate/" + URLEncoder.encode(sentence)
    logger.info("Getting doc from " + encodedURL + "...")
    val page: HtmlPage = webClient.getPage(encodedURL)
    return getTranslation(page, webClient)
  }

  def close() {
    webClient.closeAllWindows
  }
}

