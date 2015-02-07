

package object service {

  implicit class OptionAccessPimped(val v: Option[Access]) {
    def toAccess = Access(v)
  }

}