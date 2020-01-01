/*
 * Copyright (C) 2017 Lightbend Inc. <http://www.lightbend.com>
 */
package akka.cluster.bootstrap.demo;

import akka.actor.Props;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Adapter;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.ClusterEvent;
import akka.cluster.typed.Cluster;
import akka.cluster.typed.ClusterCommand;
import akka.cluster.typed.Subscribe;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.server.AllDirectives;
import akka.management.scaladsl.AkkaManagement;
import akka.management.cluster.bootstrap.ClusterBootstrap;
import akka.stream.Materializer;

public class DemoApp extends AllDirectives {

  DemoApp() {

    ActorSystem.create(Behaviors.setup(context -> {
      akka.actor.ActorSystem classicSystem = Adapter.toClassic(context.getSystem());
      Cluster cluster = Cluster.get(context.getSystem());
      context.getLog().info("Started [" + context.getSystem() + "], cluster.selfAddress = " + cluster.selfMember().address() + ")");
      Materializer mat = Materializer.matFromSystem(classicSystem);

      Http.get(classicSystem).bindAndHandle(complete("Hello world").flow(classicSystem, mat), ConnectHttp.toHost("0.0.0.0", 8080), mat);

      ActorRef<ClusterEvent.MemberEvent> listener = context.spawn(Behaviors.receiveMessage(event -> {
         context.getLog().info("MemberEvent: {}", event);
        return Behaviors.same();
      }), "listener");

      cluster.subscriptions().tell(new Subscribe<>(listener, ClusterEvent.MemberEvent.class));

      //#start-akka-management
      AkkaManagement.get(classicSystem).start();
      //#start-akka-management
      ClusterBootstrap.get(classicSystem).start();

      return Behaviors.empty();
    }), "Appka");
  }

  public static void main(String[] args) {
    new DemoApp();
  }

}

