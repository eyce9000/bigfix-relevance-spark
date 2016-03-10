name := "bigfix-spark"
version := "1.1.0"
scalaVersion := "2.10.6"
scalacOptions += "-target:jvm-1.7"
javacOptions ++= Seq("-source", "1.7", "-target", "1.7")
sparkVersion := "1.5.2"


libraryDependencies ++= {
  Seq(
    "org.apache.spark"    %%  "spark-core"    %  sparkVersion.value % "provided",
    "org.apache.spark"    %%  "spark-sql"   %  sparkVersion.value % "provided" ,
    "org.slf4j" % "slf4j-api" % "1.7.10"
  )
}

libraryDependencies ++= {
  Seq(
    "org.eclipse.persistence" % "org.eclipse.persistence.moxy" % "2.5.0",
    "joda-time" % "joda-time" % "2.2"
  )
}

libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.2.0"
libraryDependencies += "junit" % "junit" % "4.8.1" % "test"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

assemblyMergeStrategy in assembly := {
case PathList("scala", xs @ _*) => MergeStrategy.discard
case x =>
  val oldStrategy = (assemblyMergeStrategy in assembly).value
  oldStrategy(x)
}

assemblyJarName in assembly := "bigfix-spark.jar"