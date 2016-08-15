package controllers

import com.artclod.mathml.MathML
import com.artclod.mathml.scalar.MathMLElem
import controllers.support.SecureSocialConsented
import models.quiz.question.{QuestionScoring, DerivativeQuestionDifficulty}
import play.api.mvc.Controller
import play.api.db.slick.Config.driver.simple.Session
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller, Result}
import play.api.templates.Html
import scala.util.{Success, Failure}

object MathController extends Controller with SecureSocialConsented {

  val functionStr = "functionStr"
  val function = "function"
  val firstDerivative = "firstDerivative"
  val secondDerivative = "secondDerivative"

  case class FunctionDerivativeRequest(functionStr: String, function: String)

  case class FunctionDerivativeResponse(functionStr: String, function: String, firstDerivative: String, secondDerivative: String)

  implicit val formatFunctionDerivativeRequest = Json.format[FunctionDerivativeRequest]

  implicit val formatFunctionDerivativeResponse = Json.format[FunctionDerivativeResponse]

  def functionDerivatives = Action { request =>
    request.body.asJson.map { configJson =>
      configJson.validate[FunctionDerivativeRequest]
        .map { difficultyRequest =>
        MathML(difficultyRequest.function) match {
          case Failure(e) => BadRequest("Could not parse [" + difficultyRequest.function + "] as mathml\n" + e.getStackTraceString)
          case Success(mathML) => {
            val firstDerivative = mathML.dx
            val secondDerivative = firstDerivative.dx
            Ok(Json.toJson(FunctionDerivativeResponse(difficultyRequest.functionStr, difficultyRequest.function, fnc(firstDerivative), fnc(secondDerivative))))
          }
        }
      }.recoverTotal { e => BadRequest("Detected error:" + JsError.toFlatJson(e)) }
    }.getOrElse(BadRequest("Expecting Json data"))
  }

  private def fnc(body: MathMLElem) = body.toMathJS

}
