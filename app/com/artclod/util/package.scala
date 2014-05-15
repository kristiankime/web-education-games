package com.artclod

package object util {

  def eitherOp[A, B](opA:Option[A], opB:Option[B]) = (opA, opB) match {
    case (Some(a), Some(b)) => throw new IllegalArgumentException("both options had values")
    case (Some(a), None) => Left(a)
    case (None, Some(b)) => Right(b)
    case (None, None) => throw new IllegalArgumentException("neither options had values")
  }

  implicit class EitherEnhanced[A, B](e : Either[A, B]) {
    def leftOp = e.left.toOption

    def leftOp[X](f: A => X ) = e.left.toOption.map(f(_))

    def rightOp = e.right.toOption

    def rightOp[X](f: B => X ) = e.right.toOption.map(f(_))
  }

}
