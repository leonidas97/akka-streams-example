package logic

import play.api.Logger
import play.api.http.Status.OK
import play.api.libs.json.JsArray
import play.api.libs.ws.WSClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DogsFactHttpClient @Inject() ()(implicit executionContext: ExecutionContext, wsClient: WSClient) {
  private val logger = Logger(getClass)
  private val url = "https://dog-facts-api.herokuapp.com/api/v1/resources/dogs?"

  def fetchDogFacts(num: Int): Future[Option[JsArray]] = {
    wsClient
      .url(s"${url}number=$num")
      .get()
      .map { resp =>
        resp.status match {
          case OK =>
            logger.info(s"fetched $num facts")
            Some(resp.json.as[JsArray])
          case _ =>
            logger.error("unexpected response from dog server")
            None
        }
      }
      .recover { ex =>
        logger.error(s"failed to fetch dog facts, reason: ${ex.getMessage}")
        throw ex
      }
  }
}
