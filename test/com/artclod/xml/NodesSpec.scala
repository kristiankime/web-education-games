package com.artclod.xml

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class NodesSpec extends Specification {

	"nodeCount" should {

		"be 1 for a single element" in {
			Nodes.nodeCount(<a></a>) must beEqualTo(1)
		}

		"be 3 for an element with two children" in {
			Nodes.nodeCount(<top><a/><b/></top>) must beEqualTo(3)
		}

		"be 4 for an element with two children, one of which has a child" in {
			Nodes.nodeCount(<top><a><asub/></a><b/></top>) must beEqualTo(4)
		}
	}

}