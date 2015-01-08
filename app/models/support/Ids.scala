package models.support

case class UserId(v: Long) {
  override def toString = "U"+v
}

case class OrganizationId(v: Long) {
  override def toString = "O"+v
}

case class CourseId(v: Long) {
  override def toString = "C"+v
}

case class GameId(v: Long) {
  override def toString = "G"+v
}

case class QuizId(v: Long) {
  override def toString = "Qz"+v
}

case class QuestionId(v: Long) {
  override def toString = "Qn"+v
}

case class AnswerId(v: Long) {
  override def toString = "A"+v
}
