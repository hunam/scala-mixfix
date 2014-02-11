package sample

import mixfix._
import model._

object Main extends App {
  
  @syntax 
  def itWas_inThe_withThe_(suspect: Suspect, room: Room, weapon: Weapon) = 
    Accusation(suspect, room, weapon)
    
  import itWas_inThe_withThe_._
  
  println(itWas(ColonelMustard).inThe(Library).withThe(Rope))
  println(ColonelMustard inThe Library withThe Rope)
}