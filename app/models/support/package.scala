package models

import play.api.db.slick.Config.driver.simple._
import play.api.mvc._
import service._

/**
 * This object contains all the mappers/binders for the DB (TypeMapper) and the urls (PathBindable and QueryStringBindable)
 * 
 * For a path binding example http://julien.richard-foy.fr/blog/2012/04/09/how-to-implement-a-custom-pathbindable-with-play-2/
 * For a query string binding check out https://gist.github.com/julienrf/2344517
 */
package object support {

  trait HasId[I]{
    def id : I
  }

	// ==========================
	// Access
	// ==========================
	implicit def short2access = MappedColumnType.base[Access, Short](
		access => Access.toNum(access),
		short => Access.fromNum(short))

	// ==========================
	// UserId
	// ==========================
	implicit def long2userId = MappedColumnType.base[UserId, Long](
		id => id.v,
		long => UserId(long))

	implicit def userIdPathBindable(implicit longBinder: PathBindable[Long]) = new PathBindable[UserId] {
		def bind(key: String, value: String): Either[String, UserId] = {
			try { Right(UserId(value.toLong)) }
			catch { case e: NumberFormatException => Left("Could not parse " + value + " as a UserId => " + e.getMessage) }
		}

		def unbind(key: String, id: UserId): String = longBinder.unbind(key, id.v)
	}

  // ==========================
  // OrganizationId
  // ==========================
  implicit def long2organizationId = MappedColumnType.base[OrganizationId, Long](
    id => id.v,
    long => OrganizationId(long))

  implicit def organizationIdPathBindable(implicit longBinder: PathBindable[Long]) = new PathBindable[OrganizationId] {
    def bind(key: String, value: String): Either[String, OrganizationId] = {
      try { Right(OrganizationId(value.toLong)) }
      catch { case e: NumberFormatException => Left("Could not parse " + value + " as a OrganizationId => " + e.getMessage) }
    }

    def unbind(key: String, id: OrganizationId): String = longBinder.unbind(key, id.v)
  }

  implicit def organizationIdQueryStringBindable(implicit longBinder: QueryStringBindable[Long]) = new QueryStringBindable[OrganizationId] {
    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, OrganizationId]] = {
      for { either <- longBinder.bind(key, params) } yield {
        either match {
          case Right(long) => Right(OrganizationId(long))
          case _ => Left("Unable to bind a OrganizationId for key " + key)
        }
      }
    }

    def unbind(key: String, id: OrganizationId): String = longBinder.unbind(key, id.v)
  }

	// ==========================
	// CourseId
	// ==========================
	implicit def long2courseId = MappedColumnType.base[CourseId, Long](
		id => id.v,
		long => CourseId(long))

	implicit def courseIdPathBindable(implicit longBinder: PathBindable[Long]) = new PathBindable[CourseId] {
		def bind(key: String, value: String): Either[String, CourseId] = {
			try { Right(CourseId(value.toLong)) }
			catch { case e: NumberFormatException => Left("Could not parse " + value + " as a CourseId => " + e.getMessage) }
		}

		def unbind(key: String, id: CourseId): String = longBinder.unbind(key, id.v)
	}

	implicit def courseIdQueryStringBindable(implicit longBinder: QueryStringBindable[Long]) = new QueryStringBindable[CourseId] {
		def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, CourseId]] = {
			for { either <- longBinder.bind(key, params) } yield {
				either match {
					case Right(long) => Right(CourseId(long))
					case _ => Left("Unable to bind a CourseId for key " + key)
				}
			}
		}

		def unbind(key: String, id: CourseId): String = longBinder.unbind(key, id.v)
	}

	// ==========================
	// SectionId
	// ==========================
	implicit def long2sectionId = MappedColumnType.base[SectionId, Long](
		id => id.v,
		long => SectionId(long))

	implicit def sectionIdPathBindable(implicit longBinder: PathBindable[Long]) = new PathBindable[SectionId] {
		def bind(key: String, value: String): Either[String, SectionId] = {
			try { Right(SectionId(value.toLong)) }
			catch { case e: NumberFormatException => Left("Could not parse " + value + " as a SectionId => " + e.getMessage) }
		}

		def unbind(key: String, id: SectionId): String = longBinder.unbind(key, id.v)
	}
	
	implicit def sectionIdQueryStringBindable(implicit longBinder: QueryStringBindable[Long]) = new QueryStringBindable[SectionId] {
		def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SectionId]] = {
			for { either <- longBinder.bind(key, params) } yield {
				either match {
					case Right(long) => Right(SectionId(long))
					case _ => Left("Unable to bind a CourseId for key " + key)
				}
			}
		}

		def unbind(key: String, id: SectionId): String = longBinder.unbind(key, id.v)
	}

  // ==========================
  // AssignmentId
  // ==========================
  implicit def long2assignmentId = MappedColumnType.base[AssignmentId, Long](
    id => id.v,
    long => AssignmentId(long))

  implicit def assignmentIdPathBindable(implicit longBinder: PathBindable[Long]) = new PathBindable[AssignmentId] {
    def bind(key: String, value: String): Either[String, AssignmentId] = {
      try { Right(AssignmentId(value.toLong)) }
      catch { case e: NumberFormatException => Left("Could not parse " + value + " as a AssignmentId => " + e.getMessage) }
    }

    def unbind(key: String, id: AssignmentId): String = longBinder.unbind(key, id.v)
  }

  implicit def assignmentIdQueryStringBindable(implicit longBinder: QueryStringBindable[Long]) = new QueryStringBindable[AssignmentId] {
    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, AssignmentId]] = {
      for { either <- longBinder.bind(key, params) } yield {
        either match {
          case Right(long) => Right(AssignmentId(long))
          case _ => Left("Unable to bind a AssignmentId for key " + key)
        }
      }
    }

    def unbind(key: String, id: AssignmentId): String = longBinder.unbind(key, id.v)
  }

  // ==========================
  // AssignmentGroupId
  // ==========================
  implicit def long2assignmentGroupId = MappedColumnType.base[GroupId, Long](
    id => id.v,
    long => GroupId(long))

  implicit def assignmentGroupIdPathBindable(implicit longBinder: PathBindable[Long]) = new PathBindable[GroupId] {
    def bind(key: String, value: String): Either[String, GroupId] = {
      try { Right(GroupId(value.toLong)) }
      catch { case e: NumberFormatException => Left("Could not parse " + value + " as a AssignmentGroupId => " + e.getMessage) }
    }

    def unbind(key: String, id: GroupId): String = longBinder.unbind(key, id.v)
  }

  implicit def assignmentGroupIdQueryStringBindable(implicit longBinder: QueryStringBindable[Long]) = new QueryStringBindable[GroupId] {
    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, GroupId]] = {
      for { either <- longBinder.bind(key, params) } yield {
        either match {
          case Right(long) => Right(GroupId(long))
          case _ => Left("Unable to bind a AssignmentGroupId for key " + key)
        }
      }
    }

    def unbind(key: String, id: GroupId): String = longBinder.unbind(key, id.v)
  }

  // ==========================
	// QuizId
	// ==========================
	implicit def long2quizId = MappedColumnType.base[QuizId, Long](
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
			for { either <- longBinder.bind(key, params) } yield {
				either match {
					case Right(long) => Right(QuizId(long))
					case _ => Left("Unable to bind a QuizId for key " + key)
				}
			}
		}

		def unbind(key: String, id: QuizId): String = longBinder.unbind(key, id.v)
	}

	// ==========================
	// QuestionId
	// ==========================
	implicit def long2questionId = MappedColumnType.base[QuestionId, Long](
		id => id.v,
		long => QuestionId(long))

	implicit def questionIdPathBindable(implicit longBinder: PathBindable[Long]) = new PathBindable[QuestionId] {
		def bind(key: String, value: String): Either[String, QuestionId] = {
			try { Right(QuestionId(value.toLong)) }
			catch { case e: NumberFormatException => Left("Could not parse " + value + " as a QuestionId => " + e.getMessage) }
		}

		def unbind(key: String, id: QuestionId): String = longBinder.unbind(key, id.v)
	}

	// ==========================
	// AnswerId
	// ==========================
	implicit def long2answerId = MappedColumnType.base[AnswerId, Long](
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

