package com.artclod.markup

import com.artclod.math.DoubleParse
import org.specs2.mutable.Specification

import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.templates.Html
import specs2.html

import scala.slick.collection.heterogenous.Zero.+
import scala.util.Success

@RunWith(classOf[JUnitRunner])
class LaikaParser$Test extends Specification {

  "apply(String)" should {

    "parse plain text into paragraph" in {
      LaikaParser("hello").get.toString() must beEqualTo("<p>hello</p>")
    }

    "parse markdown" in {
      LaikaParser("# H1").get.toString() must beEqualTo("\n<h1>H1</h1>")
    }

    "allow ASCII math" in {
      LaikaParser("""function: \`x+2\`""").get.toString() must beEqualTo("""<p>function: `x+2`</p>""")
    }

    "allow tex math" in {
      LaikaParser("""function: $$x+2$$""").get.toString() must beEqualTo("""<p>function: $$x+2$$</p>""")
    }
  }

}
