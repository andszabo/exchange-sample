# Simple exchange
A simple exchange web application, which is more a technology sample than a real life application.

Users can place buy and sell orders in an arbitrary instrument. Orders are matched and their status along
with any trades created are updated in the users' browser real-time.

The matching rules are very simple:
  * An order is matched against another order of the opposite direction if they have the same
    instrument and quantity and the buy price is greater or equal to the sell price.
  * If there are multiple matching orders for a buy order it is matched against the sell with the lowest price.
  * If there are multiple matching orders for a sell order it is matched against the buy with the highest price.
  * If there are multiple matching orders at the same price for a newly added order it is matched
    against the order which was created the earliest.
  * When two orders match a trade is created from them. The execution price is the price of the newly
    added order that triggered the match.
  * When a new order cannot be matched against existing orders it is placed in the order book waiting for a
    matching counterpart.

## Modules
  * exchange-api - the messages types accepted and sent by the application.
  * exchange-domain - the business logic for order matching and trade creation
  * exchange-akka - actors for asynchronous, distributed processing of requests
  * exchange-boot - a Spring Boot web application that provides the web UI
  
## Technologies
The application uses **Akka** with **event sourcing**, **cluster sharding** and **distributed publish-subscribe** for
scalability and fault tolerance. Events and snapshots are stored in **MongoDB**.
Spring is used for the web API including **Websocket** communication with **STOMP** and **Spring Security**.
The UI uses **Thymeleaf**, **Bootstrap** and **AngularJS**.

## Running on a single node
  1. Start mongodb on the default port
  2. Start the application with `./gradlew bootRun`
  3. Login at `localhost:8080`

## Running in a cluster
The exchange-boot module contains sample configuration to run a cluster of docker containers on a single host.
  1. Build the application docker image: `./gradlew buildDocker`
  2. Get a container for mongodb: `docker pull mongo:latest`
  3. Create & start a mongodb container: `docker run --name mongo-exchange -d mongo`
  4. Create & start the first seed node: `exchange-boot/docker-seed1.sh`
  5. Create & start the second seed node: `exchange-boot/docker-seed2.sh`
  6. Create & start additional nodes: `exchange-boot/docker-node.sh <NAME> <HTTP_PORT>`
The seed1 and seed2 nodes will listen on port 8081 and 8082 of the host respectively. Additional nodes are
attached to the specified host port.
