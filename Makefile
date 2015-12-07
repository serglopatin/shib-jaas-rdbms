JAVAC=/usr/bin/javac

all: jar

prepare:
	mkdir -p classes

build: prepare src/com/shibjaasrdbms/*
	$(JAVAC) src/com/shibjaasrdbms/*.java
	
install: build
	mkdir -p classes/com/shibjaasrdbms
	cp src/com/shibjaasrdbms/*.class classes/com/shibjaasrdbms/

jar: install
	cd classes && jar cvf ../shibjaasrdbms.jar com/* && cd -

clean:
	rm -rf classes
	rm -f src/com/shibjaasrdbms/*.class
	rm -f shibjaasrdbms.jar
