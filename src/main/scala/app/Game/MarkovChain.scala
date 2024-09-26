package app.Game

import scala.collection.mutable
import scala.util.Random
import util.control.Breaks.*

class MarkovChain(val order: Int = 1):
  private val chain =
    mutable.Map[IndexedSeq[String], mutable.ListBuffer[String]]()
  private val random = Random()

  def addText(segments: IndexedSeq[String]): Unit =
    segments.sliding(order + 1).foreach { window =>
      val key = window.take(order)
      val nextWord = window.last
      if key.nonEmpty then
        val buffer = chain.getOrElseUpdate(key, mutable.ListBuffer[String]())
        buffer += nextWord
    }

  def generateText(maxWords: Int): String =
    if chain.isEmpty then return ""
    var currentWords = chain.keys.toIndexedSeq(random.nextInt(chain.size))
    val result = mutable.ListBuffer[String]()
    result ++= currentWords

    breakable {
      for (_ <- 1 to maxWords - order)
        chain.get(currentWords) match
          case Some(possibleNextWords) =>
            val nextWord =
              possibleNextWords(random.nextInt(possibleNextWords.size))
            result += nextWord
            currentWords = currentWords.tail :+ nextWord
          case None => break
    }

    result.mkString
