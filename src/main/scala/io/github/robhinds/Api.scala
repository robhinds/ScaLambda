package io.github.robhinds

import java.io.OutputStream
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.robhinds.Domain.{ErrorResponse, Sentence}

object Api {

  class FoodRoute extends FrontController[Sentence] {
    override def handleRequest(in: Either[Error, Sentence], out: OutputStream): Json = in match {
      case Left(_) => ErrorResponse("500", "Error parsing input string").asJson
      case Right(s) => s.asJson
    }
  }

}