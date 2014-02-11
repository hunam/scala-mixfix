import sbt._
import Keys._

object BuildSettings {
  val paradiseVersion = "2.0.0-M3"
  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "mixfix",
    version := "0.1.0",
    //scalacOptions ++= Seq("-Ymacro-debug-lite"),
    scalaVersion := "2.10.3",
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += Resolver.sonatypeRepo("releases"),
    addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)
  )
}

object MyBuild extends Build {
  import BuildSettings._

  lazy val root: Project = Project(
    "root",
    file("."),
    settings = buildSettings ++ Seq(
      run <<= run in Compile in test
    )
  ) aggregate(mixfix, test)

  lazy val mixfix: Project = Project(
    "mixfix",
    file("mixfix"),
    settings = buildSettings ++ Seq(
      libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _),
      libraryDependencies ++= (
        if (scalaVersion.value.startsWith("2.10")) List("org.scalamacros" % "quasiquotes" % paradiseVersion cross CrossVersion.full)
        else Nil
      )
    )
  )

  lazy val test: Project = Project(
    "test",
    file("test"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "2.0" % "test",
        "com.chuusai" % "shapeless" % "2.0.0-M1" % "test" cross CrossVersion.full
      )
    )
  ) dependsOn(mixfix)
}
