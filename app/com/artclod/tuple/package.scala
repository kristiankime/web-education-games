package com.artclod

package object tuple {

  implicit class MapTuple2[T1, T2](t :(T1, T2)){
    def map[O](f : ((T1, T2)) => O) = f(t)
  }

  implicit class MapTuple3[T1, T2, T3](t :(T1, T2, T3)){
    def map[O](f : ((T1, T2, T3)) => O) = f(t)
  }
}
