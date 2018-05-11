package io.github.robhinds

import java.io.{InputStream, OutputStream}
import java.nio.charset.StandardCharsets._

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import io.circe.{Decoder, Encoder, Error, Json}
import io.circe.parser._
import io.github.robhinds.Domain.ApiResponse

import scala.io.Source

abstract class Controller[A: Decoder, B: Encoder] extends RequestStreamHandler with LoggingConfig {
  self: ExceptionHandlerComponent =>

  def handleRequest(in: InputStream, out: OutputStream, c: Context): Unit = {
    logDebug("Message received")
    handleResponse(
      handleException(
        fromJson(in).fold(l=> Left(l), { r =>
          handleRequest(r)
        })
      ), out
    )
  }

  private def fromJson(in: InputStream): Either[Exception, A] = {
    decode[A](Source.fromInputStream(in).mkString(""))
  }

  private def handleResponse(j: Json, out: OutputStream): Unit = {
    logDebug(s"Sending response: $j")
    out.write(j.toString.getBytes(UTF_8))
  }

  private def handleException(r: ApiResponse[B]): Json = exceptionHandler.handle(r)

  /**
    * Method to be implemented by the AWS Lambda endpoint. It expects an argument of type A
    * which has to be something that can be decoded from JSON by Circe (see context bound on class type
    * parameter) and type B that is the response, also needs to have an in scope circe encoder (can be automatic).
    */
  def handleRequest(in: A): ApiResponse[B]
}
