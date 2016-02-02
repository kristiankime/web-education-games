package models.support

import com.artclod.math.Interval
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._

import scala.math.{Pi => π}

@RunWith(classOf[JUnitRunner])
class PackageSpec extends Specification {

  "s2VI" should {
    "turn int pair vector into string (1 pair)" in  { vectorInterval2String( Vector(Interval(1, 2)))         must beEqualTo("(1,2)")       }
    "turn int pair vector into string (2 pairs)" in { vectorInterval2String( Vector(Interval(1, 2), Interval(3, 4))) must beEqualTo("(1,2),(3,4)") }
  }

  "vI2s" should {
    "turn string into int pair vector (1 pair)" in  { string2VectorInterval( "(1,2)" )      must beEqualTo( Vector(Interval(1, 2)))          }
    "turn string into int pair vector (2 pairs)" in { string2VectorInterval( "(1,2),(3,4)") must beEqualTo( Vector(Interval(1, 2), Interval(3, 4)) ) }
  }

}
