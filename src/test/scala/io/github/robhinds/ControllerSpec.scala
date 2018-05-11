package io.github.robhinds

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import io.circe.generic.auto._
import io.circe.syntax._
import io.github.robhinds.Domain.ApiResponse
import io.github.robhinds.Domain.ApiResponse._
import org.scalatest.{FunSpec, Matchers}


class ControllerSpec extends FunSpec with Matchers {

  case class TestInput(value: String)
  case class TestOutput(value: String)
  class TestRoute extends Controller[TestInput, TestOutput] with DefaultExceptionHandler {
    override def handleRequest(in: TestInput): ApiResponse[TestOutput] = success(TestOutput(s"OUTPUT:${in.value}"))
  }
  class ErrorRoute extends Controller[TestInput, TestOutput] with DefaultExceptionHandler {
    override def handleRequest(s: TestInput): ApiResponse[TestOutput] = failure[TestOutput](new Exception("failure"))
  }

  val api = new TestRoute()
  val errorApi = new ErrorRoute()

  describe("testing happy path") {
    it("should de-serialise the json automatically") {
      val input = TestInput("test input")
      val os = new ByteArrayOutputStream()
      api.handleRequest(asIs(input.asJson.spaces2), os, null)
      os.toString("UTF-8") shouldBe "{\n  \"value\" : \"OUTPUT:test input\"\n}"
    }
  }

  describe("testing JSON deserialisation error") {
    it("should 400 for incorrect shape JSON") {
      val os = new ByteArrayOutputStream()
      api.handleRequest(asIs("{\"validjson\" : \"but not the right shape\"}"), os, null)
      os.toString("UTF-8") shouldBe "{\n  \"code\" : \"400\",\n  \"message\" : \"Error de-serialising JSON: Attempt to decode value on failed cursor: DownField(value)\"\n}"
    }
    it("should 400 for invalid JSON") {
      val os = new ByteArrayOutputStream()
      api.handleRequest(asIs("random string"), os, null)
      os.toString("UTF-8") shouldBe "{\n  \"code\" : \"400\",\n  \"message\" : \"Error de-serialising JSON: expected json value got r (line 1, column 1)\"\n}"
    }
  }

  describe("testing error response from endpoint") {
    it("should serialise with default 500") {
      val input = TestInput("test input")
      val os = new ByteArrayOutputStream()
      errorApi.handleRequest(asIs(input.asJson.spaces2), os, null)
      os.toString("UTF-8") shouldBe "{\n  \"code\" : \"500\",\n  \"message\" : \"failure\"\n}"
    }
  }

  private def asIs(s: String) = {
    new ByteArrayInputStream( s.getBytes() )
  }

}