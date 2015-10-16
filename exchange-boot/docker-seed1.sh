#!/bin/sh

docker run -p 8081:8080 --link mongo-exchange:mongodb --name seed1 -e SEED=true -d com.github.andszabo/exchange-boot:1.0-SNAPSHOT
docker logs -f seed1

