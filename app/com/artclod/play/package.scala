package com.artclod

import _root_.play.api.templates.Html

package object play {

  def plural(v: TraversableOnce[_])(plural: Html) = if (v.size == 1) { Html("") } else { plural }

  def alternate(v: TraversableOnce[_])(singular: Html, plural: Html) = if (v.size == 1) { singular } else { plural }

  def s(v: TraversableOnce[_]) = plural(v)(Html("s"))

  def es(v: TraversableOnce[_]) = plural(v)(Html("es"))

  def zes(v: TraversableOnce[_]) = plural(v)(Html("zes"))

  def are(v: TraversableOnce[_]) = alternate(v)(Html("is"), Html("are"))
}
