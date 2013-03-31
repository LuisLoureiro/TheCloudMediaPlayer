import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "The_Cloud_Media_Player"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    // groupID % artifactID % revision % configuration
    "com.google.api-client" % "google-api-client" % "1.14.1-beta",
    "com.google.http-client" % "google-http-client-jackson" % "1.14.1-beta",
    "com.google.http-client" % "google-http-client-gson" % "1.14.1-beta",
    "com.google.apis" % "google-api-services-oauth2" % "v2-rev32-1.14.1-beta"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
      resolvers += (
          "Google api services" at "http://google-api-client-libraries.appspot.com/mavenrepo"
      )
  )

}
