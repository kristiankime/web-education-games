// http://mikeslinn.blogspot.com/2013/09/sample-play-22x-buildsbt.html

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
  "postgresql"              %  "postgresql"                  % "9.1-901-1.jdbc4" withSources,
  "com.typesafe.play"       %% "play-slick"                  % "0.5.0.8" withSources
//  "com.typesafe.slick"      %% "slick"                       % "1.0.1" withSources
)     

organization := "com.artclod"

