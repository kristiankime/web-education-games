package models.id

import scala.slick.lifted.MappedTypeMapper

object Ids {

	implicit def long2uid = MappedTypeMapper.base[UID, Long](
		userId => userId.v,
		long => UID(long))

}

/**
 * User Id
 */
case class UID(v: Long)
	

