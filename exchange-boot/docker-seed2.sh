#!/bin/sh

seed1Address=`docker inspect --format '{{ .NetworkSettings.IPAddress }}' seed1`
seed1Address="$seed1Address:2551"

docker run -p 8082:8080 --link mongo-exchange:mongodb --name seed2 -e SEED=true -e SEED_NODES=$seed1Address -d com.github.andszabo/exchange-boot:1.0-SNAPSHOT
docker logs -f seed2
