package io.github.robhinds

import com.typesafe.scalalogging.Logger
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Encoder, Json, Error => JsonError}
import io.github.robhinds.Domain.{ApiResponse, ErrorResponse}
import io.github.robhinds.Errors.{BadRequest, InternalServerError, NotFound}

trait ExceptionHandlerComponent {
  def exceptionHandler: ExceptionHandler
}

trait ExceptionHandler {
  def handle[B: Encoder](e: ApiResponse[B]): Json
}

trait DefaultExceptionHandler extends ExceptionHandlerComponent {
  private val l = Logger(classOf[DefaultExceptionHandler])
  override def exceptionHandler: ExceptionHandler = new ExceptionHandler {
    override def handle[B: Encoder](e: ApiResponse[B]): Json = e match {
      case Left(x: JsonError) => errorResponse("400", s"Error de-serialising JSON: ${x.getMessage}")
      case Left(x: NotFound) => errorResponse("404", x.message)
      case Left(x: BadRequest) => errorResponse("400", x.message)
      case Left(x: InternalServerError) => errorResponse("500", x.message)
      case Left(x) => errorResponse("500", x.getMessage)
      case Right(j) => j.asJson
    }
  }

  private def errorResponse(code: String, message: String) = {
    l.error(s"ERROR Handling Request: $message")
    l.error(s"Returning $code response")
    ErrorResponse(code, message).asJson
  }
}