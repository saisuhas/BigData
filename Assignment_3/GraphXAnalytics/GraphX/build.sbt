name := "GraphX"
version := "0.1"

scalaVersion := "2.11.12"
val sparkVersion = "2.4.0"


lazy val root = (project in file(".")).
  settings(
    name := "GraphX",
    version := "1.0",
    scalaVersion := "2.12.8",
    mainClass in Compile := Some("GraphX")
  )

resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)
 resolvers += "bintray-spark-packages" at
  "https://dl.bintray.com/spark-packages/maven/"

resolvers += "Typesafe Simple Repository" at
  "http://repo.typesafe.com/typesafe/simple/maven-releases/"

resolvers += "MavenRepository" at
  "https://mvnrepository.com/"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-hive" % sparkVersion,
  "graphframes" % "graphframes" % "0.6.0-spark2.2-s_2.11")



libraryDependencies += "org.apache.bahir" %% "spark-streaming-twitter" % "2.3.2"


assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}