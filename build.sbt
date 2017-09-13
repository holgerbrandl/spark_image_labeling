name := "component_labeling"

version := "0.1"

scalaVersion := "2.11.11"


libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.2.0",
  "org.apache.spark" %% "spark-sql" % "2.2.0",
  "org.apache.spark" %% "spark-graphx" % "2.2.0",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)



// http://mvnrepository.com/artifact/net.imglib2/imglib2-ij/2.0.0-beta6
//resolvers += "Sonatype OSS Snapshots" at "http://maven.imagej.net/content/repositories/releases/"
resolvers += "Sonatype OSS Snapshots" at "http://maven.imagej.net/"
resolvers += "imglib resleases" at "http://maven.imagej.net/content/repositories/releases/"

// needed for udunits  http://mvnrepository.com/artifact/edu.ucar/udunits/4.3.23
//resolvers += "Boundless OSS Snapshots" at "http://repo.boundlessgeo.com/main/"
resolvers += "Boundless OSS Snapshots" at "https://artifacts.unidata.ucar.edu/content/groups/public/"

libraryDependencies += "net.imglib2" % "imglib2" % "4.3.0"

libraryDependencies += "edu.ucar" % "udunits" % "4.3.18"

libraryDependencies += "net.imglib2" % "imglib2-algorithm" % "0.8.1" // contains connected component impl

//http://mvnrepository.com/artifact/net.imglib2/imglib2-ij/2.0.0-beta6
libraryDependencies += "net.imglib2" % "imglib2-ij" % "2.0.0-beta-38"

// not a compile but a runtime dependency to run ImageJFunctions.show(img)
//libraryDependencies += "net.imglib2" % "imglib2-meta" % "2.0.0-beta-38"
libraryDependencies += "net.imglib2" % "imglib2-io" % "2.0.0-beta-21"


// google-add caliper

//libraryDependencies += "com.google.caliper" % "caliper" % "1.0-beta-1"
libraryDependencies += "org.openjdk.jmh" % "jmh-core" % "1.19"
libraryDependencies += "org.openjdk.jmh" % "jmh-generator-annprocess" % "1.19"


// http://www.scala-sbt.org/0.12.3/docs/Detailed-Topics/Java-Sources.html

compileOrder in Test := CompileOrder.Mixed
