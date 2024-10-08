package app.Game

import org.scalajs.dom
import org.scalajs.dom.HttpMethod

import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.JSON

object HiraganaConverter:
  def genHiraganaConverter(appId: String)(str: String): Future[String] =
    for
      res <- dom
        .fetch("https://labs.goo.ne.jp/api/hiragana",
            new dom.RequestInit {
              method = HttpMethod.POST
              headers = js.Dictionary("Content-Type" -> "application/json")
              body = JSON.stringify(js.Dictionary("app_id" -> js.Array(appId),
                      "sentence" -> str, "output_type" -> "hiragana"))
            })
        .toFuture
      json <- res.json().toFuture
    yield json.asInstanceOf[Response].converted.filterNot(_.isWhitespace)

  private trait Response extends js.Object:
//    val request_id: String
//    val output_type: String
    val converted: String
