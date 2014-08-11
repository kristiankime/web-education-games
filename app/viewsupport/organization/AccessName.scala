package viewsupport.organization

import service._

object AccessName {
	def apply(access: Access) = access match {
		case Own => "Administrator"
		case Edit => "Teacher"
		case View => "Student"
		case Non => "None"
	}

  def an(access: Access) = access match {
    case Own => "an: Administrator"
    case Edit => "a: Teacher"
    case View => "a: Student"
    case Non => "a: n/a"
  }
}