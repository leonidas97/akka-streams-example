package logic

import controllers.DogsFact
import play.api.Logger
import scredis._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DogsFactCache @Inject() () {
  private val logger = Logger(getClass)
  private val redis: Redis = Redis()
  implicit val ec: ExecutionContext = redis.dispatcher

  def cacheFacts(facts: List[DogsFact]): Future[List[Unit]] = {
    val fs: List[Future[Unit]] = facts.map(f => cacheFact(f))
    Future.sequence(fs)
  }

  def cacheFact(fact: DogsFact): Future[Unit] = {
    logger.info(s"saving ${fact.id}")
    redis
      .hmSet[String](
        fact.id,
        Map(
          "fact" -> fact.fact,
          "length" -> fact.length.toString,
          "created_at" -> fact.createdAt.toString
        )
      )
  }

  def getFact(id: String): Future[Map[String, String]] = {
    logger.info(s"fetching $id")
    redis.hmGetAsMap(id, "fact", "length", "created_at")
  }
}
