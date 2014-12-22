package com.artclod.javascript

import java.io.{FileInputStream, InputStreamReader}

import org.mozilla.javascript._
import play.api.Play
import play.api.Play.current

// modified from https://github.com/mozilla/rhino/blob/master/examples/RunScript.java
object RunJS {
  // Rhino code modified from from http://stackoverflow.com/questions/650377/javascript-rhino-use-library-or-include-other-scripts
  // Play resource code modified from http://stackoverflow.com/questions/27115924/play-framework-get-asset-server-side
  def loadJSLibrary(pathToLib: String, libName: String)(implicit context: Context, scope: ScriptableObject) = {
    val file = Play.application.getExistingFile(pathToLib + libName).get // TODO better error handling
    val fileInputStream = new FileInputStream(file)
    val inputStreamReader = new InputStreamReader(fileInputStream)
    context.evaluateReader(scope, inputStreamReader, libName, 1, null)
  }

  def mathJS2Tex(math: String) = {
    implicit val cx = Context.enter()
    try {
      implicit val scope = cx.initStandardObjects()
      loadJSLibrary("/public/javascripts/mathjs/", "math.js")
      val result = cx.evaluateString(scope, "math.parse(\""  + math + "\").toTex()", "<cmd>", 1, null)
      Context.toString(result)
    } finally {
      Context.exit()
    }
  }

}
