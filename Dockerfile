FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt update && apt install -y --no-install-recommends \
    openjdk-11-jdk \
    ssh \
    rsync \
    wget \
    pdsh \
    curl \
    nano \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
ENV HADOOP_HOME=/opt/hadoop
ENV PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin

RUN curl -O https://archive.apache.org/dist/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz && \
    tar -xzf hadoop-3.3.6.tar.gz && \
    mv hadoop-3.3.6 /opt/hadoop && \
    rm hadoop-3.3.6.tar.gz


# Download ZooKeeper
RUN cd /opt && \
    wget https://downloads.apache.org/zookeeper/zookeeper-3.8.6/apache-zookeeper-3.8.6-bin.tar.gz && \
    tar -xzvf apache-zookeeper-3.8.6-bin.tar.gz && \
    mv apache-zookeeper-3.8.6-bin /opt/zookeeper && \
    rm apache-zookeeper-3.8.6-bin.tar.gz

# Create data directory
RUN mkdir -p /opt/data/zookeeper && \
    chown -R root:root /opt/data/zookeeper && \
    chmod -R 755 /opt/data/zookeeper

 
# Create zoo.cfg automatically
RUN cp /opt/zookeeper/conf/zoo_sample.cfg /opt/zookeeper/conf/zoo.cfg && \
    echo "tickTime=2000" >> /opt/zookeeper/conf/zoo.cfg && \
    echo "dataDir=/opt/data/zookeeper" >> /opt/zookeeper/conf/zoo.cfg && \
    echo "clientPort=2181" >> /opt/zookeeper/conf/zoo.cfg && \
    echo "initLimit=5" >> /opt/zookeeper/conf/zoo.cfg && \
    echo "syncLimit=2" >> /opt/zookeeper/conf/zoo.cfg && \
    echo "server.1=mymaster01:2888:3888" >> /opt/zookeeper/conf/zoo.cfg && \
    echo "server.2=mymaster02:2888:3888" >> /opt/zookeeper/conf/zoo.cfg && \
    echo "server.3=myworker01:2888:3888" >> /opt/zookeeper/conf/zoo.cfg

WORKDIR /root