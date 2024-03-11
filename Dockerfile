FROM debian:bookworm
MAINTAINER jmedina

RUN apt update
RUN apt install aptitude -y
RUN aptitude install wget lsb-release gnupg net-tools debian-keyring -y
RUN gpg --keyserver keyserver.ubuntu.com --recv-keys B7B3B788A8D3785C
RUN gpg --armor --export B7B3B788A8D3785C | apt-key add -
RUN aptitude update

WORKDIR /root
RUN mkdir -p /root/installation

WORKDIR /root/installation

RUN wget https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.deb
RUN dpkg -i ./jdk-17_linux-x64_bin.deb

RUN wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
RUN tar zxf apache-maven-3.9.6-bin.tar.gz

RUN wget https://dev.mysql.com/get/mysql-apt-config_0.8.22-1_all.deb
RUN DEBIAN_FRONTEND=noninteractive dpkg -i ./mysql-apt-config_0.8.22-1_all.deb
RUN aptitude update
RUN aptitude install -y mysql-server

RUN wget https://downloads.apache.org/kafka/3.7.0/kafka_2.13-3.7.0.tgz
RUN tar zxf kafka_2.13-3.7.0.tgz

RUN wget https://github.com/wildfly/wildfly/releases/download/26.1.3.Final/wildfly-preview-26.1.3.Final.tar.gz 
RUN tar zxf wildfly-preview-26.1.3.Final.tar.gz
RUN /root/installation/wildfly-preview-26.1.3.Final/bin/add-user.sh -u 'wildfly' -p 'wi1df1y!' 

SHELL ["/bin/bash", "--login", "-c"]
RUN wget -qO- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
RUN nvm install 18.16.0

RUN aptitude install curl -y
RUN curl -fsSL https://pgp.mongodb.com/server-7.0.asc | gpg  --dearmor -o /etc/apt/trusted.gpg.d/mongodb-server-7.0.gpg
RUN echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-7.0.list
RUN aptitude update
RUN aptitude install mongodb-org -y

RUN aptitude install less -y

RUN mkdir -p /var/log/jtetris

ENV JAVA_HOME /usr/lib/jvm/jdk-17-oracle-x64
RUN export JAVA_HOME

ENV JAVA_OPTS="-Xms128M -Xmx2G -XX:MaxMetaspaceSize=1G"
RUN export JAVA_OPTS

ADD node-server /root/installation/node-server

COPY create-user.sql /root/installation/create-user.sql

COPY run.sh /root/installation/run.sh
ENTRYPOINT ["/root/installation/run.sh"]

#ENTRYPOINT ["tail", "-f", "/dev/null"]
#ENTRYPOINT ["mysqld", "--user=root"]
#ENTRYPOINT ["./wildfly-preview-26.1.3.Final/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
