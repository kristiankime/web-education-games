package service

import scala.slick.lifted.MappedTypeMapper

object Access {
	def toNum(access: Access): Short = access match {
		case Own => 4
		case Edit => 3
		case View => 2
		case Non => 1
	}

	def fromNum(access: Short) = access match {
		case 4 => Own
		case 3 => Edit
		case 2 => View
		case 1 => Non
		case _ => throw new IllegalArgumentException("number " + access + " does not match an access level")
	}
	
	def better(a1: Access, a2: Access) = fromNum(math.max(toNum(a1).toInt, toNum(a2).toInt).toShort)
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