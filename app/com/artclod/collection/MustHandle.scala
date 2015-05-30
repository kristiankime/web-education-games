package com.artclod.collection


/**
 * The purpose of the MustHandle classes is for future proofing.
 * The intent is that you will initially construct a MustHandle from the object's apply method and then use the -> method.
 * Then later on add another element to the MustHandle.apply call.
 * At this point the code will fail to compile and you will get an error at the appropriate -> call and be forced to update the code there.
 */
object MustHandle {

  def apply[V1](v1: V1) = MustHandle1(v1)

  def apply[V1, V2](v1: V1, v2: V2) = MustHandle2(v1, v2)

  def apply[V1, V2, V3](v1: V1, v2: V2, v3: V3) = MustHandle3(v1, v2, v3)
}

case class MustHandle1[T1](v1: T1) {
  def ->[O1](f1: (T1) => O1) = f1(v1)
}

case class MustHandle2[T1, T2](v1: T1, v2: T2) {
  def ->[O1, O2](f1: (T1) => O1, f2: (T2) => O2) = (f1(v1), f2(v2))
}

case class MustHandle3[T1, T2, T3](v1: T1, v2: T2, v3: T3) {
  def ->[O1, O2, O3](f1: (T1) => O1, f2: (T2) => O2, f3: (T3) => O3) = (f1(v1), f2(v2), f3(v3))
}