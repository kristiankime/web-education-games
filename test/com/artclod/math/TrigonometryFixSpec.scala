package com.artclod.math

import scala.math.{Pi => π, _}
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test._
import com.artclod.math.TrigonometryFix._

@RunWith(classOf[JUnitRunner])
class TrigonometryFixSpec extends Specification {

  "cos0" should {
    "return  1 for     0" in { cos0(0d)    must beEqualTo(1d) }
    "return  0 for   π/2" in { cos0(π/2)   must beEqualTo(0d) }
    "return -1 for     π" in { cos0(π)     must beEqualTo(-1d) }
    "return  0 for π*3/2" in { cos0(π*3/2) must beEqualTo(0d) }
  }

  "sin0" should {
    "return  0 for     0" in { sin0(0d)    must beEqualTo(0d) }
    "return  1 for   π/2" in { sin0(π/2)   must beEqualTo(1d) }
    "return  0 for     π" in { sin0(π)     must beEqualTo(0d) }
    "return -1 for π*3/2" in { sin0(π*3/2) must beEqualTo(-1d) }
  }

  "tan0" should {
    "return  0 for     0" in { tan0(0d)    must beEqualTo(0d) }
    "return  1 for   π/2" in { tan0(π/2)   must beEqualTo(Double.PositiveInfinity) }
    "return  0 for     π" in { tan0(π)     must beEqualTo(0d) }
    "return -1 for π*3/2" in { tan0(π*3/2) must beEqualTo(Double.PositiveInfinity) }
  }
}
