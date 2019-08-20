.PHONY: all clean doc compile

LIB_JARS=`find -L lib/ -name "*.jar" | tr [:space:] :`

compile:
	mkdir -p bin
	javac -sourcepath src -classpath $(LIB_JARS) -d bin `find -L -name "*.java"` -Xlint:unchecked

doc:
	mkdir -p doc
	javadoc -sourcepath src -classpath $(LIB_JARS) -d doc peersim.chord

run:
	java -Xmx1g -cp $(LIB_JARS):bin peersim.Simulator ./config/A1B1C1D1E1F1G1H1I1.cfg

all: compile doc run

clean:
	rm -fr bin doc
