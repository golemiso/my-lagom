package com.golemiso.mylagomstream.api

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

/**
  * The my-lagom stream interface.
  *
  * This describes everything that Lagom needs to know about how to serve and
  * consume the MylagomStream service.
  */
trait MylagomStreamService extends Service {

  def stream: ServiceCall[Source[String, NotUsed], Source[String, NotUsed]]

  override final def descriptor = {
    import Service._

    named("my-lagom-stream")
      .withCalls(
        namedCall("stream", stream)
      ).withAutoAcl(true)
  }
}

