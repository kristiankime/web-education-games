import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

	val appName = "web-education-games"
	val appVersion = "1.0-SNAPSHOT"

	val appDependencies = Seq(
		// Add your project dependencies here,
		jdbc,
		anorm,
		"org.specs2" %% "specs2" % "2.0",
		"securesocial" %% "securesocial" % "master-SNAPSHOT")

	val main = play.Project(appName, appVersion, appDependencies).settings {
		// Add your own project settings here   
		resolvers += Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
	}

}
