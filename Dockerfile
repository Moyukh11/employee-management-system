FROM tomcat:10.1-jdk17-temurin

# Remove default Tomcat application
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Render expects the web service to listen on port 10000
RUN sed -i 's/port="8080"/port="10000"/' /usr/local/tomcat/conf/server.xml

# Deploy EMS under /EMS
COPY build/ /usr/local/tomcat/webapps/EMS/

EXPOSE 10000

CMD ["catalina.sh", "run"]
