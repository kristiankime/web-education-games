package service

import scala.slick.lifted.MappedTypeMapper

object Option2Access extends (Option[Access] => Access) {
	def apply(in: Option[Access]): Access = in match {
		case Some(access) => access
		case None => Non
	}
}

object Access {
	def toNum(access: Access): Short = access match {
		case Own => 40
		case Edit => 30
		case View => 20
		case Non => 10
	}

	def fromNum(access: Short) = access match {
		case 40 => Own
		case 30 => Edit
		case 20 => View
		case 10 => Non
		case _ => throw new IllegalArgumentException("number " + access + " does not match an access level")
	}

	def better(a1: Access, a2: Access) = fromNum(math.max(toNum(a1).toInt, toNum(a2).toInt).toShort)

	def apply(in: Option[Access]): Access = in match {
		case Some(access) => access
		case None => Non
	}

	def apply(user: User, owner: User, in: Option[Access]): Access =
		if (user.id == owner.id) {
			Own
		} else {
			Access(in)
		}

	def accessMap[T](v: (T, User, Option[Access]))(implicit user: User) = (v._1, v._2, Access(user, v._2, v._3))

//	def accessMap[T](v: (T, User, Option[Access]), minimumAccess: Access)(implicit user: User) = (v._1, v._2, better(Access(user, v._2, v._3), minimumAccess))
}

object AccessMapper {
	implicit def short2access = MappedTypeMapper.base[Access, Short](
		access => Access.toNum(access),
		short => Access.fromNum(short))
}

sealed abstract class Access {
	def read: Boolean
	def write: Boolean
	def better(access: Access) = Access.better(this, access)
}

case object Own extends Access {
	override def read = true
	override def write = true
}

case object Edit extends Access {
	override def read = true
	override def write = true
}

case object View extends Access {
	override def read = true
	override def write = false
}

case object Non extends Access { // This is called Non so as to avoid naming conflicts with the Option None
	override def read = false
	override def write = false
}