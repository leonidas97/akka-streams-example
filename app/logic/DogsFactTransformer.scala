package logic

import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.json.JsValue

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class DogsFactTransformer @Inject() () {
  private val logger = Logger(getClass)

  def transform(factsJson: Seq[JsValue]): Future[List[DogsFact]] =
    Future.successful {
      factsJson.map { jsVal =>
        val id = UUID.randomUUID().toString
        val fact = (jsVal \ "fact").as[String]
        logger.info(s"transforming $id")
        DogsFact(id, fact, fact.length, DateTime.now())
      }.toList
    }
}
