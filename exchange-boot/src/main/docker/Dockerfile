FROM java:8
VOLUME /tmp
EXPOSE 8080
EXPOSE 2551
ADD exchange-boot exchange-boot
ENTRYPOINT ["exchange-boot/bin/exchange-boot","-Djava.security.egd=file:/dev/./urandom"]