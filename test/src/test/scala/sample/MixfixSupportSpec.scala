package sample

import org.scalatest._
import mixfix._
import model._

class MixfixSupportSpec extends UnitSpec {
  "mixfix support" should "work manually" in {
    object _inThe_withThe_ extends support.MixfixFunction3 with Function3[Suspect, Room, Weapon, Accusation] {
      override def apply(t1: T1, t2: T2, t3: T3): R = Accusation(t1, t2, t3)
      private def $apply = apply _
      
      protected type T1 = Suspect
      protected type T2 = Room
      protected type T3 = Weapon
      protected type R = Accusation

      // if we prefixed with 'itWas_...'
      def itWas(t1: T1) = (t1)
      
      implicit class $1(c1: C1) extends c1$t2(c1) {
        def inThe = apply _
      }

      implicit class $2(c2: C2) extends c3$[R](c2) {
        protected override def result(c3: C3) = ($apply).tupled (c3) 
        def withThe = apply _
      }
    }
  
    import _inThe_withThe_._
    
    val accusation = Accusation(ColonelMustard, Library, LeadPipe)
    _inThe_withThe_(ColonelMustard, Library, LeadPipe) should === (accusation)
    
    (ColonelMustard inThe Library withThe LeadPipe) should === (accusation)

    // if we prefixed with 'itWas_...'
    itWas(ColonelMustard).inThe(Library).withThe(LeadPipe) should === (accusation)
  

  }
}