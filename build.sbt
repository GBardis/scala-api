ThisBuild / scalaVersion := "2.13.10"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """scala-api""",
    libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
      "com.fasterxml.jackson.core" % "jackson-core" % "2.11.4",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.4",
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % "2.11.4",
      "com.typesafe.play" %% "play-json" % "2.9.3",
      "com.beachape" %% "enumeratum" % "1.7.0",
      "com.typesafe.akka" %% "akka-actor" % "2.6.20"
    )
  )