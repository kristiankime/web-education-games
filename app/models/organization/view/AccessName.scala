package models.organization.view

import service._

object AccessName {
	def apply(access: Access) = access match {
		case Own => "Administrator"
		case Edit => "Teacher"
		case View => "Student"
		case Non => "None"
	}
}