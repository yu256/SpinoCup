package app.Game

import RomajiParser.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import org.scalajs.dom
import org.scalajs.dom.document

import scala.concurrent.duration.*
import scala.scalajs.js
import scala.scalajs.js.timers.setInterval

val gameView =
  ScalaFnComponent
    .withHooks[String]
    .useState(("", "")) // fixme sentenceを生成する
    .useState("") // japanese
    .useState("") // input
    .useState(0) // score
    .useState(60) // timeLeft
    .useState(false) // isGameOver
    .useEffectOnMountBy { $ => // fixme なぜかstateが変化する度に呼ばれる
      val timeLeft = $.hook5
      val isGameOver = $.hook6

      setInterval(1.second) {
        if (!isGameOver.value && timeLeft.value > 0) {
          timeLeft.modState(_ - 1)
        } else {
          isGameOver.setState(true)
        }
      }
      Callback.empty
    }
    .render { (_, sentence, japanese, input, score, timeLeft, isGameOver) =>

      def handleKeyDown(ev: ReactKeyboardEventFromHtml): Callback = {
        if (!isGameOver.value) {
          (ev.key match {
            case key if key.length == 1 =>
              input.modState(_ + key)
              parseRomaji(input.value) match {
                case RomajiState.Complete(jp) =>
                  japanese.modState(_ + jp) >> input.setState("")
                case RomajiState.Invalid =>
                  input.setState("") >> score.modState(_ - 5)
                case RomajiState.Partial(jp, rest) =>
                  japanese.modState(_ + jp) >> input.setState(rest)
                case RomajiState.Progress => Callback.empty
              }

            case "Backspace" | "Delete" =>
              if (input.value.nonEmpty) {
                input.modState(_.dropRight(1))
              } else {
                japanese.modState(_.dropRight(1))
              }

            case _ => Callback.empty
          }) >>
            ((japanese.value, sentence.value._2) match {
              case (jp, hiragana) if jp == hiragana =>
                score.modState(_ + 10) >> {
                  // fixme sentenceを生成する
                  japanese.setState("")
                }
              case (jp, hiragana) if !hiragana.startsWith(jp) =>
                japanese.modState(_.dropRight(1)) >> score.modState(_ - 5)
              case _ => Callback.empty
            })
        } else Callback.empty
      }

      <.div(
          ^.id := "typing-area",
          ^.cls := "h-screen w-screen flex flex-col justify-center items-center",
          ^.tabIndex := 0,
          ^.onBlur --> Callback {
            document
              .getElementById("typing-area")
              .asInstanceOf[dom.HTMLElement]
              .focus()
          },
          ^.onKeyDown ==> handleKeyDown,
          <.div(
              ^.cls := "flex flex-col items-center",
              <.div(
                  ^.cls := "text-4xl",
                  sentence.value._1
              ),
              <.span(
                  s"${japanese.value}${input.value}"
              ),
              <.div(
                  ^.cls := "text-xl mt-4",
                  "スコア: ",
                  score.value
              ),
              <.div(
                  ^.cls := "text-xl mt-4",
                  "残り時間: ",
                  s"${timeLeft.value}s"
              ),
              if (isGameOver.value) {
                <.div(
                    <.div(
                        ^.cls := "text-2xl text-red-500 mt-4",
                        "Game Over"
                    ),
                    <.button(
                        ^.cls := "mt-4 p-2 bg-blue-500 text-white rounded",
                        "Restart",
                        ^.onClick --> {
                          timeLeft.setState(60) >>
                            score.setState(0) >>
                            japanese.setState("") >>
                            input.setState("") >>
                            isGameOver.setState(false)
                          // fixme sentenceを生成する
                        }
                    )
                )
              } else EmptyVdom
          )
      )
    }
