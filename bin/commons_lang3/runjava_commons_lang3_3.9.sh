#!/bin/bash
#under msp everything should be identical on all computers
MACHINE_SPECIFIC_PATH="$HOME/Graduation_Studies/ThesisWork/HelperMethodAnalysis"

PACKAGE="org.apache.commons.lang3"
SOOT_JAR="$MACHINE_SPECIFIC_PATH/soot_jar/sootclasses-trunk-jar-with-dependencies.jar"
JAVA_PATH="$MACHINE_SPECIFIC_PATH/target/classes"
CC_CLASS="ca.uwaterloo.liang.Main"
BENCHMARK_PATH="$MACHINE_SPECIFIC_PATH/Benchmarks/commons-lang3-3.9-src"
TARGET_PATH="target/classes"
TARGET_TEST_PATH="target/test-classes"
BENCHMARK="commons_lang3_3.9"
DRIVER_PATH="org.apache.commons.lang3.Driver"
OUTPUT_PATH="$MACHINE_SPECIFIC_PATH/analysis_output"

MVN_DEPENDENCY_PATH="$BENCHMARK_PATH/mvn_dependencies"

# add all the jar files needed for set_soot_classpath
for jar in $MVN_DEPENDENCY_PATH/*; do
  JAR_PATH=$JAR_PATH:$jar
done
echo $JAR_PATH

cd $BENCHMARK_PATH

# touch is_maven in the benchmark directory to indicate that a benchmark is mvn
if [ -a is_maven ]; then
  echo "it is a maven project"
  rm -rf is_maven
  mvn clean test
fi
# cd $SUB_PATH

java -cp $SOOT_JAR:$JAVA_PATH $CC_CLASS $DRIVER_PATH $BENCHMARK_PATH/$TARGET_PATH $BENCHMARK_PATH/$TARGET_TEST_PATH $jars$TARGET_PATH:$TARGET_TEST_PATH:$JAR_PATH $BENCHMARK $OUTPUT_PATH
rm -rf "sootOutput/"
touch is_maven

# $BENCHMARK_PATH/$TARGET_TEST_PATH
