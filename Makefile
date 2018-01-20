ROOT=$(shell pwd)
WAR=tcf-converter\#1.0.0.war

war:
	mvn package

clean:
	mvn clean
	
deploy:
	scp -P 22022 target/$(WAR) grid.anc.org:/tmp
	ssh -p 22022 grid.anc.org "mv /tmp/$(WAR) /usr/share/tomcat/server-1/webapps"

all: clean war deploy

run:
	docker run -d -p 8000:8080 --name tomcat -v $(ROOT)/target:/var/lib/tomcat7/webapps lappsgrid/tomcat7:1.2.3

login:
	docker exec -it tomcat /bin/bash

stop:
	docker rm -f tomcat

test:
	@echo "Dir is $(ROOT)"

