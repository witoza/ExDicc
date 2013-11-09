name := "ExDicc"

version := "1.0"

scalaVersion := "2.10.2"

libraryDependencies +=
  "log4j" % "log4j" % "1.2.17" excludeAll(
    ExclusionRule(organization = "com.sun.jdmk"),
    ExclusionRule(organization = "com.sun.jmx"),
    ExclusionRule(organization = "javax.jms")
  )
  
libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.3-M1"
  
libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.9"

libraryDependencies += "org.jsoup" % "jsoup" % "1.7.2"

libraryDependencies += "junit" % "junit" % "4.11" % "test"

libraryDependencies += "net.sourceforge.htmlunit" % "htmlunit" % "2.13"

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

EclipseKeys.withSource := true