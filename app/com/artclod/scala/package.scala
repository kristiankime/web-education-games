package com.artclod

package object scala {

  implicit class EnhancedEquals[T](v :T){
    def &=(o:T) = AndEquals(v, o == v)
  }

  case class AndEquals[T](v:T, status: Boolean){
    def &=(o:T) = AndEquals(v, status && (o == v))
  }

  implicit def AndEqualsToBoolean(v: AndEquals[_]) = v.status

}
