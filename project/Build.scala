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
    javaJpa,
    filters
    // groupID % artifactID % revision % configuration
    , "com.google.api-client" % "google-api-client" % "1.16.0-rc"
    , "com.google.http-client" % "google-http-client-jackson" % "1.16.0-rc"
    , "com.google.http-client" % "google-http-client-gson" % "1.16.0-rc"
    , "com.google.apis" % "google-api-services-oauth2" % "v2-rev43-1.16.0-rc"
    , "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
    , "org.eclipse.persistence" % "org.eclipse.persistence.jpa" % "2.4.0"
    , "org.syncloud" % "dropbox-client" % "1.5.3"
    // , "com.dropbox.core" % "dropbox-core-sdk" % "[1.6,1.7)"
    , "com.soundcloud" % "java-api-wrapper" % "1.3.0"
    , "com.google.apis" % "google-api-services-youtube" % "v3-rev70-1.16.0-rc"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
      ebeanEnabled := false,
    // Add your own project settings here
      Keys.fork in (Test) := false,
      testOptions in Test ~= { args =>
        for {
          arg <- args
          val ta: Tests.Argument = arg.asInstanceOf[Tests.Argument]
          val newArg = if(ta.framework == Some(TestFrameworks.JUnit)) ta.copy(args = List.empty[String]) else ta
        } yield newArg
      },
      resolvers += (
          "Google api services" at "http://google-api-client-libraries.appspot.com/mavenrepo"
      ),
      resolvers += (
          "EclipseLink JPA" at "http://download.eclipse.org/rt/eclipselink/maven.repo"
      )
  )

}
