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
  
  class TestRoute extends Controller[TestInput, TestOutput] with DefaultExceptionHandler {
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
