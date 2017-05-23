package io.github.robhinds

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import io.circe.generic.auto._
import io.circe.syntax._
import io.github.robhinds.Api.FoodRoute
import io.github.robhinds.Domain.Sentence
import org.scalatest.FunSpec


class ApiSpec extends FunSpec  {

  val api = new FoodRoute()

  describe("testing implicit conversion") {
    it("should de-serialise the json automatically") {
      val input = Sentence("test input")
      val is = new ByteArrayInputStream( input.asJson.spaces2.getBytes() )
      val os = new ByteArrayOutputStream()
      api.handleRequest(is, os)
    }
  }

}