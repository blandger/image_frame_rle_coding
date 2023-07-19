### Experiments for one startup

Code for gif image frame changes encoding by rle. Program reads frames from included GIF file (see src/main/resources/source_gif_image.gif) and writes 'different' frames into /output/ dir as 0.gif files.

## JDK 17 is needed

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