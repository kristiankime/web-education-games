package com.artclod.play

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class PlayPackageSpec extends Specification {

//  import com.artclod.play.collapse

	"collapse" should {
		"leave seq as is if there no duplicates" in {
      collapse(Seq('a -> "a", 'b -> "b", 'c -> "c")) must beEqualTo(Seq('a -> "a", 'b -> "b", 'c -> "c"))
		}

    "collapse duplicates in order of first appearance" in {
      collapse(Seq('a -> "a", 'b -> "b", 'c -> "c", 'a -> "+")) must beEqualTo(Seq('a -> "a +", 'b -> "b", 'c -> "c"))
    }

    "collapse multiple duplicates in order of first appearance" in {
      collapse(Seq('a -> "a", 'b -> "b", 'b -> "@", 'c -> "c", 'a -> "+", 'c -> "!")) must beEqualTo(Seq('a -> "a +", 'b -> "b @", 'c -> "c !"))
    }
	}

}