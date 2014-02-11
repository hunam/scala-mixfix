package sample

object model {

  sealed trait Suspect
  case object MissScarlet extends Suspect
  case object ColonelMustard extends Suspect
  case object MrsWhite extends Suspect
  case object ReverendGreen extends Suspect
  case object MrsPeacock extends Suspect
  case object ProfessorPlum extends Suspect
  
  sealed trait Room
  case object Kitchen extends Room
  case object Ballroom extends Room
  case object Conservatory extends Room
  case object DiningRoom extends Room
  case object BilliardRoom extends Room
  case object Library extends Room
  case object Lounge extends Room
  case object Hall extends Room
  case object Study extends Room

  sealed trait Weapon
  case object Cnadlestick extends Weapon
  case object Dagger extends Weapon
  case object LeadPipe extends Weapon
  case object Revolver extends Weapon
  case object Rope extends Weapon
  case object Wrench extends Weapon
  
  case class Accusation(suspect: Suspect, room: Room, weapon: Weapon) {
    override def toString = s"it was $suspect in the $room with the $weapon"
  }

  case class Sighting(suspect: Suspect, room: Room) {
    override def toString = s"$suspect has been sighted in the $room"
  }
}