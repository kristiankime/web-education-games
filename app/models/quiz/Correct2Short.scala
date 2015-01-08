package models.quiz

object Correct2Short {
  val T : Short = 1
  val F : Short = 0

  def apply(s: Short) = s match {
    case 0 => false
    case 1 => true
    case _ => throw new IllegalStateException("Converting short to correct value was [" + s + "] must be in { 0 -> false, 1 -> true }, coding error")
  }

  def apply(b: Boolean) : Short = if(b) 1 else 0

}
