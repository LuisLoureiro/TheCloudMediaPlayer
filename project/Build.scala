import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "The_Cloud_Media_Player"
  val appVersion      = "0.5-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaJpa,
    filters
    // groupID % artifactID % revision % configuration
    , "com.google.api-client" % "google-api-client" % "1.+"
    , "com.google.http-client" % "google-http-client-jackson" % "1.+"
    , "com.google.http-client" % "google-http-client-gson" % "1.+"
    , "com.google.apis" % "google-api-services-oauth2" % "v2-rev51-1.17.0-rc"
    , "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
    , "org.eclipse.persistence" % "org.eclipse.persistence.jpa" % "2.5.1"
    , "org.syncloud" % "dropbox-client" % "1.5.+"
    // , "com.dropbox.core" % "dropbox-core-sdk" % "[1.6,1.7)"
    , "com.soundcloud" % "java-api-wrapper" % "1.3.+"
    // , "com.google.apis" % "google-api-services-youtube" % "v3-rev70-1.16.0-rc"
    , "com.llorieruo.projects" % "oauth2-login" % "0.2"
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
          "Luis Loureiro GitHub" at "https://raw2.github.com/LuisLoureiro/mvn-repo/master"
      )
  )

}
