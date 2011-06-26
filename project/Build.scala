import sbt._
import Keys._


object BuildSettings{
  
  val asmVersion = "3.1"
  val camelVersion = "2.6.0-fuse-00-00"
  val jansiVersion = "1.4"
  val jerseyVersion = "1.5"
  val jettyVersion = "7.2.1.v20101111"
  val jlineVersion = "0.9"
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
    crossScalaVersions := Seq("2.9.0-1", "2.9.0", "2.8.1"),
    shellPrompt := { state: State => "%s> ".format(Project.extract(state).currentProject.id)}
  )
}

object Repositories{
  val fusesource_nexus_staging = "Fusesource Release Repository" at "http://repo.fusesource.com/nexus/service/local/staging/deploy/maven2"
  val fusesource_m2 = "FuseSource Community Release Repository" at "http://repo.fusesource.com/maven2"
  val fusesource_m2_snapshot = "FuseSource Community Snapshot Repository" at "http://repo.fusesource.com/maven2-snapshot"
  val fusesource_nexus_m2_snapshot = "FuseSource Community Snapshot Repository" at "http://repo.fusesource.com/nexus/content/groups/public-snapshots"
  val servicemix_m2 = "ServiceMix M2 Repository" at "http://svn.apache.org/repos/asf/servicemix/m2-repo"
  val java_net_m2 = "java.net Maven 2 Repo" at "http://download.java.net/maven/2"
  val openqa_releases = "OpenQA Releases" at "http://archiva.openqa.org/repository/releases"
  val glassfish_repo_archive = "Nexus repository collection for Glassfish" at "http://maven.glassfish.org/content/groups/glassfish"
  val snapshots_scala_tools_org = "Scala-Tools Maven2 Snapshot Repository" at "http://scala-tools.org/repo-snapshots"
  val apache_snapshots = "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots"
  val fluido_skin = "Fluido" at "http://fluido-skin.googlecode.com/svn/repo/"
  val zt_repo = "Zero turnaround repo" at "http://repos.zeroturnaround.com/maven2"
}

object Scalate extends Build{
  import BuildSettings._
  import Repositories._
  
  val allRepos = Seq(fusesource_nexus_staging, fusesource_m2, fusesource_m2_snapshot, fusesource_nexus_m2_snapshot,
    servicemix_m2, java_net_m2, openqa_releases, glassfish_repo_archive, snapshots_scala_tools_org, apache_snapshots,
    fluido_skin, zt_repo)
  
