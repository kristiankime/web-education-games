package com.artclod.math

import com.google.common.base.Strings
import org.apache.commons.lang3.math.NumberUtils

import scala.util.Try

private object IntervalSupport {
  val fmt = new java.text.DecimalFormat("#.##");

  private val num = "[-+]?[0-9]*\\.?[0-9]+"
  private val numOrInf = (DoubleParse.allInfForRegex + num).mkString("(?:", "|", ")")
  private val w = "[\\s]*"
  private val numGroup = w + "(" + numOrInf + ")" + w
  private val full = ("^\\(" + numGroup + "," + numGroup + "\\)$")
  val reg = full.r
}

object Interval {
  def apply(lower: Int, upper: Int) : Interval = Interval(lower.toDouble, upper.toDouble)

  def apply(str: String) : Option[Interval] = {
    str.trim.toLowerCase match {
      case IntervalSupport.reg(lower, upper) => {
        val loTry = DoubleParse(lower).toOption
        val upTry = DoubleParse(upper).toOption
        loTry.flatMap(lo => upTry.map(up => Interval(lo, up)))
      }
      case _ => None
    }
  }
}

case class Interval(lower: Double, upper: Double) {
  if(lower > upper){ throw new IllegalArgumentException("Lower cannot be > upper [" + lower + ", " + upper + "]")}
  val l = lower
  val u = upper
  override def toString  = "(" + IntervalSupport.fmt.format(lower) + "," + IntervalSupport.fmt.format(upper) + ")"
}