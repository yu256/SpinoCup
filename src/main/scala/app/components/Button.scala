package app.components

import com.raquo.laminar.api.L.{onClick as onClick_, *}
import org.scalajs.dom

def myButton(text: String, onClick: dom.MouseEvent => Unit): HtmlElement =
  button(
      className := "btn btn-primary bg-blue-500 rounded p-2",
      onClick_ --> onClick,
      text
  )
