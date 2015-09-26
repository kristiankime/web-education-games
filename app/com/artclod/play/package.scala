package com.artclod

import _root_.play.api.templates.Html
import _root_.play.api.data.Form

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

  implicit class FormEnhanced(form: Form[_]) {

    def errorByMessage(message: String) = form.errors.filter(_.message == message).headOption

    def hasMessage(message: String) = errorByMessage(message).nonEmpty

    def dataFor(field: String) = form.data.get(field)

    def dataOrElse(field: String, default: String) = form.data.getOrElse(field, default)

  }

  def firstTrue(t: Tuple1[Boolean]) = if(t._1){0} else{-1}
  def firstTrue(t: (Boolean, Boolean)) = if(t._1){0} else if(t._2){1} else{-1}
  def firstTrue(t: (Boolean, Boolean, Boolean)) = if(t._1){0} else if(t._2){1} else if(t._3){2} else{-1}
  def firstTrue(t: (Boolean, Boolean, Boolean, Boolean)) = if(t._1){0} else if(t._2){1} else if(t._3){2} else if(t._4){3} else{-1}
  def firstTrue(t: (Boolean, Boolean, Boolean, Boolean, Boolean)) = if(t._1){0} else if(t._2){1} else if(t._3){2} else if(t._4){3} else if(t._5){4} else{-1}

}
