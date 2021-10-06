# Masters of Renaissance

Project of Software Engineering, Polytechnic University of Milan, A.Y. 2020/2021. Grade: 30 with Honors.

Digital version of the board game *Masters of Renaissance*, implemented in Java.

## Team AM49 - Prof. Margara

- Damiano Amatruda ([@damianoamatruda](https://github.com/damianoamatruda)) <br> damiano.amatruda@mail.polimi.it
- Alessandro Atanassov ([@alex-atanassov](https://github.com/alex-atanassov)) <br> alessandro.atanassov@mail.polimi.it
- Marco Ciarrocca ([@marcociarrocca](https://github.com/marcociarrocca)) <br> marco.ciarrocca@mail.polimi.it

## Project Requirements

The project consists in developing a digital version of the board game *Masters of Renaissance (Maestri del
Rinascimento)*, using the Java programming language.

The game needs to be implemented as a distributed system, with a single server and multiple clients that connect to it.

The system must be based on the MVC architecture.

## Implemented features

| Feature | Implemented |
| ------- | ----------- |
| Complete game rules | :heavy_check_mark: 1+ players supported (see [configuration file](#configuration-file)) |
| CLI-based client | :heavy_check_mark: |
| GUI-based client | :heavy_check_mark: |
| Communication protocol ("Socket") | :heavy_check_mark: |
| Advanced functionality 1 | :heavy_check_mark: Multiple games ("Partite multiple") |
| Advanced functionality 2 | :heavy_check_mark: Local game ("Partita locale") |
| Advanced functionality 3 | :heavy_check_mark: Resilience to disconnections ("Resilienza alle disconnessioni", see below) |

The communication-side architecture of this project is built to support machine-local games and remote games, doing this
by choosing the right [Network](#src/main/java/it/polimi/ingsw/common/Network.java), either instantiating a local
backend or connecting the client to the remote server instead.  
Both the client's view model and the backend's model are therefore recreated at every connection, including the local
games'. This implies that there is no persistence of data when playing locally with the current implementation, as the
local backend gets recreated too.

## Compiling

To run the tests and compile the software:

1. Install [Java SE 16](https://docs.oracle.com/en/java/javase/16/) (earlier versions will not work)
2. Install [Maven](https://maven.apache.org/install.html)
3. Clone this repo
4. In the cloned repo folder, run: `mvn package -DskipTests`
5. The compiled artifacts (`-server` and `-client` jar files) will be inside the `/target` folder.

## Running the server

Run the `-server` jar file in the `/target` folder.

Syntax: `java -jar AM49-1.0-SNAPSHOT-server.jar [options]`

**Supported options:**

```bash
  --port    network listening port (default 1234)
--config    path of the custom configuration file
```

### Public instance

A public instance of the server is available at 51.15.199.149:51015.

### Docker image

A [`Dockerfile`](Dockerfile) and a [`docker-compose.yml`](docker-compose.yaml) are available to build and deploy a
docker container running the server.

To build the container:

1. Place the server's jar in the same folder as the Dockerfile
2. Rename the jar file to `server.jar`
3. Run `docker build -t ingsw-server .`

To deploy the container, run `docker-compose up -d` in the same folder as the `docker-compose.yaml` file.

## Running the client

**(Windows)** Since the CLI uses colored text, the default Windows terminal is not supported. Using WSL is therefore
necessary. Running the GUI works from any terminal.

Run the `-client` jar file in the `/target` folder.

Syntax: `java -jar AM49-1.0-SNAPSHOT-client.jar [options]`

**Supported options:**

```
--cli    run the client in text mode (if omitted the GUI will start instead)
```

## Configuration file

The default configuration file can be found in [`/src/main/resources`](src/main/resources/config/config.json).

This file contains all the necessary parameters and the game data needed for the server's Model to work.  
Since the Model is completely parameterized, all parameters are necessary and must be specified.

Custom configuration files can be specified from within the clients, by going to the *Options* menu. The custom
configuration files will work only for local games, as online games use the server's configuration files. To know more
about how the game data is synchronized when a client joins an online game, see
the [Communication protocol](deliverables/communication-protocol.md)'s specification.

### Disclaimer

As stated above, the configuration file contains **all** the game data.  
Since custom configurations were not a requirement for the project, this functionality has been implemented but not
fully tested in all possible configurations.

## License

This project is developed in collaboration with [Politecnico di Milano](https://www.polimi.it) and
[Cranio Creations](https://www.craniocreations.it).
