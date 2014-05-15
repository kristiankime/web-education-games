package com.artclod.util

sealed abstract class OneOf[+A, +B, +C] {
  def First = OneOf.FirstProjection(this)

  def Second = OneOf.SecondProjection(this)

  def Third = OneOf.ThirdProjection(this)

  def fold[X](fa: A => X, fb: B => X, fc: C => X) = this match {
    case First(a) => fa(a)
    case Second(b) => fb(b)
    case Third(c) => fc(c)
  }

  def isFirst: Boolean

  def isSecond: Boolean

  def isThird: Boolean
}

final case class First[+A, +B, +C](a: A) extends OneOf[A, B, C] {
  def isFirst = true

  def isSecond = false

  def isThird = false
}

final case class Second[+A, +B, +C](b: B) extends OneOf[A, B, C] {
  def isFirst = false

  def isSecond = true

  def isThird = false
}

final case class Third[+A, +B, +C](c: C) extends OneOf[A, B, C] {
  def isFirst = false

  def isSecond = false

  def isThird = true
}


object OneOf {

  final case class FirstProjection[+A, +B, +C](e: OneOf[A, B, C]) {
    def get = e match {
      case First(a) => a
      case Second(_) => throw new NoSuchElementException("OneOf.left.value not first")
      case Third(_) => throw new NoSuchElementException("OneOf.left.value not first")
    }

    def foreach[U](f: A => U) = e match {
      case First(a) => f(a)
      case Second(_) => {}
      case Third(_) => {}
    }

    def getOrElse[AA >: A](or: => AA) = e match {
      case First(a) => a
      case Second(_) => or
      case Third(_) => or
    }

    def forall(f: A => Boolean) = e match {
      case First(a) => f(a)
      case Second(_) => true
      case Third(_) => true
    }

    def exists(f: A => Boolean) = e match {
      case First(a) => f(a)
      case Second(_) => false
      case Third(_) => false
    }

    def flatMap[X, BB >: B, Y](f: A => OneOf[X, BB, Y]) = e match {
      case First(a) => f(a)
      case Second(b) => Second(b)
      case Third(c) => Third(c)
    }

    def map[X](f: A => X) = e match {
      case First(a) => First(f(a))
      case Second(b) => Second(b)
      case Third(c) => Third(c)
    }

    def filter[Y, Z](p: A => Boolean): Option[OneOf[A, Y, Z]] = e match {
      case First(a) => if (p(a)) Some(First(a)) else None
      case Second(b) => None
      case Third(c) => None
    }

    def toSeq = e match {
      case First(a) => Seq(a)
      case Second(_) => Seq.empty
      case Third(_) => Seq.empty
    }

    def toOption = e match {
      case First(a) => Some(a)
      case Second(_) => None
      case Third(_) => None
    }
  }


  final case class SecondProjection[+A, +B, +C](e: OneOf[A, B, C]) {
    def get = e match {
      case First(_) => throw new NoSuchElementException("OneOf.left.value not second")
      case Second(b) => b
      case Third(_) => throw new NoSuchElementException("OneOf.left.value not second")
    }

    def foreach[U](f: B => U) = e match {
      case First(_) => {}
      case Second(b) => f(b)
      case Third(_) => {}
    }
  }

  final case class ThirdProjection[+A, +B, +C](e: OneOf[A, B, C]) {
    def get = e match {
      case First(_) => throw new NoSuchElementException("OneOf.left.value not second")
      case Second(_) => throw new NoSuchElementException("OneOf.left.value not second")
      case Third(c) => c
    }

    def foreach[U](f: C => U) = e match {
      case First(_) => {}
      case Second(_) => {}
      case Third(c) => f(c)
    }
  }
}