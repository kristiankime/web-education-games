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
class LaikaParserSpec extends Specification {

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

  "replaceSpecials" should {

    "replace first match with 0" in {
      val (replaced, map) = LaikaParser.replaceSpecials("hello $$zero$$ bye", LaikaParser.mt)

      replaced must beEqualTo("hello $$0$$ bye")
      map must beEqualTo(Map("$$0$$" -> "$$zero$$"))
    }

    "replace all matches with sequential numbers" in {
      val (replaced, map) = LaikaParser.replaceSpecials("a $$zero$$ b $$one$$ c $$two$$ d", LaikaParser.mt)

      replaced must beEqualTo("a $$0$$ b $$1$$ c $$2$$ d")
      map must beEqualTo(Map("$$0$$" -> "$$zero$$", "$$1$$" -> "$$one$$", "$$2$$" -> "$$two$$"))
    }

    "handle the case where the string starts with a special" in {
      val (replaced, map) = LaikaParser.replaceSpecials("$$zero$$ b $$one$$ c $$two$$", LaikaParser.mt)

      replaced must beEqualTo("$$0$$ b $$1$$ c $$2$$")
      map must beEqualTo(Map("$$0$$" -> "$$zero$$", "$$1$$" -> "$$one$$", "$$2$$" -> "$$two$$"))
    }

    "handle the case where the string ends with a special" in {
      val (replaced, map) = LaikaParser.replaceSpecials("a $$zero$$ b $$one$$ c $$two$$", LaikaParser.mt)

      replaced must beEqualTo("a $$0$$ b $$1$$ c $$2$$")
      map must beEqualTo(Map("$$0$$" -> "$$zero$$", "$$1$$" -> "$$one$$", "$$2$$" -> "$$two$$"))
    }
  }

}
