package org.wito.exdicc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpanishDictTest {

  @Test
  def testMasculinNoun() {
    var wi = SpanishDict("tienda").get
    assertEquals("tienda", wi.originalWord)
    assertEquals("la tienda", wi.lookedUpWord)
    assertEquals("store", wi.quickDef)
    assertEquals("noun", wi.quickPos)
  }

  @Test
  def testMasculinAndFemeninNoun() {
    var wi = SpanishDict("tio").get
    assertEquals("tio", wi.originalWord)
    assertEquals("el/la tío", wi.lookedUpWord)
    assertEquals("uncle", wi.quickDef)
    assertEquals("noun", wi.quickPos)
  }

  @Test
  def tesCantFindWord() {
    var wi = SpanishDict("niemamnie")
    assertTrue(wi.isEmpty)
  }
}