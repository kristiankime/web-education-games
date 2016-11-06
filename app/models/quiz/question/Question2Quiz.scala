package models.quiz.question

import models.support.{QuizId, UserId, QuestionId}
import org.joda.time.DateTime


case class Question2Quiz(questionId: QuestionId, quizId: QuizId, ownerId: UserId, creationDate: DateTime, order: Int)
