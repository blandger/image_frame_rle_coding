### Experiments for one startup

Console application is used for gif image frame changes encoding by RLE (Run Length Encoding and Decoding). 
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

### Code workflow for Run Length Encoding and Decoding

Here is RLE encoding is used only for testing purpose.

1. Find DIFF image/data between two frames

| Source image frame (N)                |             Operation             | Next image frame (N + 1)              |        Result        |
|:--------------------------------------|:---------------------------------:|:--------------------------------------|:--------------------:|
| ![13_source.gif](doc%2F13_source.gif) | compute DIFFERENCE between frames | ![14_source.gif](doc%2F14_source.gif) | Plain diff int Array |

2. Encode DIFF array by RLE. Then RLE data can be passed for later displaying (somewhere).

Frame diff int Array ==> RLE encoding ==> RLE int Array

3. Decode RLE DIFF into plain int array (at displaying end)

RLE int Array ==> plain int diff array 

4.  Plain diff array is applied to Source Image (only changed image part is transferred)

| Diff Image |                        Operation                        | Next image frame (N + 1) |                 Result                  |
|:--------------------------------------|:-------------------------------------------------------:|:--------------------------------------|:---------------------------------------:|
| ![13_diff.gif](doc%2F13_diff.gif) | RLE int DIFF Array is applied to Source image frame (N) | ![14_rle_composed.gif](doc%2F14_rle_composed.gif) | Compute/Create Next image frame (N + 1) |

