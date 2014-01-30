package models.organization.view

import models.organization.Section
import service.User
import service.Access

case class SectionDetails(s: Section, owner: User, a: Access)

object SectionDetails {
	def apply(v: (Section, User, Access)): SectionDetails = SectionDetails(v._1, v._2, v._3)
}