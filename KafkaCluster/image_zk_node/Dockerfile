FROM centos:centos8

RUN yum update -y && \
    yum install java-11-openjdk -y && \
    yum install wget -y && \
    wget https://archive.apache.org/dist/kafka/2.4.0/kafka_2.13-2.4.0.tgz -P /tmp && \
    cd /var && tar -xvzf /tmp/kafka_2.13-2.4.0.tgz && \
    rm /tmp/kafka_2.13-2.4.0.tgz && \
    ln -s kafka_2.13-2.4.0 kafka

RUN yum install nc -y && \
    yum install jq -y && \
    yum install python3 -y && \
    yum install openssh-server openssh-clients -y

RUN yum install krb5-workstation krb5-libs -y

RUN mkdir -p /tmp/zookeeper

COPY profile_kafka.sh /etc/profile.d

COPY krb5.conf /etc/krb5.conf 
COPY init.sh /
RUN chmod +x init.sh

ENTRYPOINT [ "/init.sh" ]