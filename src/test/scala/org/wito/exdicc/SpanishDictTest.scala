package org.wito.exdicc

import org.junit.Assert.assertEquals
import org.junit.Test

class SpanishDictTest {

  @Test
  def testMasculinNoun() {
    var wi = SpanishDict("tienda")
    assertEquals("tienda", wi.originalWord)
    assertEquals("la tienda", wi.lookedUpWord)
    assertEquals("store", wi.quickDef)
    assertEquals("noun", wi.quickPos)
  }

  @Test
  def testMasculinAndFemeninNoun() {
    var wi = SpanishDict("tio")
    assertEquals("tio", wi.originalWord)
    assertEquals("el/la tío", wi.lookedUpWord)
    assertEquals("uncle", wi.quickDef)
    assertEquals("noun", wi.quickPos)
  }

  @Test
  def tesCantFindWord() {
    var wi = SpanishDict("niemamnie")
    assertEquals("niemamnie", wi.originalWord)
    assertEquals("", wi.lookedUpWord)
    assertEquals("", wi.quickDef)
    assertEquals("", wi.quickPos)
  }
}