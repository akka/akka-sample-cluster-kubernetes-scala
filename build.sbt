organization in ThisBuild := "com.lightbend"

name := """akka-sample-cluster-kubernetes-scala"""

version := "0.1"

scalaVersion := "2.13.0"
lazy val akkaHttpVersion = "10.1.10"
lazy val akkaVersion    = "2.6.2"

scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")
classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.AllLibraryJars
fork in run := true
Compile / run / fork := true

mainClass in (Compile, run) := Some("akka.sample.cluster.kubernetes.DemoApp")

enablePlugins(JavaServerAppPackaging)
enablePlugins(DockerPlugin)

dockerExposedPorts := Seq(8080, 8558, 2552)

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-cluster-typed"         % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % "1.0.5",
    "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % "1.0.5",

    "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test


//    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
//    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
//    "org.scalatest" %% "scalatest" % "3.0.8" % "test",
//
//    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
//    "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
//    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
//    "com.typesafe.akka" %% "akka-persistence-typed" % akkaVersion,
//    "com.typesafe.akka" %% "akka-cluster-sharding-typed" % akkaVersion,
//    "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion,
//    "com.typesafe.akka" %% "akka-multi-node-testkit"    % akkaVersion,

  )
}
