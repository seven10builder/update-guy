FROM develar/java:8u45

MAINTAINER kmilner "kmilner@seven10storage.com" 

WORKDIR "/seven10/update-guy/"

COPY "release/server/update-guy-server.jar" "update-guy-server.jar"
COPY "src/main/resources/default-local-repo.json" "repo.json"
COPY "src/main/resources/log4j2.xml" "log4j2.xml"

EXPOSE 7519

# this volume is for the server's cache
VOLUME ["/seven10/update-guy/local"]

# this volume is for the local repo, if used
VOLUME ["/seven10/update-guy/repos"]

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-Dupdate-guy.repoFileName=repo.json", "-Dupdate-guy.localPath=/seven10/update-guy", "-jar", "update-guy-server.jar"]
