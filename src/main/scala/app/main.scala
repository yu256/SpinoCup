package app

import com.raquo.laminar.api.L.*
import org.scalajs.dom

@main def main(): Unit =
  lazy val appContainer = dom.document.querySelector("#app")
  renderOnDomContentLoaded(appContainer, app)

lazy val app =
  div()