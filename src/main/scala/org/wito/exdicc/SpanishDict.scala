package org.wito.exdicc

import java.lang.Boolean
import java.net.URLEncoder

import org.apache.log4j.LogManager
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

case class WordInfo(originalWord: String, lookedUpWord: String, quickDef: String, quickPos: String)

case class ProcessingRequest(fin: String, fout: String) {
  def this(fin: String) = this(fin, fin)
}

class SpanishDict {

  private val logger = LogManager.getLogger(getClass)

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

  private def select1st(node: Option[Element], path: String): Option[Element] = {
    if (node.isEmpty) {
      return None
    }
    val nds = node.get.select(path)
    if (nds.isEmpty) {
      return None
    }
    return Some(nds.get(0))
  }

  def getQuickDefinition(word: String): Option[WordInfo] = {

    val encodedURL = "http://www.spanishdict.com/translate/" + URLEncoder.encode(word)
    logger.debug("Getting doc from " + encodedURL + "...")
    val t1 = System.currentTimeMillis
    val document = Jsoup.connect(encodedURL).get
    logger.debug("Got in " + (System.currentTimeMillis - t1) + "ms.")

    val resultBlockNd = select1st(Some(document), ".results-block")
    val hwBlockNd = select1st(resultBlockNd, ".hw-block")
    val quickDefNd = select1st(hwBlockNd, ".quick_def")

    if (quickDefNd.isEmpty) {
      logger.info("Can't extract basic info for '" + word + "'")
      return None
    }

    val quickDef = quickDefNd.get.text
    var lookedUpWord = select1st(hwBlockNd, ".word").get.text
    var quickPos = ""

    val quickPosNd = select1st(hwBlockNd, ".quick_pos")
    if (!quickPosNd.isEmpty) {
      quickPos = quickPosNd.get.text
      if (quickPos == "noun") {
        val partOfSpeachNode = resultBlockNd.get.select(".main-translation .dictionary_word .part_of_speech")
        if (!partOfSpeachNode.isEmpty) {
          val pos = partOfSpeachNode.get(0).text
          val m = pos.contains("masculine")
          val f = pos.contains("feminine")
          lookedUpWord = getGenderPrefixForNoun(m, f) + lookedUpWord
        } else {
          logger.warn("Can't extract gender of a noun " + word)
        }
      }
    }

    return Some(new WordInfo(word, lookedUpWord, quickDef, quickPos))
  }

}

