package logic

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class DogsFactProcessingStream @Inject() (
    dogsFactHttpClient: DogsFactHttpClient,
    dogsFactTransformer: DogsFactTransformer,
    dogsFactCache: DogsFactCache
) {

  implicit val actorSystem: ActorSystem = ActorSystem("processing")
  implicit val ec: ExecutionContext = actorSystem.dispatcher
  val parallelism = 4

  //TODO: move to method
  Source(List.fill(1000)(20))
    .mapAsync(parallelism)(n => dogsFactHttpClient.fetchDogFacts(n))
    .mapAsync(parallelism)(js => dogsFactTransformer.transform(js.get.value.toSeq))
    .mapAsync(parallelism)(facts => dogsFactCache.cacheFacts(facts))
    .run()
}
