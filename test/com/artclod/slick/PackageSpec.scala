package com.artclod.slick

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PackageSpec extends Specification{

  "listGroupBy" should {

    "return empty if input is empty" in {
      val grouped = listGroupBy(List[(Int, String)]())(_._1, _._2)

      grouped.length must beEqualTo(0)
    }

    "return groups by key function" in {
      val grouped = listGroupBy(List((1, "a"), (1, "b"), (2, "foo")))(_._1, _._2)

      grouped must beEqualTo(List( ListGroup(1, List("a", "b")), ListGroup(2, List("foo")))
      )
    }

  }

}
