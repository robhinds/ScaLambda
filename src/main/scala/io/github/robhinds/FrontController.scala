package io.github.robhinds

import java.io.{InputStream, OutputStream}
import java.nio.charset.StandardCharsets._
import io.circe.{Error, Json}
import io.circe.parser._
import io.circe.generic.auto._
import scala.io.Source

trait FrontController[A] {

  def handleRequest(in: InputStream, out: OutputStream): Unit =
    handleRequest(decode[A](Source.fromInputStream(in).mkString("")), out)

  def handleResponse(j: Json, out: OutputStream): Unit =
    out.write(j.spaces2.getBytes(UTF_8))

  def handleRequest(in: Either[Error, A], out: OutputStream): Json

}
