name := "sbt-multi-project-example"

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.example",
  scalaVersion := "2.12.3"
)

// PROJECTS

lazy val global = project
  .in(file("."))
  .settings(settings)
  .aggregate(
    common,
    restful,
    engine
  )

lazy val common = project
  .in(file("common"))
  .settings(commonSettings: _*)
  .settings(
    name := "common",
    settings,
    libraryDependencies ++= commonDependencies
  )

lazy val restful = project
  .in(file("app-restful"))
  .settings(commonSettings: _*)
  .settings(
    name := "app.restful",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.akkaHttp,
      dependencies.akkaStream,
      dependencies.sprayJson
      // dependencies.monocleCore,
      // dependencies.monocleMacro
    )
  )
  .dependsOn(
    common
  )

lazy val engine = project
  .in(file("app-engine"))
  .settings(commonSettings: _*)
  .settings(
    name := "app.engine",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      // dependencies.pureconfig
    )
  )
  .dependsOn(
    common
  )

// DEPENDENCIES

lazy val dependencies =
  new {
    val akkaV           = "2.5.6"
    val akkaHttpV       = "10.1.0-RC2"
    val scalatestV      = "3.0.4"

    val akkaActor       = "com.typesafe.akka"          %% "akka-actor"              % akkaV
    val akkaRemote      = "com.typesafe.akka"          %% "akka-remote"             % akkaV
    val akkaCluster     = "com.typesafe.akka"          %% "akka-cluster"            % akkaV
    val akkaStream      = "com.typesafe.akka"          %% "akka-stream"             % akkaV
    val akkaHttp        = "com.typesafe.akka"          %% "akka-http"               % akkaHttpV
    val sprayJson       = "com.typesafe.akka"          %% "akka-http-spray-json"    % akkaHttpV
    val scalatest       = "org.scalatest"              %% "scalatest"               % scalatestV

    // val logbackV        = "1.2.3"
    // val logstashV       = "4.11"
    // val scalaLoggingV   = "3.7.2"
    // val slf4jV          = "1.7.25"
    // val typesafeConfigV = "1.3.1"
    // val pureconfigV     = "0.8.0"
    // val monocleV        = "1.4.0"
    // val scalacheckV     = "1.13.5"

    // val logback        = "ch.qos.logback"             % "logback-classic"          % logbackV
    // val logstash       = "net.logstash.logback"       % "logstash-logback-encoder" % logstashV
    // val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging"           % scalaLoggingV
    // val slf4j          = "org.slf4j"                  % "jcl-over-slf4j"           % slf4jV
    // val typesafeConfig = "com.typesafe"               % "config"                   % typesafeConfigV
    // val monocleCore    = "com.github.julien-truffaut" %% "monocle-core"            % monocleV
    // val monocleMacro   = "com.github.julien-truffaut" %% "monocle-macro"           % monocleV
    // val pureconfig     = "com.github.pureconfig"      %% "pureconfig"              % pureconfigV
    // val scalacheck     = "org.scalacheck"             %% "scalacheck"              % scalacheckV
  }

lazy val commonDependencies = Seq(
  dependencies.akkaActor,
  dependencies.akkaRemote,
  dependencies.akkaCluster,
  dependencies.scalatest  % "test",

  // dependencies.logback,
  // dependencies.logstash,
  // dependencies.scalaLogging,
  // dependencies.slf4j,
  // dependencies.typesafeConfig,
  // dependencies.scalacheck % "test"
)

// SETTINGS

lazy val settings =
resolverSettings ++
wartremoverSettings ++
scalafmtSettings

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val resolverSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val wartremoverSettings = Seq(
  wartremoverWarnings in (Compile, compile) ++= Warts.allBut(Wart.Throw)
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtTestOnCompile := true,
    scalafmtVersion := "1.2.0"
  )

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + "-" + version.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case _                             => MergeStrategy.first
  }
)
