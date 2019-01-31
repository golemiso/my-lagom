package com.golemiso.mylagomstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.golemiso.mylagomstream.api.MylagomStreamService
import com.golemiso.mylagom.api.MylagomService
import com.softwaremill.macwire._

class MylagomStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new MylagomStreamApplication(context) {
      override def serviceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new MylagomStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[MylagomStreamService])
}

abstract class MylagomStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[MylagomStreamService](wire[MylagomStreamServiceImpl])

  // Bind the MylagomService client
  lazy val mylagomService = serviceClient.implement[MylagomService]
}
