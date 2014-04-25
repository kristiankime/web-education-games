package com.artclod

import scala.slick.lifted._

package object slick {

  implicit class PimpedColumn[U](column: Column[U]){
    def ~:[O](c: Column[O]) = c ~ column
  }

  implicit class PimpedProjection2[T1,T2](p: Projection2[T1,T2]){
    def ~:[O](c: Column[O]) = new Projection3(c, p._1,p._2)
  }

  implicit class PimpedProjection3[T1,T2,T3](p: Projection3[T1,T2,T3]){
    def ~:[O](c: Column[O]) = new Projection4(c, p._1,p._2,p._3)
  }

  implicit class PimpedProjection4[T1,T2,T3,T4](p: Projection4[T1,T2,T3,T4]){
    def ~:[O](c: Column[O]) = new Projection5(c, p._1,p._2,p._3,p._4)
  }

  implicit class PimpedProjection5[T1,T2,T3,T4,T5](p: Projection5[T1,T2,T3,T4,T5]){
    def ~:[O](c: Column[O]) = new Projection6(c, p._1,p._2,p._3,p._4,p._5)
  }

  implicit class PimpedProjection6[T1,T2,T3,T4,T5,T6](p: Projection6[T1,T2,T3,T4,T5,T6]){
    def ~:[O](c: Column[O]) = new Projection7(c, p._1,p._2,p._3,p._4,p._5,p._6)
  }

  implicit class PimpedProjection7[T1,T2,T3,T4,T5,T6,T7](p: Projection7[T1,T2,T3,T4,T5,T6,T7]){
    def ~:[O](c: Column[O]) = new Projection8(c, p._1,p._2,p._3,p._4,p._5,p._6,p._7)
  }

}
