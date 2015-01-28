package com.artclod

package object collection {

  implicit class PimpedOptionList[T](val op: Option[List[T]]) {
    def dropOption: List[T] = op match {
      case Some(list) => list
      case None => List()
    }
  }

	implicit class PimpedSeq[E](seq: Seq[E]) {
		def elementAfter(e: E) = {
			val index = seq.indexOf(e)
			if (index == -1) { None }
			else if (index == (seq.size - 1)) { None }
			else { Some(seq(index + 1)) }
		}

    def elementBefore(e: E) = {
      val index = seq.indexOf(e)
      if (index == -1) { None }
      else if (index == 0) { None }
      else { Some(seq(index - 1)) }
    }
	}

  def takeTuple2[E](s : TraversableOnce[E]) : (E, E) = s match {
    case Seq(e1, e2, xs@_* ) => (e1, e2)
    case _ => throw new IllegalArgumentException("There were not enough elements in " + s)
  }

}