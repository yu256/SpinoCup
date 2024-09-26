package app.Game

import com.raquo.laminar.api.L.*
import org.scalajs.dom

import concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.given
import scala.scalajs.js.timers.setInterval
import scala.util.chaining.scalaUtilChainingOps

def gameView(gooAppId: String, chainLen: Int, toLearnText: String) =
  import RomajiParser.*

  enum SentenceState:
    case Loaded(sentence: (String, String))
    case Loading(prev: Option[(String, String)])

  val japanese = Var("")
  val input = Var("")
  val sentence = Var(SentenceState.Loading(None))

  val score = Var(0)
  val timeLeft = Var(60)
  val isGameOver = Var(false)

  val genSentence =
    val generator = SentenceGen(gooAppId, chainLen, toLearnText)
    () => generator.genSentence().map(SentenceState.Loaded(_) pipe sentence.set)

  genSentence().foreach { _ =>
    setInterval(1.second) {
      if timeLeft.now() > 0 then timeLeft.update(_ - 1)
      else isGameOver.set(true)
    }
  }

  def restartGame(): Unit =
    timeLeft.set(60)
    score.set(0)
    sentence.update {
      case SentenceState.Loaded(cur) =>
        SentenceState.Loading(Some(cur))
      case _ => throw Error("unreachable")
    }
    genSentence()
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
            case (jp, SentenceState.Loaded(_, hiragana)) if jp == hiragana =>
              score.update(_ + 10)
              sentence.update {
                case SentenceState.Loaded(prev) =>
                  SentenceState.Loading(Some(prev))
                case otherwise => otherwise
              }
              genSentence()
              japanese.set("")
            case (jp, SentenceState.Loaded(_, hiragana))
                if !hiragana.startsWith(jp) =>
              japanese.update(_.dropRight(1))
              score.update(_ - 5)
            case _ =>
          }
      },
      div(
          cls := "flex flex-col items-center",
          div(
              cls := "text-4xl whitespace-pre-wrap",
              text <-- sentence.signal.map {
                case SentenceState.Loading(None) => "Loading..."
                case SentenceState.Loading(Some((prevJp, prevHiragana))) =>
                  s"Loading...\n$prevJp\n($prevHiragana)"
                case SentenceState.Loaded((japanese, hiragana)) =>
                  s"$japanese\n($hiragana)"
              }
          ),
          span(
              cls := "text-3xl mt-4",
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
                      onClick --> { _ => restartGame() },
                      disabled <-- sentence.signal.map {
                        case SentenceState.Loading(_) => true
                        case _                        => false
                      }
                  )
              )
            }
          }
      )
  )
