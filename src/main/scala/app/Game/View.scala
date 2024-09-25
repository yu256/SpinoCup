package app.Game

import com.raquo.laminar.api.L.*
import org.scalajs.dom

import scala.concurrent.duration.given
import scala.scalajs.js
import scala.scalajs.js.timers.setInterval

def gameView() =
  import RomajiParser.*

  val japanese = Var("")
  val input = Var("")
  val sentence = Var(SentenceGen.genSentence())

  val score = Var(0)
  val timeLeft = Var(60)
  val isGameOver = Var(false)

  setInterval(1.second) {
    if timeLeft.now() > 0 then timeLeft.update(_ - 1)
    else isGameOver.set(true)
  }

  def restartGame(): Unit =
    timeLeft.set(60)
    score.set(0)
    sentence.set(SentenceGen.genSentence())
    japanese.set("")
    input.set("")
    isGameOver.set(false)

  div(
      idAttr := "typing-area",
      cls := "h-screen w-screen flex flex-col justify-center items-center",
      tabIndex := 0,
      autoFocus := true,
      onBlur --> { ev =>
        ev.preventDefault()
        dom.document
          .getElementById("typing-area")
          .asInstanceOf[dom.HTMLElement]
          .focus()
      },
      onKeyDown --> { ev =>
        if !isGameOver.now() then
          ev.key match {
            case key if key.length == 1 =>
              input.update(_ ++ key)
              parseRomaji(input.now()) match {
                case RomajiState.Complete(jp) =>
                  japanese.update(_ ++ jp)
                  input.set("")
                case RomajiState.Invalid =>
                  input.set("")
                  score.update(_ - 5)
                case RomajiState.Partial(jp, rest) =>
                  japanese.update(_ ++ jp)
                  input.set(rest)
                case RomajiState.Progress =>
              }
            case "Backspace" | "Delete" =>
              (if input.now().nonEmpty then input else japanese)
                .update(_.dropRight(1))
            case _ =>
          }
          (japanese.now(), sentence.now()) match {
            case (jp, (_, hiragana)) if jp == hiragana =>
              score.update(_ + 10)
              sentence.set(SentenceGen.genSentence())
              japanese.set("")
            case (jp, (_, hiragana)) if !hiragana.startsWith(jp) =>
              japanese.update(_.dropRight(1))
              score.update(_ - 5)
            case _ =>
          }
      },
      div(
          cls := "flex flex-col items-center",
          div(
              cls := "text-4xl",
              text <-- sentence.signal.map(_._1)
          ),
          span(
              text <-- japanese.signal.combineWithFn(input.signal)(_ ++ _)
          ),
          div(
              cls := "text-xl mt-4",
              "スコア: ",
              child.text <-- score.signal
          ),
          div(
              cls := "text-xl mt-4",
              "残り時間: ",
              child.text <-- timeLeft.signal.map(_.toString ++ "s")
          ),
          child.maybe <-- isGameOver.signal.map {
            Option.when(_) {
              div(
                  div(
                      cls := "text-2xl text-red-500 mt-4",
                      "Game Over"
                  ),
                  button(
                      cls := "mt-4 p-2 bg-blue-500 text-white rounded",
                      "Restart",
                      onClick --> { _ => restartGame() }
                  )
              )
            }
          }
      )
  )
