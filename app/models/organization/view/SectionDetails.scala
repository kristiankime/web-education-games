package models.organization.view

import models.organization.Section
import service.User
import service.Access

case class SectionDetails(s: Section, a: Access)