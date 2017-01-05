package models.quiz.question.support

import play.api.data.FormError
import play.api.data.format.Formatter
import play.api.db.slick.Config.driver.simple._

object DerivativeOrder {
  val all = Vector(FuncFirstSecond, FuncSecondFirst, FirstFuncSecond, FirstSecondFunc, SecondFuncFirst, SecondFirstFunc)

  def random = com.artclod.util.Randoms.randomFrom(all)

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
        case Some(txt) => DerivativeOrder.parse(key, txt)
      }
    }

    def unbind(key: String, value: DerivativeOrder) = Map(key -> value.asString)
  }

  private def parse(key: String, in: String) : Either[Seq[FormError], DerivativeOrder] = all.find( _.asString == in) match {
    case Some(out) => Right(out)
    case None => Left(Seq(FormError(key, "Could not find " + DerivativeOrder.getClass.getSimpleName + " for " + in)))
  }
}

sealed trait DerivativeOrder {
  val asString : String
  val niceString : String
  override def toString = asString
}

object FuncFirstSecond extends DerivativeOrder { val asString = "F-1-2"; val niceString = "Function, 1st Derivative, 2nd Derivative" }
object FuncSecondFirst extends DerivativeOrder { val asString = "F-2-1"; val niceString = "Function, 2nd Derivative, 1st Derivative" }
object FirstFuncSecond extends DerivativeOrder { val asString = "1-F-2"; val niceString = "1st Derivative, Function, 2nd Derivative" }
object FirstSecondFunc extends DerivativeOrder { val asString = "1-2-F"; val niceString = "1st Derivative, 2nd Derivative, Function" }
object SecondFuncFirst extends DerivativeOrder { val asString = "2-F-1"; val niceString = "2nd Derivative, Function, 1st Derivative" }
object SecondFirstFunc extends DerivativeOrder { val asString = "2-1-F"; val niceString = "2nd Derivative, 1st Derivative, Function" }
