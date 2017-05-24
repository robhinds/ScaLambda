package io.github.robhinds

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Error => JsonError, Json}
import io.github.robhinds.Domain.ErrorResponse

trait ExceptionHandlerComponent {
  def exceptionHandler: ExceptionHandler
}

trait ExceptionHandler {
  def handle(e: Either[Exception, Json]): Json
}

trait DefaultExceptionHandler extends ExceptionHandlerComponent {
  override def exceptionHandler: ExceptionHandler = new ExceptionHandler {
    override def handle(e: Either[Exception, Json]): Json = e match {
      case Left(x: JsonError) => ErrorResponse("400", s"Error de-serialising JSON: ${x.getMessage}").asJson
      case Left(x) => ErrorResponse("500", x.getMessage).asJson
      case Right(j) => j
    }
  }
}