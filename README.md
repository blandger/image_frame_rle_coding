### Experiments for one startup

Console application is used for gif image frame changes encoding by rle. 
Program read frames from included GIF file (see src/main/resources/source_gif_image.gif) and writes 'different' frames into /output/ dir as 0.gif, 1.gif... files.

## JDK 17 is needed

As a quick way you can install [SDKMAN](https://sdkman.io/install) locally first and use it for JDK installation and management later.

### Show available JDK list (after SDKMAN is installed)
> sdk list java

### Install JDK from list (e.g. 17.0.7-graalce OR  17.0.7-tem)
> sdk install java 17.0.7-graalce

> sdk install java 17.0.7-tem

Check
> java --version

`openjdk 17.0.5 2022-10-18`

### Build

#### Linux

> ./mvnw clean package

#### Windows

> mvnw.cmd clean package

### Run

> java -jar target/DemoApplication-0.0.1-SNAPSHOT-jar-with-dependencies.jar

### CI/CD with Auto DevOps

This template is compatible with [Auto DevOps](https://docs.gitlab.com/ee/topics/autodevops/).

If Auto DevOps is not already enabled for this project, you can [turn it on](https://docs.gitlab.com/ee/topics/autodevops/#enabling-auto-devops) in the project settings.