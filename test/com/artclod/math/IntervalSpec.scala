package com.artclod.math

import com.artclod.math.TrigonometryFix._
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._

import scala.math.{Pi => π}

@RunWith(classOf[JUnitRunner])
class IntervalSpec extends Specification {

  "Range.apply(String)" should {
    "parse integers in parens"                   in { Interval("(2, 4)")             must beEqualTo( Some(Interval(2,4)) )         }
    "parse doubles in parens"                    in { Interval("(2.4, 4.7)")         must beEqualTo( Some(Interval(2.4d, 4.7d)) )  }
    "parser ignores whitespace"                  in { Interval("  (   2  ,   5  ) ") must beEqualTo( Some(Interval(2, 5)) )        }
    "parse infinities"                           in { Interval("(-Inf, Inf)")        must beEqualTo( Some(Interval(Double.NegativeInfinity, Double.PositiveInfinity)) )  }
    "parser fails if parens are absent"          in { Interval("2, 4")               must beNone                                   }
    "parser fails on extra paren"                in { Interval("(2, 4))")            must beNone                                   }
    "parser fails if a number is missing"        in { Interval("(2)")                must beNone                                   }
    "parser fails if there are too many numbers" in { Interval("(2,3,4)")            must beNone                                   }
  }


  "overlap" should {
    "return false if a is completely before b" in { Interval(1,2).overlap(Interval(3,4))  must beFalse }
    "return false if b is completely before a" in { Interval(3,4).overlap(Interval(1,2))  must beFalse }
    "return false if a touches the end of b" in {   Interval(2,3).overlap(Interval(1,2))  must beFalse }
    "return false if b touches the end of a" in {   Interval(1,2).overlap(Interval(2,3))  must beFalse }
    "return false if a touches the start of b" in { Interval(1,2).overlap(Interval(2,3))  must beFalse }
    "return false if b touches the start of a" in { Interval(2,3).overlap(Interval(1,2))  must beFalse }
    "return true if a lower == b lower" in {        Interval(1,2).overlap(Interval(1,20)) must beTrue }
    "return true if b lower == a lower" in {        Interval(1,20).overlap(Interval(1,2)) must beTrue }
    "return true if a upper == b upper" in {        Interval(-1,2).overlap(Interval(1,2)) must beTrue }
    "return true if b upper == a upper" in {        Interval(1,2).overlap(Interval(-1,2)) must beTrue }
    "return true if a contains b" in {              Interval(1,10).overlap(Interval(3,4)) must beTrue }
    "return true if b contains a" in {              Interval(3,4).overlap(Interval(1,10)) must beTrue }
    "return true if a upper in b" in {              Interval(3,7).overlap(Interval(5,10)) must beTrue }
    "return true if b upper in a" in {              Interval(5,10).overlap(Interval(3,7)) must beTrue }
    "return true if a lower in b" in {              Interval(3,7).overlap(Interval(1,5))  must beTrue }
    "return true if b lower in a" in {              Interval(1,5).overlap(Interval(3,7))  must beTrue }
  }

  "overlap (sequence)" should {
    "return false if no overlap" in { Interval.overlap(Vector(Interval(1, 2), Interval(3, 4))) must beFalse }
    "return true if overlap" in { Interval.overlap(Vector(Interval(2, 4), Interval(1, 2), Interval(3, 4))) must beTrue }
  }

}
