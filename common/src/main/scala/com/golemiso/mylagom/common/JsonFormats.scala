package com.golemiso.mylagom.common

import play.api.libs.json.{Json, OFormat}


case class WrapperObject[T](value: T)

case class StringObject(value: String)
