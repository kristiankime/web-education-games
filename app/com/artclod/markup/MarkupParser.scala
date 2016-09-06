package com.artclod.markup

import com.artclod.mathml.TextToHtmlGraph
import play.api.templates.Html

import scala.util.Success


object MarkupParser {

  def apply(text: String) = LaikaParser(text).map( TextToHtmlGraph.sideGraphs(_) )

}
