package io.github.robhinds

import java.io.InputStream

import io.circe.Error
import io.circe.parser._
import io.circe.generic.auto._
import io.github.robhinds.Domain.Sentence

import scala.io.Source

object Decoders {

  private def stream(in: InputStream) = Source.fromInputStream(in).mkString("")

  implicit def decodeSentence(in: InputStream): Either[Error, Sentence] = decode[Sentence](stream(in))

}
