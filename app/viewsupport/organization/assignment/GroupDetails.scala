package viewsupport.organization.assignment

import models.organization.assignment.Group
import service.User

case class GroupDetails(group: Group, students: List[User])