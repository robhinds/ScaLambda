package io.github.robhinds

object Errors {
  case class NotFound(message: String = "Resource not found") extends Exception
  case class BadRequest(message: String = "Bad request") extends Exception
  case class InternalServerError(message: String = "Something went wrong") extends Exception
}
