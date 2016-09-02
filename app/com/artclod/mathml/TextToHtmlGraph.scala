package com.artclod.mathml

import com.artclod.play.JsResultsError
import com.google.common.annotations.VisibleForTesting
import play.api.libs.json.{JsValue, Format, Json}
import play.api.templates.Html
import views.html
import com.artclod.util.TryUtil.{TryPimp, EitherPimp}
import com.artclod.play.JsResultPimp

import scala.util.Try

object TextToHtmlGraph {
  def q(text: String) = "\"" + text + "\"";
  val name = q("name")
  val function = q("function")
  val titleOp = q("titleOp")
  val glider = q("glider")
  val xSize = q("xSize")
  val ySize = q("ySize")

  implicit val formatDerivativeDifficultyRequest : Format[TextToHtmlGraph] = Json.format[TextToHtmlGraph]

  @VisibleForTesting
  def from(text: String): Try[TextToHtmlGraph] = {
    val jsTry = Try(Json.parse(text))
    jsTry.flatMap( js => { JsResultPimp(formatDerivativeDifficultyRequest.reads(js)).toTry } )
  }

  def parse(text: String) : Try[Html] = {
    val objOp: Try[TextToHtmlGraph] = from(text)
    objOp.map(v => views.html.mathml.graph(v.name, v.function, v.titleOp, v.glider.getOrElse(true), v.xSize.getOrElse(300), v.ySize.getOrElse(300)))
  }

  def replaceGraph(text: String) : String = {
    val gs = """\$graph\$(.*?)\$graph\$""".r
    val ret = gs.replaceSomeIn(text, m => {
      val main = m.group(1)
      val mainCleanup = main.replaceAll("&quot;", "\"")
      val toReplace = "{ " + mainCleanup + " }"
      val op = parse(toReplace).toOption.map(_.toString)
      op
      })
    ret
  }

  def replaceGraph(html: Html) : Html = Html(replaceGraph(html.toString()))


}

case class TextToHtmlGraph(name: String, function: String = "0", titleOp : Option[String] = None, glider: Option[Boolean] = None, xSize : Option[Int] = None, ySize : Option[Int] = None)
