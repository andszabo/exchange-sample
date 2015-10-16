#!/bin/sh

seed1Address=`docker inspect --format '{{ .NetworkSettings.IPAddress }}' seed1`
seed2Address=`docker inspect --format '{{ .NetworkSettings.IPAddress }}' seed2`
seedNodes="$seed1Address:2551,$seed2Address:2551"

docker run -p $2:8080 --link mongo-exchange:mongodb --name $1 -e SEED_NODES=$seedNodes -d com.github.andszabo/exchange-boot:1.0-SNAPSHOT
docker logs -f $1
