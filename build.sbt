name := "ssimple"

version := "1.0"

scalaVersion := "2.10.6"

val sparkVersion = "1.6.3"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-streaming-kafka" % sparkVersion, // Not provided
  "edu.stanford.nlp" % "stanford-corenlp" % "3.7.0" classifier "models",
  "joseblas" %% "spark-corenlp" % "0.0.1-SNAPSHOT"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("META-INF", "DUMMY.DSA") => MergeStrategy.discard
  case PathList("META-INF", "DUMMY.SF") => MergeStrategy.discard
  case PathList("META-INF", "ECLIPSEF.SF") => MergeStrategy.discard
  case PathList("META-INF", "ECLIPSEF.RSA") => MergeStrategy.discard
  case "UnusedStubClass.class" => MergeStrategy.discard
  case _ => MergeStrategy.last
}