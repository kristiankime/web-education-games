package service

import scala.slick.lifted.MappedTypeMapper
import models.id._

object Option2Access extends (Option[Access] => Access) {
	def apply(in: Option[Access]): Access = in match {
		case Some(access) => access
		case None => Non
	}
}

object Access {
	def toNum(access: Access): Short = access.v

	def fromNum(access: Short) = access match {
		case Own.v => Own
		case Edit.v => Edit
		case View.v => View
		case Non.v => Non
		case _ => throw new IllegalArgumentException("number " + access + " does not match an access level")
	}

	def better(a1: Access, a2: Access) = fromNum(math.max(toNum(a1).toInt, toNum(a2).toInt).toShort)

	def apply(in: Option[Access]): Access = in match {
		case Some(access) => access
		case None => Non
	}

	def apply(user: User, owner: UserId): Access = if (user.id == owner) { Own } else { Non }

	def apply(user: User, owner: User, in: Option[Access]): Access =
		if (user.id == owner.id) {
			Own
		} else {
			Access(in)
		}

	def accessMap[T](v: (T, User, Option[Access]))(implicit user: User) = (v._1, v._2, Access(user, v._2, v._3))
}

object AccessMapper {
	implicit def short2access = MappedTypeMapper.base[Access, Short](
		access => Access.toNum(access),
		short => Access.fromNum(short))
}

sealed abstract class Access {
	val v: Short
	def read: Boolean
	def write: Boolean
	def better(access: Access) = Access.better(this, access)
}

case object Own extends Access {
	val v = 40.toShort
	override def read = true
	override def write = true
}

case object Edit extends Access {
	val v = 30.toShort
	override def read = true
	override def write = true
}

case object View extends Access {
	val v = 20.toShort
	override def read = true
	override def write = false
}

case object Non extends Access { // This is called Non so as to avoid naming conflicts with the Option None
	val v = 10.toShort
	override def read = false
	override def write = false
}