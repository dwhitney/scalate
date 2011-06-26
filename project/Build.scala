import sbt._
import Keys._


object BuildSettings{
  
  val asmVersion = "3.1"
  val camelVersion = "2.6.0-fuse-00-00"
  val jansiVersion = "1.4"
  val jerseyVersion = "1.5"
  val jettyVersion = "7.2.1.v20101111"
  val jlineVersion = "0.9.95.20100209"
  val junitVersion = "4.5"
  val karafVersion = "2.1.0"
  val logbackVersion = "0.9.26"
  val markdownVersion = "0.3.0-1.0.2b4"
  val osgiVersion = "4.2.0"
  val scalaTestVersion = "1.4.1"
  val scalamdVersion = "1.5"
  val servletApiVersion = "2.5"
  val slf4jVersion = "1.6.1"
  val springVersion = "3.0.5.RELEASE"
  val wikitextVersion = "1.2"
  
  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.fusesource",
    scalaVersion := "2.9.0",
    shellPrompt := { state: State => "%s> ".format(Project.extract(state).currentProject.id)}
  )
}

object Repositories{
  val fusesource_m2 = "FuseSource Community Release Repository" at "http://repo.fusesource.com/maven2"
  val fusesource_m2_snapshot = "FuseSource Community Snapshot Repository" at "http://repo.fusesource.com/maven2-snapshot"
  val fusesource_nexus_m2_snapshot = "FuseSource Community Snapshot Repository" at "http://repo.fusesource.com/nexus/content/groups/public-snapshots"
  val java_net_m2 = "java.net Maven 2 Repo" at "http://download.java.net/maven/2"
  val zt_repo = "Zero turnaround repo" at "http://repos.zeroturnaround.com/maven2"
}

object Scalate extends Build{
  import BuildSettings._
  import Repositories._
  
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
      "org.slf4j" % "slf4j-api" % slf4jVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
      "junit" % "junit" % junitVersion % "test"
    )))
    
  lazy val scalate_core = Project("scalate-core", file("scalate-core"),
  settings = buildSettings ++ Seq( libraryDependencies := Seq(
    "javax.servlet" % "servlet-api" % servletApiVersion % "compile",
    "com.sun.jersey" % "jersey-server" % jerseyVersion % "compile",
    "org.scala-lang" % "scala-compiler" % "2.9.0" % "compile",
    "org.fusesource.scalamd" % "scalamd" % scalamdVersion % "compile",
    "ch.qos.logback" % "logback-classic" % logbackVersion % "runtime",
    "org.osgi" % "org.osgi.core" % osgiVersion % "compile",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "junit" % "junit" % junitVersion % "test"
  ))) dependsOn(scalate_util)
  
  lazy val scalate_test = Project("scalate-test", file("scalate-test"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.scalatest" %% "scalatest" % scalaTestVersion,
      "junit" % "junit" % junitVersion,
      "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.0a5",
      "org.eclipse.jetty" % "jetty-server" % jettyVersion,
      "org.eclipse.jetty" % "jetty-webapp" % jettyVersion,
      "org.eclipse.jetty" % "jetty-util" % jettyVersion
    ))) dependsOn(scalate_core)
    
  lazy val scalate_wikitext = Project("scalate-wikitext", file("scalate-wikitext"),
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.fusesource.wikitext" % "confluence-core" % wikitextVersion,
      "org.fusesource.wikitext" % "textile-core" % wikitextVersion
    ))) dependsOn(scalate_test)
  
  lazy val scalate_page = Project("scalate-page", file("scalate-page"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.yaml" % "snakeyaml" % "1.7"
    ))) dependsOn(scalate_wikitext)
    
  lazy val scalate_camel = Project("scalate-camel", file("scalate-camel"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.apache.camel" % "camel-spring" % camelVersion,
      "org.apache.camel" % "camel-scala" % camelVersion
    ))) dependsOn(scalate_wikitext)

  lazy val scalate_web = Project("scalate-web", file("scalate-web"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "javax.servlet" % "servlet-api" % servletApiVersion % "provided"
    ))) dependsOn(scalate_page)
    
  lazy val scalate_jsp_converter = Project("scalate-jsp-converter", file("scalate-jsp-converter"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.apache.karaf.shell" % "org.apache.karaf.shell.console" % karafVersion
    ))) dependsOn(scalate_web)
    
  lazy val scalate_war = Project("scalate-war", file("scalate-war"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
    ))) dependsOn(scalate_web)
    
  lazy val scalate_guice = Project("scalate-guice", file("scalate-guice"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "com.sun.jersey.contribs" % "jersey-guice" % jerseyVersion
    ),
    resolvers := Seq(java_net_m2)
    )) dependsOn(scalate_test)
    
  lazy val scalate_spring_mvc = Project("scalate-spring-mvc", file("scalate-spring-mvc"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.springframework" % "spring-webmvc" % springVersion
    ))) dependsOn(scalate_test)
    
  lazy val scalate_jrebel = Project("scalate-jrebel", file("scalate-jrebel"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.zeroturnaround" % "jr-sdk" % "3.1.2" % "provided"
    ),
      resolvers := Seq(zt_repo)
    )) dependsOn(scalate_test)

  lazy val scalate_jruby = Project("scalate-jruby", file("scalate-jruby"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.jruby" % "jruby-complete" % "1.5.6"
    ))) dependsOn(scalate_test)
    
  lazy val scalate_tool = Project("scalate-tool", file("scalate-tool"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "xmlrpc" % "xmlrpc-client" % "3.0" from "http://repo1.maven.org/maven2/xmlrpc/xmlrpc-client/3.0/xmlrpc-client-3.0.jar",
      "xmlrpc" % "xmlrpc-common" % "3.0" from "http://repo1.maven.org/maven2/xmlrpc/xmlrpc-common/3.0/xmlrpc-common-3.0.jar",
      "org.apache.karaf.shell" % "org.apache.karaf.shell.console" % karafVersion,
      "org.codehaus.swizzle" % "swizzle-confluence" % "1.4"
    ))) dependsOn(scalate_test)
    
  lazy val scalate_website = Project("scalate-website", file("scalate-website"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
    ))) dependsOn(scalate_test)

  lazy val scalate_markdownj = Project("scalate-markdownj", file("scalate-markdownj"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.markdownj" % "markdownj" % markdownVersion
    ))) dependsOn(scalate_test)

  lazy val scalate_bookstore = Project("scalate-bookstore", file("samples/scalate-bookstore"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.markdownj" % "markdownj" % markdownVersion
    ))) dependsOn(scalate_war, scalate_guice)
    
  lazy val scalate_example = Project("scalate-example", file("samples/scalate-example"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
    ))) dependsOn(scalate_war)
    
  lazy val scalate_sample = Project("scalate-sample", file("samples/scalate-sample"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
    ))) dependsOn(scalate_war, scalate_markdownj)
    
  lazy val scalate_sample_precompile = Project("scalate-sample-precompile", file("samples/scalate-sample-precompile"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
    ))) dependsOn(scalate_war)

  lazy val scalate_sample_scuery = Project("scalate-sample-scuery", file("samples/scalate-sample-scuery"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
    ))) dependsOn(scalate_war, scalate_guice)
  
  lazy val scalate_sample_sitegen = Project("scalate-sample-sitegen", file("samples/scalate-sample-sitegen"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
    ))) dependsOn(scalate_war)
        
  lazy val scalate_sample_spring_mvc = Project("scalate-sample-spring-mvc", file("samples/scalate-sample-spring-mvc"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
    ))) dependsOn(scalate_war, scalate_spring_mvc)
}