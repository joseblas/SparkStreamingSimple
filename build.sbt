name := "ssimple"

version := "1.0"

scalaVersion := "2.10.6"

val sparkVersion = "1.6.3"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % sparkVersion  % "provided",
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.7.0" classifier "models",
  "joseblas" %% "spark-corenlp" % "0.0.1-SNAPSHOT"
)