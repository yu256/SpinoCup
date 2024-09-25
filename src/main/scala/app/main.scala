package app

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import org.scalajs.dom

val component = ScalaFnComponent
  .withHooks[Unit]
  .useState(None: Option[String])
  .useState(false)
  .render { $ =>
    $.hook1.value
      .filter(_ => $.hook2.value)
      .fold {
        <.div(
            ^.cls := "flex flex-col items-center justify-center h-screen bg-gray-800",
            <.h1(
                ^.cls := "text-5xl font-bold mb-6 text-center text-white",
                "AtsusugiDa"
            ),
            <.p(
                ^.cls := "text-lg text-center text-gray-400 mb-8",
                "gooのapp_idを入力してください。"
            ),
            <.form(
                ^.cls := "flex flex-col items-center",
                ^.onSubmit ==> { e =>
                  e.preventDefault()
                  $.hook2.setState(true)
                },
                <.input(
                    ^.cls := "px-4 py-2 border border-gray-600 rounded-md mb-4",
                    ^.`type` := "text",
                    ^.placeholder := "app_id",
                    ^.onChange ==> { (e: ReactEventFromInput) =>
                      $.hook1.setState(Some(e.target.value))
                    }
                ),
                <.button(
                    ^.cls := "px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700",
                    ^.`type` := "submit",
                    "Submit"
                )
            )
        )
      }(Game.gameView.apply)
  }

@main def main(): Unit =
  val appContainer = dom.document.querySelector("#app")
  component().renderIntoDOM(appContainer)
