package org.wito.exdicc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SentenceTranslatorTest {

  @Test
  def noTranslation() {
    val str = new SpanishSentenceTranslator
    assertEquals(None, str.translate("dupadupadupa"))
    str.close
  }

  @Test
  def thereIsTranslation() {
    val str = new SpanishSentenceTranslator
    assertEquals(Some("feel very cold"), str.translate("sienten mucho frio"))
    str.close
  }

}