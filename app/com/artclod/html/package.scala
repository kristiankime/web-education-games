package com.artclod

import play.api.templates.Html

package object html {

  implicit class MakeHtml[T](items: Seq[T]){

    def mkHtml(ifEmpty: Html)(start: Html, f: T => Html, end: Html) = {
      if(items.isEmpty) ifEmpty
      else {
        var first = true
        val ret = start

        items.foreach(v => {
          ret += f(v)
        })

        ret += end
        ret
      }
    }

  }

}
