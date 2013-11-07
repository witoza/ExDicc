package org.wito.exdicc

import java.lang.Boolean
import java.net.URLEncoder

import org.apache.log4j.LogManager
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

case class WordInfo(originalWord: String, lookedUpWord: String, quickDef: String, quickPos: String)

case class ProcessingRequest(fin: String, fout: String)

class SpanishDict(word: String) {

  private val logger = LogManager.getLogger(getClass)

  private val doc = {
    val encodedURL = "http://www.spanishdict.com/translate/" + URLEncoder.encode(word)
    logger.info("Getting doc from " + encodedURL + "...")
    val t1 = System.currentTimeMillis
    val document = Jsoup.connect(encodedURL).get
    logger.info("done in " + (System.currentTimeMillis - t1) + "ms.")
    document
  }

  private def getGenderPrefixForNoun(m: Boolean, f: Boolean): String = {
    if (m) {
      if (f) {
        return "el/la "
      } else {
        return "el "
      }
    } else {
      if (f) {
        return "la "
      }
    }
    return ""
  }

  def getQuickDefinition(): WordInfo = {

    var quickPos = ""
    var quickDef = ""
    var lookedUpWord = ""

    val resultBlock = doc.select(".results-block").get(0)

    try {
      val nd = resultBlock.select(".hw-block").get(0)
      quickDef = nd.select(".quick_def").get(0).text
      lookedUpWord = nd.select(".word").get(0).text

      val quickPosNode = nd.select(".quick_pos")
      if (!quickPosNode.isEmpty) {
        quickPos = quickPosNode.get(0).text
        if (quickPos == "noun") {
          val partOfSpeachNode = resultBlock.select(".main-translation .dictionary_word .part_of_speech")
          if (!partOfSpeachNode.isEmpty()) {
            val pos = partOfSpeachNode.get(0).text
            val m = pos.contains("masculine")
            val f = pos.contains("feminine")
            lookedUpWord = getGenderPrefixForNoun(m, f) + lookedUpWord
          } else {
            logger.warn("Can't extract gender of a noun " + word)
          }

        }
      }

    } catch {
      case e: Exception =>
        logger.debug("Can't extract basic info for word " + word, e)
        lookedUpWord = ""
    }

    return new WordInfo(word, lookedUpWord, quickDef, quickPos)
  }

}

object SpanishDict {
  def apply(word: String): WordInfo = {
    return new SpanishDict(word).getQuickDefinition
  }
}