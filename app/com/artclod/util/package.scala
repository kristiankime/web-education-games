package com.artclod

package object util {

  implicit class TypeSafeEquals[T](a:T) {
    def ===(b: T) = { a == b }

    def !==(b: T) = { a != b }
  }

  def eitherOp[A, B](opA: Option[A], opB: Option[B]) = (opA, opB) match {
    case (_, Some(b)) => Right(b)
    case (Some(a), None) => Left(a)
    case (None, None) => throw new IllegalArgumentException("neither options had values")
  }

  implicit class EitherEnhanced[A, B](e: Either[A, B]) {
    def leftOp = e.left.toOption

    def leftOp[X](f: A => X) = e.left.toOption.map(f(_))

    def rightOp = e.right.toOption

    def rightOp[X](f: B => X) = e.right.toOption.map(f(_))
  }

  implicit class EitherCombine1[L, R1](e: Either[L, R1]) {
    def +[R](o: Either[L, R]) =
      (e, o) match {
        case (Left(l), _) => Left(l)
        case (_, Left(l)) => Left(l)
        case (Right(a), Right(b)) => Right((a, b))
    }
  }

  implicit class EitherCombine2[L, R1, R2](e: Either[L, (R1, R2)]) {
    def +[R](o: Either[L, R]) =
      (e, o) match {
        case (Left(l), _) => Left(l)
        case (_, Left(l)) => Left(l)
        case (Right(a), Right(b)) => Right((a._1, a._2, b))
      }
  }

  implicit class EitherCombine3[L, R1, R2, R3](e: Either[L, (R1, R2, R3)]) {
    def +[R](o: Either[L, R]) =
      (e, o) match {
        case (Left(l), _) => Left(l)
        case (_, Left(l)) => Left(l)
        case (Right(a), Right(b)) => Right((a._1, a._2, a._3, b))
      }
  }
}
