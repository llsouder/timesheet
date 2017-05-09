FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/timesheet.jar /timesheet/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/timesheet/app.jar"]
