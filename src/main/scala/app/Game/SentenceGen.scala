package app.Game

import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

object SegmenterWrapper:

  import scala.scalajs.js
  import scala.scalajs.js.annotation.*

  @js.native
  @JSGlobal("Intl.Segmenter")
  private class Segmenter(locale: String, options: js.Dictionary[String])
      extends js.Object:
    def segment(input: String): scalajs.js.Iterable[SegmentElem] = js.native

  @js.native
  private trait SegmentElem extends js.Object:
    val segment: String = js.native
    //    val index: Int = js.native
    //    val input: String = js.native
    val isWordLike: Boolean = js.native

  def segmentText(text: String): Iterable[String] =
    for
      segment <- Segmenter("jp", js.Dictionary("granularity" -> "word"))
        .segment(text)
      if segment.isWordLike
    yield segment.segment

class SentenceGen(gooAppId: String):
  private val markovChain = MarkovChain(3)
  markovChain.addText(SegmenterWrapper
        .segmentText("") // fixme 学習データを入れる
        .toVector)
  private val hiraganaConverter =
    HiraganaConverter.genHiraganaConverter(gooAppId)

  private val sentenceDeque =
    collection.mutable.ArrayDeque.fill(3)(genSentenceWithHiragana())

  private def genSentenceWithHiragana() =
    val sentence = markovChain.generateText(10)
    val hiragana = hiraganaConverter(sentence)
    hiragana.map(sentence -> _)

  def genSentence(): Future[(String, String)] =
    sentenceDeque += genSentenceWithHiragana()
    sentenceDeque.removeLast()
