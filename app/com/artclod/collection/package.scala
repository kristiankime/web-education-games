package com.artclod

package object collection {

	implicit class PimpedSeq[E](seq: Seq[E]) {
		def elementAfter(e: E) = {
			val index = seq.indexOf(e)
			if (index == -1) { None }
			else if (index == (seq.size - 1)) { None }
			else { Some(seq(index + 1)) }
		}
	}

}