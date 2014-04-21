// For a more complete example see: http://mikeslinn.blogspot.com/2013/09/sample-play-22x-buildsbt.html

import sbt._
import sbt.Keys._
import play.Project._

play.Project.playScalaSettings

name := "web-education-games"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "postgresql"           %  "postgresql"          % "9.1-901-1.jdbc4" withSources,
  "com.typesafe.play"    %% "play-slick"          % "0.5.0.8" withSources,
  "securesocial"         %% "securesocial"        % "2.1.2" withSources,
  "com.typesafe"         %% "play-plugins-mailer" % "2.2.0" withSources,
  "com.github.tototoshi" %% "slick-joda-mapper"   % "0.4.0" withSources //from https://github.com/tototoshi/slick-joda-mapper
)     

resolvers ++= Seq(
  Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)
)

templatesImport ++= Seq("models._", "viewsupport.organization._", "viewsupport.question._")

routesImport += "models.support._"

// TODO check this out http://stackoverflow.com/questions/10436815/how-to-use-twitter-bootstrap-2-with-play-framework-2-x
