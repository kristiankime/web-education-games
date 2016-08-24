package com.artclod.markup

import laika.api.Transform
import laika.parse.markdown.Markdown
import laika.render.HTML
import play.api.templates.Html

import scala.util.Try

object LaikaParser {

  def apply(text: String) = Try(Html(Transform from Markdown.strict to HTML fromString text toString()))

}
