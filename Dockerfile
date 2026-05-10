FROM tomcat:10.1-jdk21

# Set working directory
WORKDIR /usr/local/tomcat

# Copy WAR file to webapps
COPY target/FashionStore.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
