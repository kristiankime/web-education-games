package models.quiz.question

import com.google.common.annotations.VisibleForTesting


object QuestionScoring {
  @VisibleForTesting
  val zoneOfProximalDevelopmentFactor = 1.25

  def teacherScore(question: DerivativeQuestion, correct: Boolean, studentSkillLevel: Double) : Double = teacherScore(QuestionDifficulty(question.mathML), correct, studentSkillLevel)

  def teacherScore(difficulty : Double, correct: Boolean, studentSkillLevel: Double) : Double = {
    val l = studentSkillLevel
    val d = difficulty
    val z = l * zoneOfProximalDevelopmentFactor // zoneOfProximalDevelopment
    val s = math.min(d, z) / z // Scoring

    if(correct) s else 1d - s
  }
}
