package com.scala.kafka

import com.rockymadden.stringmetric.similarity.HammingMetric
import scala.io.Source._
import resource._
import scala.collection.mutable

object FuzzySearch extends Config with App {

  private val textFile = "src/main/resources/data.txt"
  private val word = "her"
  private val fuzziness = 1

  case class FuzzyWord(word: String, line: Int, pos: Int)

  def fuzzySearch(text: List[String], word: String, fuzziness: Int): mutable.MutableList[FuzzyWord] = {
    val wordLength = word.length

    def iterateList(text: List[String], numLine: Int, resultList: mutable.MutableList[FuzzyWord]):
    mutable.MutableList[FuzzyWord] = {
      if (text.isEmpty) resultList
      else {
        val line = text.head

        def iterateLine(index: Int): mutable.MutableList[FuzzyWord] = {
          if (index + wordLength > line.length) iterateList(text.tail, numLine + 1, resultList)
          else {
            val subWord = line.substring(index, index + wordLength)
            val i: Option[Int] = HammingMetric.compare(subWord, word)
            if (i.isDefined && i.get <= fuzziness) {
              resultList.+=:(FuzzyWord(subWord, numLine, index + 1))
            }
            iterateLine(index + 1)
          }
        }
        iterateLine(0)
      }
    }

    iterateList(text, 1, mutable.MutableList())
  }

  def readTextFile(filename: String): Option[List[String]] = {
    try {
      var lines = List[String]()
      for (source <- managed(scala.io.Source.fromFile(textFile)))
        lines = (for (line <- source.getLines) yield line).toList
      Some(lines)
    }
    catch {
      case e: Exception => None
    }
  }

  readTextFile(textFile) match {
    case Some(lines) => fuzzySearch(lines, word, fuzziness).foreach(println)
    case None => println("Couldn't read file!")
  }

}
