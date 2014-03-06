package com.artclod

package object collection {

	implicit class PimpedSeq[E](t: Seq[E]) {
		def elementAfter(e: E) = {
			val index = t.indexOf(e)
			if (index == -1) { None }
			else if (index == (t.size - 1)) { None }
			else { Some(t(index + 1)) }
		}
	}

}