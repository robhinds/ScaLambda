package io.github.robhinds

import java.io.{InputStream, OutputStream}
import java.nio.charset.StandardCharsets._
import io.circe.{Decoder, Json}
import io.circe.parser._
import scala.io.Source

abstract class Controller[A: Decoder] { self: ExceptionHandlerComponent =>

  def handleRequest(in: InputStream, out: OutputStream): Unit =
    handleResponse(
      handleException(
        fromJson(in).fold(l=> Left(l), { r =>
          handleRequest(r)
        })
      ), out
    )

  private def fromJson(in: InputStream) = {
    decode[A](Source.fromInputStream(in).mkString(""))
  }

  private def handleResponse(j: Json, out: OutputStream): Unit =
    out.write(j.toString.getBytes(UTF_8))

  private def handleException(r: Either[Exception, Json]): Json = exceptionHandler.handle(r)

  /**
    * Method to be implemented by the AWS Lambda endpoint. It expects an argument of type A
    * which has to be something that can be decoded from JSON by Circe (see context bound on class type
    * parameter).
    */
  def handleRequest(in: A): Either[Exception, Json]
}
