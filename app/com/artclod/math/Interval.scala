package com.artclod.math

import com.google.common.base.Strings

import scala.util.Try

private object IntervalSupport {
  val fmt = new java.text.DecimalFormat("#.##");

  private val num = """[-+]?[0-9]*\.?[0-9]+"""
  private val w = """[\s]*"""
  private val numGroup = w + "(" + num + ")" + w
  val full = ("""\(""" + numGroup + "," + numGroup + """\)""")
  val reg = full.r
}

object Interval {
  def apply(lower: Int, upper: Int) : Interval = Interval(lower.toDouble, upper.toDouble)

  def apply(str: String) : Option[Interval] = {

    val foo = IntervalSupport.full

    str.trim match {
      case IntervalSupport.reg(lower, upper) => {
        val loTry = Try(lower.toDouble).toOption
        val upTry = Try(upper.toDouble).toOption
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