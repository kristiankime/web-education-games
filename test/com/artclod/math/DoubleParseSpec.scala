package com.artclod.math

import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._

import scala.math.{Pi => π}
import scala.util.Success

@RunWith(classOf[JUnitRunner])
class DoubleParseSpec extends Specification {

  "DoubleParse.apply(String)" should {
    "parse inf" in { DoubleParse("inf") must beEqualTo( Success(Double.PositiveInfinity)) }
    "parse Inf" in { DoubleParse("Inf") must beEqualTo( Success(Double.PositiveInfinity)) }
    "parse infinity" in { DoubleParse("infinity") must beEqualTo( Success(Double.PositiveInfinity)) }
    "parse positive infinity" in { DoubleParse("positive infinity") must beEqualTo( Success(Double.PositiveInfinity)) }
    "parse pos inf" in { DoubleParse("pos inf") must beEqualTo( Success(Double.PositiveInfinity)) }
    "parse +inf" in { DoubleParse("+inf") must beEqualTo( Success(Double.PositiveInfinity)) }

    "parse negative infinity" in { DoubleParse("negative infinity") must beEqualTo( Success(Double.NegativeInfinity)) }
    "parse neg inf" in { DoubleParse("neg inf") must beEqualTo( Success(Double.NegativeInfinity)) }
    "parse -inf" in { DoubleParse("-inf") must beEqualTo( Success(Double.NegativeInfinity)) }

    "parse 3" in { DoubleParse("3") must beEqualTo( Success(3d)) }
    "parse 3.1" in { DoubleParse("3.1") must beEqualTo( Success(3.1d)) }
  }

}
