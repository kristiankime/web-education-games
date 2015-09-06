package com.artclod

package object tuple {

  implicit class MapTuple2[T1, T2](t :(T1, T2)){
    def map[O](f : ((T1, T2)) => O) = f(t)
  }

  implicit class MapTuple3[T1, T2, T3](t :(T1, T2, T3)){
    def map[O](f : ((T1, T2, T3)) => O) = f(t)
  }

  implicit class MapTuple4[T1, T2, T3, T4](t :(T1, T2, T3, T4)){
    def map[O](f : ((T1, T2, T3, T4)) => O) = f(t)
  }

  implicit class MapTuple5[T1, T2, T3, T4, T5](t :(T1, T2, T3, T4, T5)){
    def map[O](f : ((T1, T2, T3, T4, T5)) => O) = f(t)
  }

  implicit class MapTuple6[T1, T2, T3, T4, T5, T6](t :(T1, T2, T3, T4, T5, T6)){
    def map[O](f : ((T1, T2, T3, T4, T5, T6)) => O) = f(t)
  }

}
