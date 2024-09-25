package app.Game

import scala.util.Random

object SentenceGen:
  def genSentence(): (String, String) =
    genAtsu()

  private val atsuSeq = Vector('厚', '熱', '暑')
  private val atsuNextSeq = Vector("い", "すぎる", "くない")

  extension [A](seq: IndexedSeq[A])
    private def random(): A = seq(Random.nextInt(seq.length))

  def genAtsu(): (String, String) =
    val atsu = atsuSeq.random()
    val next = atsuNextSeq.random()
    s"$atsu$next" -> s"あつ$next"
