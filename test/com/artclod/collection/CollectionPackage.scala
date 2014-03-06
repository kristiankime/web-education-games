package com.artclod.collection

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml._
import mathml.scalar._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class CollectionPackageSpec extends Specification {

	"elementAfter" should {
		"should return next element" in {
			List("A", "B", "C").elementAfter("B") must beEqualTo(Some("C"))
		}

		"should return None if element is the last element" in {
			List("A", "B", "C").elementAfter("C") must beEqualTo(None)
		}
		
		"should return None if element is not in the list" in {
			List("A", "B", "C").elementAfter("Not in the list") must beEqualTo(None)
		}
	}

}