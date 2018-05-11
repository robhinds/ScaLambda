package io.github.robhinds

object Domain {
  type ApiResponse[T] = Either[Exception, T]
  object ApiResponse {
    def failure[T](e :Exception): ApiResponse[T] = Left(e)
    def success[T](t: T): ApiResponse[T] = Right(t)
  }
  case class ErrorResponse(code: String, message: String)
}
