package models.id

import scala.slick.lifted.MappedTypeMapper

object Ids {

	implicit def long2uid = MappedTypeMapper.base[UID, Long](
		id => id.v,
		long => UID(long))

	implicit def long2courseId = MappedTypeMapper.base[CourseId, Long](
		id => id.v,
		long => CourseId(long))

	implicit def long2sectionId = MappedTypeMapper.base[SectionId, Long](
		id => id.v,
		long => SectionId(long))

	implicit def long2quizId = MappedTypeMapper.base[QuizId, Long](
		id => id.v,
		long => QuizId(long))

	implicit def long2questionId = MappedTypeMapper.base[QuestionId, Long](
		id => id.v,
		long => QuestionId(long))

	implicit def long2answerId = MappedTypeMapper.base[AnswerId, Long](
		id => id.v,
		long => AnswerId(long))
}

/**
 * User Id
 */
case class UID(v: Long)

case class CourseId(v: Long)

case class SectionId(v: Long)

case class QuizId(v: Long)

case class QuestionId(v: Long)

case class AnswerId(v: Long)
