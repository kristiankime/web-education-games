package com.artclod.mathml

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import views.html.tag.name
import com.artclod.mathml.{TextToHtmlGraph => m}

@RunWith(classOf[JUnitRunner])
class TextToHtmlGraph$Test extends Specification {

  "objTry" should {

    "parse text into object" in {
      val objTry = TextToHtmlGraph.from( """{ "name": "name", "function": "x+2", "titleOp" : "title", "glider": false, "xSize": 301, "ySize": 302 }""")
      objTry.get must beEqualTo( TextToHtmlGraph("name", "x+2", Some("title"), Some(false), Some(301), Some(302)) )
    }

    "parse text into object (does not require optional fields)" in {
      val objTry = TextToHtmlGraph.from( """{ "name": "name", "function": "x+1" }""")
      objTry.get must beEqualTo( TextToHtmlGraph("name", "x+1") )
    }

  }

  "replace" should {

    "replace ?? xxx ?? with html graph" in {
      val replaced = TextToHtmlGraph.replaceGraph("""Some text ?? "name": "name", "function": "x^2" ?? more text""")
      replaced must beEqualTo("Some text " + views.html.mathml.graph("name", "x^2", None, true, 300, 300) +  " more text")
    }

  }

}
