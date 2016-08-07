// For a more complete example see: http://mikeslinn.blogspot.com/2013/09/sample-play-22x-buildsbt.html

import sbt._
import sbt.Keys._
import play.Project._

scalacOptions += "-feature"

play.Project.playScalaSettings

name := "web-education-games"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "postgresql"              %  "postgresql"          % "9.1-901-1.jdbc4" withSources,
  "com.typesafe.play"       %% "play-slick"          % "0.6.1" withSources,
  "ws.securesocial"         %% "securesocial"        % "2.1.4" withSources,
  "com.typesafe"            %% "play-plugins-mailer" % "2.2.0" withSources,
  "org.mozilla"             %  "rhino"               % "1.7R4" withSources,
  "com.github.mumoshu"      %% "play2-memcached"     % "0.5.0-RC1" withSources, // 0.4.0" withSources // or 0.5.0-RC1 to try the latest improvements
  "org.planet42"            %% "laika-core"           % "0.6.0" withSources
)

resolvers ++= Seq(
  Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("Spy Repository", new URL("http://files.couchbase.com/maven2"))(Resolver.mavenStylePatterns) // required to resolve `spymemcached`, the plugin's dependency.
)

routesImport += "models.support._"

