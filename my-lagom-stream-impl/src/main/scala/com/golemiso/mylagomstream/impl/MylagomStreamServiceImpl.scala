package com.golemiso.mylagomstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.golemiso.mylagomstream.api.MylagomStreamService
import com.golemiso.mylagom.api.MylagomService

import scala.concurrent.Future

/**
  * Implementation of the MylagomStreamService.
  */
class MylagomStreamServiceImpl(mylagomService: MylagomService) extends MylagomStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(mylagomService.hello(_).invoke()))
  }
}
