#!/bin/bash

if [[ -z "$1" ]]
then
  echo Usage $0 "xxx.clj ..."
  exit 0
fi

# Setup the classpath with all dependency jar files

CLASSPATH=./src:./test
for a in `find ./lib -name "*.jar"`
do
  CLASSPATH=$CLASSPATH:$a
done

# Find the right clojure main

clj=$1; shift

if [[ "$clj" = "-cp" ]]
then
  echo $CLASSPATH
  exit 0
fi

cljmain=`find . -name "*.clj" -print | grep "${clj%.clj}.clj"`

if [[ -z "$cljmain" ]]; then
  echo "$clj.clj is not found anywhere..."
  exit 1
fi

echo java -cp $CLASSPATH jline.ConsoleRunner clojure.main $cljmain $*
java -cp $CLASSPATH jline.ConsoleRunner clojure.main $cljmain $*
