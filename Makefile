JAR=mock.jar
CLASSES=Main.class MockHTTPServer.class HTTPRequests.class

$(JAR) : $(CLASSES)
	jar cvfe $(JAR) Main $(CLASSES)

Main.class : Main.java
	javac Main.java

MockHTTPServer.class : MockHTTPServer.java
	javac MockHTTPServer.java

HTTPRequests.class : HTTPRequests.java
	javac HTTPRequests.java

clean :
	rm *.class $(JAR)
