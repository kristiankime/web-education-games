package com.artclod

import _root_.play.api.templates.Html

package object play {

  def plural(v: TraversableOnce[_])(plural: Html) = if (v.size == 1) { Html("") } else { plural }

  def s(v: TraversableOnce[_])= plural(v)(Html("s"))

}