  lazy val scalate_util = Project("scalate-util", file("scalate-util"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.slf4j" % "slf4j-api" % slf4jVersion,
      "junit" % "junit" % junitVersion % "test"),
      
      libraryDependencies <<= (scalaVersion, libraryDependencies) { (sv, deps) =>
      	if(sv == "2.8.1") deps :+ "org.scalatest" %% "scalatest" % "1.5" % "test"
      	else if(sv == "2.9.0-1") deps :+ "org.scalatest" % "scalatest_2.9.0" % "1.4.1" % "test"
      	else deps :+ "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
      }
      
    ))
    
  lazy val scalate_core = Project("scalate-core", file("scalate-core"),
  settings = buildSettings ++ Seq( libraryDependencies := Seq(
    "javax.servlet" % "servlet-api" % servletApiVersion % "compile",
    "com.sun.jersey" % "jersey-server" % jerseyVersion % "compile",
    "org.fusesource.scalamd" % "scalamd" % scalamdVersion % "compile",
    "ch.qos.logback" % "logback-classic" % logbackVersion % "runtime",
    "org.osgi" % "org.osgi.core" % osgiVersion % "compile",
    "junit" % "junit" % junitVersion % "test"),
    
    libraryDependencies <<= (scalaVersion, libraryDependencies) { (sv, deps) =>
    	if(sv == "2.8.1") deps :+ "org.scala-lang" % "scala-compiler" % "2.8.1" % "compile"
    	else if(sv == "2.9.0") deps :+ "org.scala-lang" % "scala-compiler" % "2.9.0" % "compile"
    	else if(sv == "2.9.0-1") deps :+ "org.scala-lang" % "scala-compiler" % "2.9.0-1" % "compile"
    	else Seq[ModuleID]()
    },
    
    resolvers := Seq(java_net_m2)
  )) dependsOn(scalate_util)
  
  lazy val scalate_test = Project("scalate-test", file("scalate-test"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "junit" % "junit" % junitVersion,
      "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.0a5",
      "org.eclipse.jetty" % "jetty-server" % jettyVersion,
      "org.eclipse.jetty" % "jetty-webapp" % jettyVersion,
      "org.eclipse.jetty" % "jetty-util" % jettyVersion
    ),
    libraryDependencies <<= (scalaVersion, libraryDependencies) { (sv, deps) =>
    	if(sv == "2.8.1") deps :+ "org.scalatest" %% "scalatest" % "1.5"
    	else if(sv == "2.9.0-1") deps :+ "org.scalatest" % "scalatest_2.9.0" % "1.4.1"
    	else deps :+ "org.scalatest" %% "scalatest" % scalaTestVersion
    }
    )) dependsOn(scalate_core)
    
  lazy val scalate_wikitext = Project("scalate-wikitext", file("scalate-wikitext"),
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.fusesource.wikitext" % "confluence-core" % wikitextVersion,
      "org.fusesource.wikitext" % "textile-core" % wikitextVersion
    ))) dependsOn(scalate_test)
  
  lazy val scalate_page = Project("scalate-page", file("scalate-page"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.yaml" % "snakeyaml" % "1.7"
    ),
      resolvers := Seq(fusesource_nexus_m2_snapshot)
    )) dependsOn(scalate_wikitext)
    
  lazy val scalate_camel = Project("scalate-camel", file("scalate-camel"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.apache.camel" % "camel-spring" % camelVersion,
      "org.apache.camel" % "camel-scala" % camelVersion
    ),
      resolvers := Seq(fusesource_m2)
    )) dependsOn(scalate_wikitext)

  lazy val scalate_web = Project("scalate-web", file("scalate-web"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "javax.servlet" % "servlet-api" % servletApiVersion % "provided"
    ))) dependsOn(scalate_page)
    
  lazy val scalate_jsp_converter = Project("scalate-jsp-converter", file("scalate-jsp-converter"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.apache.karaf.shell" % "org.apache.karaf.shell.console" % karafVersion
    ),
      resolvers := allRepos
    )) dependsOn(scalate_web)
    
  lazy val scalate_war = Project("scalate-war", file("scalate-war"), settings = buildSettings) dependsOn(scalate_test)
    
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
    
  lazy val scalate_website = Project("scalate-website", file("scalate-website"), settings = buildSettings) dependsOn(scalate_test)

  lazy val scalate_markdownj = Project("scalate-markdownj", file("scalate-markdownj"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.markdownj" % "markdownj" % markdownVersion
    ))) dependsOn(scalate_test)

  lazy val scalate_bookstore = Project("scalate-bookstore", file("samples/scalate-bookstore"), 
    settings = buildSettings ++ Seq( libraryDependencies := Seq(
      "org.markdownj" % "markdownj" % markdownVersion
    ))) dependsOn(scalate_war, scalate_guice)
    
  lazy val scalate_example = Project("scalate-example", file("samples/scalate-example"), settings = buildSettings) dependsOn(scalate_war)
    
  lazy val scalate_sample = Project("scalate-sample", file("samples/scalate-sample"), settings = buildSettings) dependsOn(scalate_war, scalate_markdownj)
    
  lazy val scalate_sample_precompile = Project("scalate-sample-precompile", file("samples/scalate-sample-precompile"), settings = buildSettings) dependsOn(scalate_war)

  lazy val scalate_sample_scuery = Project("scalate-sample-scuery", file("samples/scalate-sample-scuery"), settings = buildSettings) dependsOn(scalate_war, scalate_guice)
  
  lazy val scalate_sample_sitegen = Project("scalate-sample-sitegen", file("samples/scalate-sample-sitegen"), settings = buildSettings) dependsOn(scalate_war)
        
  lazy val scalate_sample_spring_mvc = Project("scalate-sample-spring-mvc", file("samples/scalate-sample-spring-mvc"), settings = buildSettings) dependsOn(scalate_war, scalate_spring_mvc)
  
  lazy val scalate_distro = Project("scalate-distro", file("scalate-distro"), settings = buildSettings) aggregate(scalate_util, scalate_core, scalate_test,
      scalate_wikitext, scalate_page, scalate_camel, scalate_web, scalate_jsp_converter, scalate_war, scalate_jrebel, scalate_markdownj,
      scalate_jruby, scalate_tool, scalate_website, scalate_bookstore, scalate_guice, scalate_spring_mvc, scalate_example,
      scalate_sample, scalate_sample_precompile, scalate_sample_scuery, scalate_sample_sitegen, scalate_sample_spring_mvc)
}