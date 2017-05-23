package io.github.robhinds

object Domain {

  case class Sentence(value: String)

  case class ErrorResponse(code: String, message: String)
}
