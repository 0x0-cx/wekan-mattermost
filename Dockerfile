FROM openjdk:11

COPY target/wekan-mattermost-1.0.0-SNAPSHOT-standalone.jar /

# ENV PORT

# ENV URL

CMD ["/usr/local/openjdk-11/bin/java", "-jar", "/wekan-mattermost-1.0.0-SNAPSHOT-standalone.jar"]