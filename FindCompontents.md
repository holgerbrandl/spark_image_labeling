

Array DBs
* http://www.alphadevx.com/a/36-Comparison-of-Relational-and-Multi-Dimensional-Database-Structures
* https://en.wikipedia.org/wiki/Array_DBMS

# Spark Graph API pointers


https://spark.apache.org/docs/latest/graphx-programming-guide.html#connected-components




# ImgLib2 API pointers

[Image Open & Display](https://imagej.net/ImgLib2_-_Getting_Started)

[Accessors & Cursors](https://imagej.net/ImgLib2_-_Accessors#Cursor)


```
ImagePlusImg image = ImagePlusAdapter.wrap(img);
Img<UnsignedByteType> image = ImageJFunctions.wrap(img);
```

try ops http://mvnrepository.com/artifact/net.imagej/imagej-ops/0.38.0

# Benchmarking Notes

Example image after enhance contrasts
![](.FindCompontents_images/1332418f.png)


Libs
* https://scalameter.github.io/
* jmh http://www.baeldung.com/java-microbenchmark-harness


@State Model
> When multiple {@link Param}-s are needed for the benchmark run,
 JMH will compute the outer product of all the parameters in the run.

 run with json as outformat
 ```
 com.github.holgerbrandl.spark.misc.ExampleBenchmark.init  -rf json -rff results.csv
 ```

 csv provided error is 99.9 CI

if execution plan is injected into benchmark method --> traverse state outer product

Run with
```bash
cd /Users/brandl/projects/spark/component_labeling

#sbt package
#appJar=/Users/brandl/projects/spark/component_labeling/target/scala-2.11/component_labeling_2.11-0.1.jar
#ll $appJar
#java -jar "target/scala-2.11/component_labeling_2.11-0.1.jar"
# -> won't work without a fat jar

## use sbt plugin
https://github.com/ktoso/sbt-jmh
# Write your benchmarks in `src/main/scala`. They will be picked up and instrumented by the plugin.

sbt jmh:run * ## works if no "extends app" are present in cde

# run an individual class
sbt jmh:run com.github.holgerbrandl.spark.components.LocalSparkComponentsBM

# or followin params from sbt-jmh docs
sbt jmh:run -i 3 -wi 3 -f1 -t1 .*FalseSharing.*
```

# Future iteration

rebuild using gradle, see https://docs.gradle.org/current/userguide/scala_plugin.html and do profiling with https://github.com/melix/jmh-gradle-plugin which also supports better combined fatjar packaging and thus allow for `java -jar build/libs/benchmarking-experiments-0.1.0-all.jar` which seems to accept args because of https://github.com/danielmitterdorfer/benchmarking-experiments/blob/master/pom.xml#L65


![](.FindCompontents_images/97611800.png)
from  http://daniel.mitterdorfer.name/img/jmh-workflow.png which also uses `gradle shadow` --> sources: https://github.com/danielmitterdorfer/benchmarking-experiments without using jmh-gradle!!

or use mvn archetype

```
## jar building: follow advice from http://openjdk.java.net/projects/code-tools/jmh/
cd ~/Desktop/
mvn archetype:generate \
          -DinteractiveMode=false \
          -DarchetypeGroupId=org.openjdk.jmh \
          -DarchetypeArtifactId=jmh-scala-benchmark-archetype \
          -DgroupId=org.sample \
          -DartifactId=test \
          -Dversion=1.0
```


# Next steps

* fix imglib generics by converting Interval back into byte/int image



