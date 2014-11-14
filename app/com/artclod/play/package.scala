package com.artclod

import _root_.play.api.templates.Html
import scala.collection.mutable.LinkedHashMap

package object play {

  def plural(v: TraversableOnce[_])(plural: Html) = if (v.size == 1) { Html("") } else { plural }

  def alternate(v: TraversableOnce[_])(singular: Html, plural: Html) = if (v.size == 1) { singular } else { plural }

  def s(v: TraversableOnce[_]) = plural(v)(Html("s"))

  def es(v: TraversableOnce[_]) = plural(v)(Html("es"))

  def zes(v: TraversableOnce[_]) = plural(v)(Html("zes"))

  def are(v: TraversableOnce[_]) = alternate(v)(Html("is"), Html("are"))

  def collapse(atts: Seq[(scala.Symbol, String)]) : Seq[(scala.Symbol, String)]= {
    val map = LinkedHashMap[scala.Symbol, String]()

    for (a <- atts) {
      map.get(a._1) match {
        case None => map += a
        case Some(str) => map.+=((a._1, str + " " + a._2))
      }
    }

    map.toSeq
  }

  def asAtt(symbol: scala.Symbol) = symbol.toString.replace('_', '-').substring(1)

}
