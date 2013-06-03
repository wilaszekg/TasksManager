import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "tasksManager"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "be.objectify" %% "deadbolt-java" % "2.1-RC2",
    "org.powermock" % "powermock-module-junit4" % "1.5",
    "org.powermock" % "powermock-api-mockito" % "1.5",
    "org.seleniumhq.selenium" % "selenium-java" % "2.32.0" % "test",
    "postgresql" % "postgresql" % "9.1-901.jdbc4"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += Resolver.url("Objectify Play Repository", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
	resolvers += Resolver.url("Objectify Play Snapshot Repository", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns)    
  )

}
