package com.artclod.util

import laika.api.Transform
import laika.parse.markdown.Markdown
import laika.api.{Transform, Render, Parse}
import laika.parse.markdown.Markdown
import laika.parse.rst.ReStructuredText
import laika.render.HTML
import laika.template.ParseTemplate
import laika.tree.Documents.DocumentContext
import play.api.templates.Html

import scala.util.Try

object LaikaParser {

  def apply(text: String) = Try(Html(Transform from Markdown.strict to HTML fromString text toString()))

}
