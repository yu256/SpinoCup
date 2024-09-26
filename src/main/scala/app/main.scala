package app

import Game.gameView
import com.raquo.laminar.api.L.*
import org.scalajs.dom

@main def main(): Unit =
  lazy val appContainer = dom.document.querySelector("#app")
  renderOnDomContentLoaded(appContainer, app)

lazy val app =
  var gooAppId = ""
  var chainLen = 10
  var toLearnText = "暑すぎる"

  val isFinishedVar = Var(false)

  span(
      child <-- isFinishedVar.signal.map { isFinished =>
        if gooAppId.nonEmpty && isFinished then
          gameView(gooAppId, chainLen, toLearnText)
        else
          startScreen(appIdSetter = gooAppId = _, chainLenSetter = chainLen = _,
              toLearnTextSetter = toLearnText = _, isFinishedVar.set(true))
      }
  )

def startScreen(
    appIdSetter: String => Unit,
    chainLenSetter: Int => Unit,
    toLearnTextSetter: String => Unit,
    isFinishedSetter: => Unit
) =
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
      form(
          cls := "flex flex-col items-center",
          input(
              cls := "p-2 border border-gray-600 rounded-md mb-4",
              placeholder := "app_id",
              onInput.mapToValue --> appIdSetter
          ),
          input(
              cls := "p-2 border border-gray-600 rounded-md mb-4",
              placeholder := "文字の長さ（初期値10）",
              onInput.mapToValue --> { _.toIntOption.foreach(chainLenSetter) }
          ),
          textArea(
              cls := "p-2 border border-gray-600 rounded-md mb-4",
              placeholder := "学習データ",
              onInput.mapToValue --> toLearnTextSetter
          ),
          button(
              cls := "px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600",
              "Start",
              onClick --> { e =>
                e.preventDefault()
                isFinishedSetter
              }
          )
      )
  )
