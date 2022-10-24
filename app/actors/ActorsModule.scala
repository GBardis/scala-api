package actors

import com.google.inject.AbstractModule
import play.libs.akka.AkkaGuiceSupport

import java.util.UUID

class ActorsModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    val taskId = UUID.randomUUID().toString
    bindActor(classOf[AsyncTaskInActor], taskId)
  }
}