package controllers
import logic.{DogsFact, DogsFactCache, DogsFactHttpClient, DogsFactProcessingStream, DogsFactTransformer}
import org.joda.time.DateTime
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Injecting

import java.util.UUID

class DogsAppSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting {

  "DogsApp" should {

    "DogsHttpClient" should {
      "return JsArray" in {
        val client = inject[DogsFactHttpClient]
        val factsF = client.fetchDogFacts(5)
        ScalaFutures.whenReady(factsF, Timeout(5.seconds)) { facts =>
          facts.get.value.size mustBe 5
          println(facts.get.value)
        }
      }
    }

    "DogsFactTransformer" should {
      "return transformed array" in {
        val client = inject[DogsFactHttpClient]
        val transformer = inject[DogsFactTransformer]
        val factsF = client.fetchDogFacts(5)
        ScalaFutures.whenReady(factsF, Timeout(5.seconds)) { facts =>
          val transformed = transformer.transform(facts.get.value.toSeq)
          transformed.size mustBe 5
          println(transformed)
        }
      }
    }

    "DogsFactCache" should {
      "save and fetch successfully" in {
        val cache = inject[DogsFactCache]
        val factStr = "dog fact num 1"
        val fact = DogsFact(UUID.randomUUID().toString, factStr, factStr.length, DateTime.now())
        ScalaFutures.whenReady(cache.cacheFact(fact), Timeout(5.seconds)) { _ => succeed }
        ScalaFutures.whenReady(cache.getFact(fact.id), Timeout(5.seconds)) { cachedFact =>
          cachedFact.get("fact") mustBe Some(factStr)
          println(cachedFact)
        }
      }
    }

    "DogsFactProcessor" should {
      "show all fetched facts" in {
        inject[DogsFactProcessingStream]
        Thread.sleep(10000)
        succeed
      }
    }

  }
}
