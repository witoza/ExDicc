package org.wito.exdicc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpanishDictTest {

  private val spanishDict = new SpanishDict

  @Test
  def masculinNoun() {
    var wi = spanishDict.getQuickDefinition("tienda").get
    assertEquals("tienda", wi.originalWord)
    assertEquals("la tienda", wi.lookedUpWord)
    assertEquals("store", wi.quickDef)
    assertEquals("noun", wi.quickPos)
  }

  @Test
  def masculinAndFemeninNoun() {
    var wi = spanishDict.getQuickDefinition("tio").get
    assertEquals("tio", wi.originalWord)
    assertEquals("el/la tío", wi.lookedUpWord)
    assertEquals("uncle", wi.quickDef)
    assertEquals("noun", wi.quickPos)
  }

  @Test
  def cantFindWord() {
    var wi = spanishDict.getQuickDefinition("niemamnie")
    assertTrue(wi.isEmpty)
  }
}