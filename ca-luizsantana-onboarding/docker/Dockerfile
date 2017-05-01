FROM ubuntu:14.04

# Install Java 8
RUN apt-get update
RUN apt-get install software-properties-common -y
RUN add-apt-repository ppa:webupd8team/java -y
RUN apt-get update
RUN echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
RUN apt-get install oracle-java8-installer -y --force-yes
RUN apt-get install oracle-java8-set-default
RUN apt-get install curl -y --force-yes
RUN apt-get install sudo -y --force-yes
RUN apt-get install nano -y --force-yes
RUN apt-get install libxext6 -y --force-yes
RUN export TERM=xterm

#Install aws cli
RUN apt-get install awscli -y

#Install Understand
COPY ./Understand-4.0.882-Linux-64bit.tgz /

#Unzip Understand
RUN tar -xvzf /Understand-4.0.882-Linux-64bit.tgz
WORKDIR /scitools/bin/linux64
RUN export PATH=$PATH:/scitools/bin/linux64

# Configure  env
RUN mkdir app
RUN mkdir /repositories
RUN mkdir /output
RUN chmod 777 /repositories
RUN chmod 777 /output

COPY ./deadcode-service-0.1.0.jar /app/api.jar

EXPOSE 80 8080 8090

ENTRYPOINT [ "sh", "-c", "java -jar /app/api.jar" ]