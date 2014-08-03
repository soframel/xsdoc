name := """xsdoc"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers += "Maven Central Repository" at "http://repo1.maven.org/maven2"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "com.sun.xsom"%"xsom"%"20110809",
  "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"
)