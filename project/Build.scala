import sbt._
import Keys._


object BuildSettings{
  
  val asmVersion = "3.1"
  val jansiVersion = "1.4"
  val jerseyServerVersion = "1.5"
  val jettyVersion = "7.2.1.v20101111"
  val jlineVersion = "0.9.95.20100209"
  val junitVersion = "4.5"
  val karafVersion = "2.1.0"
  val logbackVersion = "0.9.26"
  val markdownVersion = "0.3.0-1.0.2b4"
  val springVersion = "3.0.5.RELEASE"
  val osgiVersion = "4.2.0"
  val scalaTestVersion = "1.4.1"
  val scalamdVersion = "1.5"
  val servletApiVersion = "2.5"
  val slf4jVersion = "1.6.1"
  val wikitextVersion = "1.2"
  
  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.fusesource",
    scalaVersion := "2.9.0",
    shellPrompt := { state: State => "%s> ".format(Project.extract(state).currentProject.id)}
  )
}

object Scalate extends Build{
  import BuildSettings._
  
  /*lazy val scalate_util = project("scalate-util", "scalate-util", new ScalateUtil(_))
  lazy val scalate_core = project("scalate-core", "scalate-core", new ScalateCore(_), scalate_util)
  lazy val scalate_test = project("scalate-test", "scalate-test", new ScalateTest(_), scalate_core)
  lazy val scalate_page = project("scalate-page", "scalate-page", new ScalatePage(_), scalate_core, scalate_test)
  lazy val scalate_wikitext = project("scalate-wikitext", "scalate-wikitext", new ScalateWikiText(_), scalate_core, scalate_test)
  lazy val scalate_camel = project("scalate-camel", "scalate-camel", new ScalateCamel(_), scalate_core, scalate_test)
  lazy val scalate_jsp_converter = project("scalate-jsp-converter", "scalate-jsp-converter", new ScalateJspConverter(_), scalate_core)
  lazy val scalate_war = project("scalate-war", "scalate-war", new ScalateWar(_), scalate_core, scalate_test)
  lazy val scalate_sample = project("scalate-sample", "scalate-sample", new ScalateSample(_), scalate_core, scalate_test, scalate_war)
  lazy val scalate_bookstore = project("scalate-bookstore", "scalate-bookstore", new ScalateBookstore(_), scalate_core, scalate_test, scalate_war)*/
  
  lazy val scalate_util = Project("scalate-util", file("scalate-util"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.slf4j" % "slf4j-api" % slf4jVersion
    )))
    
  lazy val scalate_core = Project("scalate-core", file("scalate-core"),
  settings = buildSettings ++ Seq( libraryDependencies := Seq(
    "javax.servlet" % "servlet-api" % servletApiVersion % "compile",
    "com.sun.jersey" % "jersey-server" % jerseyServerVersion % "compile",
    "org.scala-lang" % "scala-compiler" % "2.9.0" % "compile",
    "org.fusesource.scalamd" % "scalamd" % scalamdVersion % "compile",
    "ch.qos.logback" % "logback-classic" % logbackVersion % "runtime",
    "org.osgi" % "org.osgi.core" % osgiVersion % "compile",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "junit" % "junit" % junitVersion % "test"
  ))) dependsOn(scalate_util)
  
}