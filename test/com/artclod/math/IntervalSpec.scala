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
    "parser fails if parens are absent"          in { Interval("2, 4")               must beNone                                   }
    "parser fails if a number is missing"        in { Interval("(2)")                must beNone                                   }
    "parser fails if there are too many numbers" in { Interval("(2,3,4)")            must beNone                                   }
  }

}
