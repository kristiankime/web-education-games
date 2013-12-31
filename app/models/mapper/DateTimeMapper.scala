package models.mapper

import slick.lifted.MappedTypeMapper
import java.sql.Date
import org.joda.time.DateTime

// taken from https://gist.github.com/dragisak/4756344
object DateTimeMapper {

	implicit def date2dateTime = MappedTypeMapper.base[DateTime, Date](
		dateTime => new Date(dateTime.getMillis),
		date => new DateTime(date))

}