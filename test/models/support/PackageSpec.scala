package models.support

import com.artclod.math.Interval
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import Double.{PositiveInfinity => ∞, NegativeInfinity => -∞}

@RunWith(classOf[JUnitRunner])
class PackageSpec extends Specification {

  "vectorInterval2String" should {
    "turn int pair vector into string (1 pair)"  in { vectorInterval2String(Vector(Interval(1, 2)))                 must beEqualTo("(1,2)")       }
    "turn int pair vector into string (2 pairs)" in { vectorInterval2String(Vector(Interval(1, 2), Interval(3, 4))) must beEqualTo("(1,2),(3,4)") }
    "turn infinities into string"                in { vectorInterval2String(Vector(Interval(-∞, ∞)))                must beEqualTo("(-∞,∞)")      }
  }

  "string2VectorInterval" should {
    "turn string into int pair vector (1 pair)"  in { string2VectorInterval( "(1,2)" )           must beEqualTo(Vector(Interval(1, 2)))                  }
    "turn string into int pair vector (2 pairs)" in { string2VectorInterval( "(1,2),(3,4)")      must beEqualTo(Vector(Interval(1, 2), Interval(3, 4)))  }
    "handle infinities"                          in { string2VectorInterval( "(-Inf,2),(3,Inf)") must beEqualTo(Vector(Interval(-∞, 2), Interval(3, ∞))) }
  }

}
