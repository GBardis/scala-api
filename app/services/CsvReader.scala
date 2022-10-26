package services

import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import models.Song
import utils.JsonUtil

import java.nio.file.Paths
import java.time.temporal.ChronoUnit
import java.time.{LocalDateTime, ZoneId}
import java.util
import scala.io.{BufferedSource, Source}

class CsvReader(pathToFile: String) {
  var line_count = 0
  val timeStart = LocalDateTime.now()
  val mapper = new ObjectMapper()
  val songs = new util.ArrayList[String]()
  var timeToProcess = ""

  def readFile: BufferedSource = {
    Source.fromResource(pathToFile)
  }

  def csvToJson: Unit = {
    for (line <- readFile.getLines.drop(1)) {
      line_count = line_count + 1
      val values = line.split(",").map(_.trim)
      val song = new Song(values(0), values(1), values(2), values(3), values(4), values(5))
      val stringSong = JsonUtil.toJson(song)
      songs.add(stringSong)
    }
    readFile.close()

    val timeFinished = LocalDateTime.now()
    val i1 = timeStart.atZone(ZoneId.of("Europe/London"))
    val i2 = timeFinished.atZone(ZoneId.of("Europe/London"))

    timeToProcess = i1.until(i2, ChronoUnit.SECONDS).toString
  }

  def jsonWriteToFile: Unit = {
    mapper.enable(SerializationFeature.INDENT_OUTPUT)
    mapper.writeValue(Paths.get("public/json/songs.json").toFile, songs)
  }
}
