name := "web-education-games"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

play.Project.playScalaSettings

val appDependencies = Seq(
    "postgresql" % "postgresql" % "9.1-901.jdbc4"
  )