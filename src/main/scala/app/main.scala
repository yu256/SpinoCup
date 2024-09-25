package app

import Game.gameView
import com.raquo.laminar.api.L.*
import org.scalajs.dom

@main def main(): Unit =
  lazy val appContainer = dom.document.querySelector("#app")
  renderOnDomContentLoaded(appContainer, app)

lazy val app =
  val gooAppId = Var(None: Option[String])

  span(
      child <-- gooAppId.signal.map(
          _.fold(startScreen(gooAppId.set compose Some.apply))(gameView))
  )

def startScreen(appIdSetter: String => Unit) =
  div(
      cls := "flex flex-col items-center justify-center h-screen bg-gray-800",
      h1(
          cls := "text-5xl font-bold mb-6 text-center text-white",
          "AtsusugiDa"
      ),
      p(
          cls := "text-lg text-center text-gray-400 mb-8",
          "gooのapp_idを入力してください。"
      ),
      input(cls := "p-2 border border-gray-600 rounded-md mb-4",
          placeholder := "app_id", onInput.mapToValue --> appIdSetter)
  )
