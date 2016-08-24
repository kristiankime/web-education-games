package controllers

import com.artclod.markup.LaikaParser
import com.artclod.mathml.MathML
import com.artclod.mathml.scalar.MathMLElem
import controllers.MathController.{FunctionDerivativeResponse, FunctionDerivativeRequest}
import controllers.support.SecureSocialConsented
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}
import play.api.templates.Html
import play.api.libs.json.{JsError, Json}
import scala.util.{Failure, Success}
import com.artclod.mathml.MathML
import com.artclod.mathml.scalar.MathMLElem
import controllers.support.SecureSocialConsented
import models.quiz.question.{QuestionScoring, DerivativeQuestionDifficulty}
import play.api.mvc.Controller
import play.api.db.slick.Config.driver.simple.Session
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller, Result}
import play.api.templates.Html

object MarkupController extends Controller with SecureSocialConsented {

  case class MarkupRequest(markupStr: String)

  case class MarkupResponse(markupStr: String, markupHtml: String)

  implicit val formatMarkupRequest = Json.format[MarkupRequest]

  implicit val formatMarkupResponse = Json.format[MarkupResponse]

  def markup = Action { request =>
    request.body.asJson.map { configJson =>
      configJson.validate[MarkupRequest]
        .map { markupRequest =>
        LaikaParser(markupRequest.markupStr) match {
          case Failure(e) => BadRequest("Could not parse [" + markupRequest.markupStr + "] as markup\n" + e.getStackTraceString)
          case Success(html) => {
            Ok(Json.toJson(MarkupResponse(markupRequest.markupStr, html.toString())))
          }
        }
      }.recoverTotal { e => BadRequest("Detected error:" + JsError.toFlatJson(e)) }
    }.getOrElse(BadRequest("Expecting Json data"))
  }

  private def fnc(body: MathMLElem) = body.toMathJS

}
