package services

import services.ProcessingProgressTracker.registerNewStatus

import java.io.File
import java.util.UUID
import models.ProcessingStatus.Status

object FakeImageProcessor {

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def processImage(sessionId: UUID, file: File) = {
    Thread.sleep(1000)
    for {
      _ <- registerNewStatus(Status.Preparing, sessionId)
      _ = Thread.sleep(1000)
      _ <- registerNewStatus(Status.Parsing, sessionId)
      _ = Thread.sleep(2000)
      _ <- registerNewStatus(Status.Optimising, sessionId)
      _ = Thread.sleep(1000)
      _ <- registerNewStatus(Status.Done, sessionId)
    } yield None
  }
}
