package models.game

object GameRole extends Enumeration {
  type GameRole = Value
  val Requestor, Requestee, Unrelated = Value
}
