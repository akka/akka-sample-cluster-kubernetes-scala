package akka.sample.cluster.kubernetes

import akka.actor.CoordinatedShutdown
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.ClusterEvent
import akka.cluster.typed.{Cluster, Subscribe}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.javadsl.AkkaManagement
import akka.{actor => classic}

object DemoApp extends App {

  ActorSystem[Nothing](
    Behaviors.setup[Nothing] { context =>
      import akka.actor.typed.scaladsl.adapter._
      implicit val classicSystem: classic.ActorSystem = context.system.toClassic
      implicit val e = context.system.executionContext
      val shutdown = CoordinatedShutdown(classicSystem)

      val cluster = Cluster(context.system)
      context.log.info("Started [" + context.system + "], cluster.selfAddress = " + cluster.selfMember.address + ")")

      Http().bindAndHandle(complete("Hello world"), "0.0.0.0", 8080)

      // Create an actor that handles cluster domain events
      val listener = context.spawn(Behaviors.receiveMessage[ClusterEvent.MemberEvent] {
        case event: ClusterEvent.MemberEvent =>
          context.log.info("MemberEvent: {}", event)
          Behaviors.same
      }, "listener")

      Cluster(context.system).subscriptions ! Subscribe(listener, classOf[ClusterEvent.MemberEvent])

      AkkaManagement.get(classicSystem).start()
      ClusterBootstrap.get(classicSystem).start()
      Behaviors.empty
    }, "Appka")
}