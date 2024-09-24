package app.Game

import com.raquo.laminar.api.L.*

def gameView =
  import RomajiParser.*
  val japanese = Var("")
  val input = Var("")

  div(
      cls := "h-screen w-screen flex flex-col justify-center items-center",
      tabIndex := 0,
      autoFocus := true,
      onKeyDown --> (_.key match {
        case key if key.length == 1 =>
          input.update(_ ++ key)
          parseRomaji(input.now()) match {
            case RomajiState.Complete(jp) =>
              japanese.update(_ ++ jp)
              input.set("")
            case RomajiState.Invalid => input.set("")
            case RomajiState.Partial(jp, rest) =>
              japanese.update(_ ++ jp)
              input.set(rest)
            case RomajiState.Progress =>
          }
        case "Backspace" | "Delete" =>
          (if input.now().nonEmpty then input else japanese)
            .update(_.dropRight(1))
        case _ =>
      }),
      div(
          cls := "flex flex-col items-center",
          span(
              text <-- japanese.signal.combineWithFn(input.signal)(_ ++ _)
          )
      )
  )
