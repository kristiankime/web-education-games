package com.artclod.util

object LaikaExample {
  def main(args: Array[String]) {
    import laika.api.{Transform, Render, Parse}
    import laika.parse.markdown.Markdown
    import laika.parse.rst.ReStructuredText
    import laika.render.HTML
    import laika.template.ParseTemplate
    import laika.tree.Documents.DocumentContext

    //    val file1 : File = null;
    //    val file2 : File = null;


    val html = Transform from Markdown.strict to HTML fromString
      """An h1 header\n============"""
    toString() //"hello.html"

    println(html)

    //    val doc = Parse as ReStructuredText.withRawContent fromString("")
    //    val templateDoc = ParseTemplate fromFile file2
    //    val fullDoc = templateDoc.rewrite(DocumentContext(doc))
    //    val html = (Render as HTML from fullDoc).toString
    //    html.toString

    //    val doc = Parse as ReStructuredText.withRawContent fromFile file1

    //    val doc = Parse as ReStructuredText.withRawContent fromFile file1
    //    val templateDoc = ParseTemplate fromFile file2
    //    val fullDoc = templateDoc.rewrite(DocumentContext(doc))
    //    val html = (Render as HTML from fullDoc).toString
    //    html.toString
  }
}
