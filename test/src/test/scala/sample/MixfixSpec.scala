package sample

import model._
import mixfix._

import org.scalatest._
import shapeless.test.illTyped

class MixfixSpec extends UnitSpec {

  val accusation = Accusation(ColonelMustard, Library, LeadPipe)

  "mixfix syntax" should "provide hint that it is enabled" in {
    @syntax def _foobar_(a: Int, b: Int) = a + b
    
    _foobar_.mixfixEnabled should be (true)
  }

  it should "allow direct invocation of the original function" in {
    @syntax 
    def _inThe_withThe_(suspect: Suspect, room: Room, weapon: Weapon) = Accusation(suspect, room, weapon)
    
    _inThe_withThe_(ColonelMustard, Library, LeadPipe) === accusation
  }
  
  it should "support 3-argument functions" in {
    @syntax 
    def _inThe_withThe_(suspect: Suspect, room: Room, weapon: Weapon) = Accusation(suspect, room, weapon)
  
    import _inThe_withThe_._
    (ColonelMustard inThe Library withThe LeadPipe) should === (accusation)
  }

  it should "support 2-argument functions" in {
    @syntax
    def _sightedIn_(suspect: Suspect, room: Room) = Sighting(suspect, room)
    
    val sighting = Sighting(MissScarlet, Ballroom)

    import _sightedIn_._
    (MissScarlet sightedIn Ballroom) should === (sighting)
  }
  
  it should "allow using a prefix method" in {
    @syntax
    def itWas_inThe_withThe_(suspect: Suspect, room: Room, weapon: Weapon) = Accusation(suspect, room, weapon)
    
    import itWas_inThe_withThe_._
    (itWas(ColonelMustard) inThe Library withThe LeadPipe) should === (accusation)
  }
  
  it should "allow repeated usage of same type" in {
    @syntax 
    def _neq_(weapon1: Weapon, weapon2: Weapon): Boolean = weapon1 != weapon2
      
    import _neq_._
    (Revolver neq Rope) should be (true)
    (Revolver neq Revolver) should be (false)
  }
  
  it should "allow unicode abuse" in {
    @syntax 
    def `_≠_`(weapon1: Weapon, weapon2: Weapon): Boolean = weapon1 != weapon2
    
    import `_≠_`._
    (Revolver ≠ Rope) should be (true)
    (Revolver ≠ Revolver) should be (false)
  }
  
  it should "escape object methods after first clause" in {
    import math.BigInt
    @syntax def `_⌃_%_`(i: BigInt, exp: BigInt, m: BigInt) = i.modPow(exp, m)
    
    import `_⌃_%_`._
    
    /* note:
     * `⌃` is a unicode character that looks similar to a caret (^), already used by xor().
     * % is plain old shift-5, which is defined as modulu for BigInt.
     */  
    
    (BigInt(2) `⌃` BigInt(10) % BigInt(1000)) should === (24)
  }

  it should "support type parameters" in {
    illTyped("""
      @syntax def _add_[A : Numeric](a: A, b: A) = { val n = implicitly[Numeric[A]]; import n._; a + b }
    """)
    pending
  }
  
  it should "support using an anonymous parameter list" in {
    illTyped("""
      @syntax def `_add_`: (Int, Int) ⇒ Int = _ + _
    """)
    pending
  }
  
  it should "support multiple parameter lists" in {
    illTyped("""
      @syntax def `_add_`(a: Int)(b: Int): Int = a + b
    """)
    pending
  }
  
  it should "allow using multiple syntax definitions together" in {
    illTyped("""
      @syntax def `_add_`(a: Int, b: Int): Int = a + b
      @syntax def `_sub_`(a: Int, b: Int): Int = a + b
      import _add_._, _sub_._
      (1 add 2)
      (2 sub 1)
    """)
    pending
  }
  
}
