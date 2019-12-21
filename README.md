# p4rez
*Samir Bensadoun & Hugo Krawczyk*

## Build & Run *(from current directory)*
### Serveur
*daemon.c use MSG_SYN (ubuntu, default) or MSG_SEND (macOS)*

`cd ./p4rezServer`

`make`

`./p4rezServer`

### Client
*JavaFX require to download [Java SE Runtime Environment 8](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) first*

`cd ./p4rezClient`

`PATH_TO_JDK1.8/Contents/Home/bin/java -jar p4rezClient.jar`
