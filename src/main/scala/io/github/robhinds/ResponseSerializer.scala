package io.github.robhinds

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Encoder, Json}
import io.github.robhinds.Domain.ErrorResponse

trait ResponseSerializerComponent {
  def responseSerializer: ResponseSerializer
}

trait ResponseSerializer {
  def serialiseResponse[B: Encoder](e: Either[ErrorResponse, B]): Json
}

trait DefaultResponseSerializerComponent extends ResponseSerializerComponent {
  override def responseSerializer: ResponseSerializer = new ResponseSerializer {
    override def serialiseResponse[B: Encoder](e: Either[ErrorResponse, B]): Json = e match {
      case Left(x) => x.asJson
      case Right(x) =>
        Json.fromFields(List(
          ("status", Json.fromString("200")),
          ("data", x.asJson)
      ))
    }
  }
}
