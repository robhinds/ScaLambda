# AWS Lambda Web Support

Some boilerplate machinery to support AWS lambda endpoints written in Scala.

Uses Circe to automatically allow mapping from inbound JSON to case classes (or sealed traits)


For example, define an endpoint as an encapsulated class extending the Controller class.
```
  class TestRoute extends Controller[TestInput] with DefaultExceptionHandler {
    override def handleRequest(s: TestInput): Either[Exception, Json] =
      Right(s.asJson)
  }
```