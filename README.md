[![Build Status](https://travis-ci.org/robhinds/ScaLambda.png)](https://travis-ci.org/robhinds/ScaLambda)


# ScaLambda
## A Scala framework for working with AWS Lambda

Started when [AWS Lambda](https://aws.amazon.com/lambda/) JVM support was more limited and there wasn't a succinct way to easily define APIs for handling input and output types (you required verbose java POJOs with annotations on all fields).

The AWS Java support has since improved and AWS comes with its own JSON de/serialisation steps, but this provides a more idiomatic scala approach to defining AWS Lambda functions. It uses [Circe](https://circe.github.io/circe/)'s auto derived JSON encoder/decoders for handling the serialisation and deserialisation of input and output messages. It also provides exception handling and aims to remove all boilerplate from your lambda code.


The library lets you easily define a typed function, allowing you to concentrate on just the business logic you need. There are conveneint helper methods to return error responses (404s, 400s, etc) and errors in the de-serialisation step will automatically 400 the user request (if you use the default exception handler - you can also provide your own implementation of the exception handler to override that behavour and other error mappings)

For example:
```scala
  case class TestInput(value: String)
  case class TestOutput(value: String)
  
  class TestRoute extends Controller[TestInput, TestOutput] with DefaultResponseSerializerComponent with DefaultExceptionHandler {
    override def handleRequest(in: TestInput): ApiResponse[TestOutput] = 
        success(TestOutput(s"OUTPUT:${in.value}"))
  }
```

This can easily be deployed to AWS (we recommend the [Serverless framework](https://serverless.com/))


### Getting started

Grab the JAR from bintray, add the following code to your gradle configuration

```gradle
repositories {
    maven {
        url  "https://dl.bintray.com/robhinds/snapshots" 
    }
}

dependencies {
    compile 'io.github.robhinds:ScaLambda:0.0.1'
}
```


### Customisation
The basic premise of the functions allow you to define any input or output case classes/sealed traits that can be encoded/decoded as JSON. Beyond that, it is also possible to provide a custom error handling mechanism and custom serialisation mechanism (this allows you to have a common response envelope, for example, so all your functions can return consistent messages - if you want to return additional metadata such as rate limiting information, paging info, etc)

#### Error Handling
The Controller class expects you to provide an implementation of the ExceptionHandlerComponent trait - this has a single method that has to be implemented:

```scala
trait DefaultExceptionHandler extends ExceptionHandlerComponent {
  override def exceptionHandler: ExceptionHandler = new ExceptionHandler {
    override def handle[B](e: ApiResponse[B]): Either[ErrorResponse, B] = e match {
      case Left(x: JsonError) => errorResponse("400", s"Error de-serialising JSON: ${x.getMessage}")
      case Left(x: NotFound) => errorResponse("404", x.message)
      case Left(x: BadRequest) => errorResponse("400", x.message)
      case Left(x: InternalServerError) => errorResponse("500", x.message)
      case Left(x) => errorResponse("500", x.getMessage)
      case Right(x) => Right(x)
    }
  }
```

The left side of the ApiResponse is an Exception, so you can specify how to handle any exceptions you need - the JsonError above is the exception that Circe throws if the input cannot be parsed to the expected input.

#### Response Serialisation
Similarly, the Controller class expects you to provide an implementation of the ResponseSerializerComponent:
```scala
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
```
This method takes the response of the error handling and returns some JSON response - this allows you to create any response envelope. The default implementation simply wraps all responses in a JSON structure with a "status" field that returns the status code 200 and the actual response as the "data" field in the message.

