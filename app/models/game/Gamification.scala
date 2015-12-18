package models.game

object Gamification {
  val pointsPerLevel = 30;

  def level(points: Int) = points / pointsPerLevel

  def pointsInLevel(points: Int) = points % pointsPerLevel
}
