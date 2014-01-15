package models

import scala.slick.lifted.MappedTypeMapper
import play.api.mvc._
import scala.util.Try

// For path binding example http://julien.richard-foy.fr/blog/2012/04/09/how-to-implement-a-custom-pathbindable-with-play-2/
// For Query string binding check out https://gist.github.com/julienrf/2344517
package object id {

	implicit def long2userId = MappedTypeMapper.base[UserId, Long](
		id => id.v,
		long => UserId(long))

	implicit def userIdPathBindable(implicit longBinder: PathBindable[Long]) = new PathBindable[UserId] {
		def bind(key: String, value: String): Either[String, UserId] = {
			try { Right(UserId(value.toLong)) }
			catch { case e: NumberFormatException => Left("Could not parse " + value + " as a UserId => " + e.getMessage) }
		}

		def unbind(key: String, id: UserId): String = longBinder.unbind(key, id.v)
	}

	implicit def long2courseId = MappedTypeMapper.base[CourseId, Long](
		id => id.v,
		long => CourseId(long))

	implicit def courseIdPathBindable(implicit longBinder: PathBindable[Long]) = new PathBindable[CourseId] {
		def bind(key: String, value: String): Either[String, CourseId] = {
			try { Right(CourseId(value.toLong)) }
			catch { case e: NumberFormatException => Left("Could not parse " + value + " as a CourseId => " + e.getMessage) }
		}

		def unbind(key: String, id: CourseId): String = longBinder.unbind(key, id.v)
	}

	implicit def long2sectionId = MappedTypeMapper.base[SectionId, Long](
		id => id.v,
		long => SectionId(long))

	implicit def sectionIdPathBindable(implicit longBinder: PathBindable[Long]) = new PathBindable[SectionId] {
		def bind(key: String, value: String): Either[String, SectionId] = {
			try { Right(SectionId(value.toLong)) }
			catch { case e: NumberFormatException => Left("Could not parse " + value + " as a SectionId => " + e.getMessage) }
		}

		def unbind(key: String, id: SectionId): String = longBinder.unbind(key, id.v)
	}

	implicit def long2quizId = MappedTypeMapper.base[QuizId, Long](
		id => id.v,
		long => QuizId(long))

	implicit def quizIdPathBindable(implicit longBinder: PathBindable[Long]) = new PathBindable[QuizId] {
		def bind(key: String, value: String): Either[String, QuizId] = {
			try { Right(QuizId(value.toLong)) }
			catch { case e: NumberFormatException => Left("Could not parse " + value + " as a QuizId => " + e.getMessage) }
		}

		def unbind(key: String, id: QuizId): String = longBinder.unbind(key, id.v)
	}

	implicit def quizIdQueryStringBindable(implicit longBinder: QueryStringBindable[Long]) = new QueryStringBindable[QuizId] {
		def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, QuizId]] = {
			for { eitherQuiz <- longBinder.bind(key, params) } yield {
				eitherQuiz match {
					case Right(long) => Right(QuizId(long))
					case _ => Left("Unable to bind a QuizId for key " + key)
				}
			}
		}

		def unbind(key: String, id: QuizId): String = longBinder.unbind(key, id.v)
	}

	implicit def long2questionId = MappedTypeMapper.base[QuestionId, Long](
		id => id.v,
		long => QuestionId(long))

	implicit def questionIdPathBindable(implicit longBinder: PathBindable[Long]) = new PathBindable[QuestionId] {
		def bind(key: String, value: String): Either[String, QuestionId] = {
			try { Right(QuestionId(value.toLong)) }
			catch { case e: NumberFormatException => Left("Could not parse " + value + " as a QuestionId => " + e.getMessage) }
		}

		def unbind(key: String, id: QuestionId): String = longBinder.unbind(key, id.v)
	}

	implicit def long2answerId = MappedTypeMapper.base[AnswerId, Long](
		id => id.v,
		long => AnswerId(long))

	implicit def answerIdPathBindable(implicit longBinder: PathBindable[Long]) = new PathBindable[AnswerId] {
		def bind(key: String, value: String): Either[String, AnswerId] = {
			try { Right(AnswerId(value.toLong)) }
			catch { case e: NumberFormatException => Left("Could not parse " + value + " as a AnswerId => " + e.getMessage) }
		}

		def unbind(key: String, id: AnswerId): String = longBinder.unbind(key, id.v)
	}
}

