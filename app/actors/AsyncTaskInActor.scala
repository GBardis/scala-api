package actors

import akka.actor.{Actor, Identify, Terminated}
import akka.http.scaladsl.model.DateTime
import controllers.GetInfo
import models.StartProcess
import play.api.libs.json.Json
import services.CsvReader

class AsyncTaskInActor extends Actor {
  var csvReader: CsvReader = null
  var pathToFile: String = null

  override def receive: Receive = {
    case StartProcess(path) =>
      pathToFile = path
      Console.println(s"Message ${pathToFile} received at ${DateTime.now} path ${self.path}")
      start_process(pathToFile)
      Console.println(s"Process finished path ${self.path}")
    case Terminated =>
      Console.println(s"Process Killed ${self.path}")
      context.system.terminate()
      sender ! "Teminated"
    case GetInfo =>
      Console.println(s"Info send ${self.path}")
      sender ! Json.obj(
        "LineCount" -> csvReader.line_count,
        "timeToProcess" -> csvReader.timeToProcess,
        "PathToFile" -> pathToFile
      )
    case Identify =>
      Console.println(s"Process ${self.path}")
      sender ! self.path.name
  }

  def start_process(path: String): Unit = {
    csvReader = new CsvReader(path)
    csvReader.csvToJson
    csvReader.jsonWriteToFile
  }
}