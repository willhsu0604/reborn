name := "reborn"

version := "1.0"

lazy val `reborn` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

resolvers += "cloudera" at "https://repository.cloudera.com/artifactory/cloudera-repos"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "org.junit.jupiter" % "junit-jupiter-engine" % "5.0.0-M3",
  "org.junit.platform" % "junit-platform-console" % "1.0.0-M3",
  "org.opentest4j" % "opentest4j" % "1.0.0-M1",
  "org.slf4j" % "slf4j-simple" % "1.7.21",
  "org.apache.logging.log4j" % "log4j-api" % "2.6.2",
  "org.apache.logging.log4j" % "log4j-to-slf4j" % "2.6.2",
  "org.eclipse.jetty" % "jetty-server" % "9.4.2.v20170220",
  "org.eclipse.jetty" % "jetty-servlet" % "9.4.2.v20170220",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.6.3" force(),
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.3" force(),
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.6.3" force(),
  "org.reflections" % "reflections" % "0.9.10",
  "org.apache.httpcomponents" % "httpclient" % "4.5.3"
)

fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)

updateOptions := updateOptions.value.withCachedResolution(true)

test in assembly := {}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case x if x.startsWith("META-INF") && x.endsWith(".SF") => MergeStrategy.discard
  case x if x.startsWith("META-INF") && x.endsWith(".SF") => MergeStrategy.discard
  case x if x.startsWith("META-INF") && x.endsWith(".RSA") => MergeStrategy.discard
  case x if x.startsWith("META-INF") && x.endsWith(".DSA") => MergeStrategy.discard
  case x if x.startsWith("META-INF") && x.endsWith(".TXT") => MergeStrategy.discard
  case x if x.startsWith("META-INF") && x.endsWith("org.apache.hadoop.fs.FileSystem") => MergeStrategy.concat
  case PathList(ps@_*) if ps.last endsWith ".conf" => MergeStrategy.concat
  case x => MergeStrategy.first
}