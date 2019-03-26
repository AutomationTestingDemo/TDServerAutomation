#!/bin/sh
#Java 8
FROM isl-dsdc.ca.com:5001/ca-standard-images/centos7-jre8:latest

USER root

MAINTAINER tds-mra

WORKDIR /opt/arcot

COPY *.jar .

COPY start.sh .

RUN mkdir conf && chown -R 1010:1010 /opt/arcot && chmod -R 755 /opt/arcot

USER 1010

ENTRYPOINT ["./start.sh"]





