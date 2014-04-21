package com.artclod.random

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification
import scala.util.Random

@RunWith(classOf[JUnitRunner])
class RandomSpec extends Specification{

  "pickNFrom" should {

    "return the right number of elements (aka N)" in {
      val picked = pickNFrom(2, new Random(0L))((1 to 10).toVector)
      picked.length must beEqualTo(2)
    }

    "return elements should be from available elements" in {
      val picked = pickNFrom(3, new Random(0L))((1 to 10).toVector)
      picked(0) must beBetween(1, 10)
      picked(1) must beBetween(1, 10)
      picked(2) must beBetween(1, 10)
    }

    "return N elements if N is > the number of available elements" in {
      val picked = pickNFrom(5, new Random(0L))((1 to 2).toVector)
      picked.length must beEqualTo(2)
    }

    "return all the elements if N is = the number of available elements" in {
      val picked = pickNFrom(3, new Random(0L))((1 to 3).toVector)
      picked must contain(1)
      picked must contain(2)
      picked must contain(3)
    }

  }

  "pick2From" should {

    "return 2 elements from the available" in {
      val picked = (1 to 10).toVector.pick2From(new Random(0L))
      picked._1 must beBetween(1, 10)
      picked._2 must beBetween(1, 10)
    }

    "error with only 1 element" in {
      Vector(1).pick2From(new Random(0L)) must throwA[IllegalArgumentException]
    }
  }

}
