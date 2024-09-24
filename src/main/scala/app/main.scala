package app

import Game.gameView
import com.raquo.laminar.api.L.*
import components.*
import org.scalajs.dom

@main def main(): Unit =
  lazy val appContainer = dom.document.querySelector("#app")
  renderOnDomContentLoaded(appContainer, app)

lazy val app =
  val isStarted = Var(false)

  span(
      child <-- isStarted.signal.map {
        if _ then gameView else startScreen(_ => isStarted.set(true))
      }
  )

def startScreen(onClickStart: dom.MouseEvent => Unit) =
  div(
      cls := "flex flex-col items-center justify-center h-screen bg-gray-800",
      h1(
          cls := "text-5xl font-bold mb-6 text-center text-white",
          "AtsusugiDa"
      ),
      p(
          cls := "text-lg text-center text-gray-400 mb-8",
          "下のボタンを押してスタート"
      ),
      myButton(
          "Start Game",
          onClickStart
      )
  )
