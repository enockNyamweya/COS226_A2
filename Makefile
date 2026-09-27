JAVAC = javac
JAVA = java
JFLAGS = -g

SOURCES = $(wildcard *.java)
CLASSES = $(SOURCES:.java=.class)

all: $(CLASSES)

%.class: %.java
	$(JAVAC) $(JFLAGS) $<

run: all
	$(JAVA) Main

clean:
	rm -f *.class
