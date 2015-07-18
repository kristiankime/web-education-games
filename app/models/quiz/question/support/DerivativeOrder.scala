package models.quiz.question.support

import play.api.data.FormError
import play.api.data.format.Formatter
import play.api.db.slick.Config.driver.simple._

object DerivativeOrder {
  val all = Vector(OneTwoThree, OneThreeTwo, TwoOneThree, TwoThreeOne, ThreeOneTwo, ThreeTwoOne)

  def apply(in: String) = all.find( _.asString == in) match {
    case Some(out) => out
    case None => throw new IllegalArgumentException("Could not find " + DerivativeOrder.getClass.getSimpleName + " for " + in)
  }

  implicit def string2DerivativeOrder = MappedColumnType.base[DerivativeOrder, String](
    derivativeOrder => derivativeOrder.asString,
    str => apply(str))

  implicit def derivativeOrderFormatter: Formatter[DerivativeOrder] = new Formatter[DerivativeOrder] {
    def bind(key: String, data: Map[String, String]) = {
      data.get(key) match {
        case None => Left(Seq(FormError(key, "key must map to a value", Nil)))
        case Some(txt) => DerivativeOrder.parse(txt)
      }
    }

    def unbind(key: String, value: DerivativeOrder) = Map(key -> value.asString)
  }

  private def parse(in: String) : Either[Seq[FormError], DerivativeOrder] = all.find( _.asString == in) match {
    case Some(out) => Right(out)
    case None => Left(Seq(FormError(in, "Could not find " + DerivativeOrder.getClass.getSimpleName + " for " + in)))
  }
}

sealed trait DerivativeOrder {
  val asString : String
  val niceString : String
}

object OneTwoThree extends DerivativeOrder { val asString = "F-1-2"; val niceString = "Function, 1st Derivative, 2nd Derivative" }
object OneThreeTwo extends DerivativeOrder { val asString = "F-2-1"; val niceString = "Function, 2nd Derivative, 1st Derivative" }
object TwoOneThree extends DerivativeOrder { val asString = "2-F-1"; val niceString = "2st Derivative, Function, 1st Derivative" }
object TwoThreeOne extends DerivativeOrder { val asString = "1-2-F"; val niceString = "Function, 2st Derivative, 1st Derivative" }
object ThreeOneTwo extends DerivativeOrder { val asString = "2-F-1"; val niceString = "2st Derivative, Function, 1st Derivative" }
object ThreeTwoOne extends DerivativeOrder { val asString = "2-1-F"; val niceString = "2st Derivative, 1st Derivative, Function" }

