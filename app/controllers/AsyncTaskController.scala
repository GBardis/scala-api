package controllers

import actors.AsyncTaskInActor
import akka.actor.{ActorRef, ActorSystem, Props, Terminated}
import akka.pattern.ask
import akka.util.ByteString
//import akka.protobufv3.internal.ByteString
import akka.stream.IOResult
import akka.stream.scaladsl.FileIO
import akka.stream.scaladsl.Source
import akka.util.Timeout
import models.StartProcess
import play.api.http.HttpEntity
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, ResponseHeader, Result}

import java.util.UUID
import javax.inject._
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps

case object GetInfo

@Singleton
class AsyncTaskController @Inject()(val controllerComponents: ControllerComponents, val actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends BaseController {
  def createAsyncTask(): Action[AnyContent] = Action {
    implicit val timeout = Timeout(5 seconds)
    val taskId = taskID
    actorSystem.scheduler.scheduleOnce(
      1 seconds,
      actor(taskId),
      StartProcess("public/csv/tcc_ceds_music.csv")
    )
    Ok(response(taskId))
  }


  def getInfoAsyncTask(id: String): Action[AnyContent] = Action {
    implicit val timeout = Timeout(100 seconds)
    val selection = actorSystem.actorSelection(s"user/${id}").ask(GetInfo)

    val result = Await.result(selection, timeout.duration)
    Ok(result.toString)
  }

  def terminateAsyncTask(id: String): Action[AnyContent] = Action {
    implicit val timeout = Timeout(15 seconds)
    val selection = actorSystem.actorSelection(s"user/${id}").ask(Terminated)

    val result = Await.result(selection, timeout.duration)
    Ok(result.toString)
  }

  private

  def actor(taskId: String): ActorRef = {
    actorSystem.actorOf(Props[AsyncTaskInActor], name = s"${taskId}")
  }

  def taskID = {
    UUID.randomUUID().toString
  }

  def response(taskId: String): JsValue = {
    Json.obj(
      "taskID" -> taskId,
      "message" -> "Task created"
    )
  }

  def getFile = Action {
    val file = new java.io.File("public/json/songs.json")
    val path: java.nio.file.Path = file.toPath
    if (file.isFile) {
      val source: Source[ByteString, Future[IOResult]] = FileIO.fromPath(path)
      Result(
        header = ResponseHeader(200, Map.empty),
        body = HttpEntity.Streamed(source, None, Some("application/json"))
      )
    } else {
      NotFound
    }
  }
}
