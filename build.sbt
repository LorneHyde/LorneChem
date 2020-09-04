name := "RealChem"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % "test"
val lift_json = "net.liftweb" %% "lift-json" % "3.4.1"
libraryDependencies += lift_json
libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "2.1.1"
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.3.2"