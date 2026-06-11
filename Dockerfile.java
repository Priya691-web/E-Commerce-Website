# Runtime stage with Tomcat for prebuilt WAR deployment
FROM tomcat:10.1-jdk21

# Set working directory
WORKDIR /usr/local/tomcat

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set environment variables
ENV CATALINA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/ROOT \
           /usr/local/tomcat/webapps/docs \
           /usr/local/tomcat/webapps/examples \
           /usr/local/tomcat/webapps/host-manager \
           /usr/local/tomcat/webapps/manager

# Copy prebuilt WAR file from host target directory
COPY target/FashionStore-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Health check
HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/home || exit 1

# Start Tomcat
CMD ["catalina.sh", "run"]
