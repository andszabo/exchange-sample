FROM java:8
VOLUME /tmp
EXPOSE 8080
EXPOSE 2551
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"
ENV EXCHANGE_BOOT_OPTS="-Dakka.contrib.persistence.mongodb.mongo.urls.0=mongodb:27017"
ADD exchange-boot exchange-boot
ENTRYPOINT ["exchange-boot/bin/exchange-boot"]