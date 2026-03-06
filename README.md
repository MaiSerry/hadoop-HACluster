# High Availability Hadoop Cluster

This repository presents a robust, easily deployable **Hadoop High Availability (HA) cluster** using Docker and Docker Compose. Engineered to eliminate single points of failure within HDFS and YARN

## Features

*   **High Availability**: Implements HDFS HA with two NameNodes (Active/Standby) and JournalNodes for shared edit logs, ensuring data accessibility and resilience against failures.
*   **Dockerized Deployment**: Simplifies cluster setup and management through containerization, enabling rapid spin-up and teardown of the entire Hadoop ecosystem.
*   **Automatic Failover**: Integrates Apache ZooKeeper and the ZooKeeper Failover Controller (ZKFC) for seamless and automatic NameNode failover, significantly minimizing downtime.

*   **YARN High Availability**: Configures YARN with multiple ResourceManager instances for resilient job scheduling and application management, enhancing overall cluster reliability.

## Architecture

The cluster is built upon a high-availability architecture designed for fault tolerance and continuous operation.

<img width="1582" height="796" alt="Image" src="https://github.com/user-attachments/assets/1b737e4f-27be-4c3a-b717-5e62023a6c83" />


## Prerequisites

Ensure your system meets the following requirements before proceeding with the cluster setup:

*   **Docker Engine**: Version 18.06.0 or higher. [Installation Guide](https://docs.docker.com/engine/install/)
*   **Docker Compose**: Version 1.27.0 or higher. [Installation Guide](https://docs.docker.com/compose/install/)


## Setup and Installation

Follow these steps to set up and launch your Hadoop HA cluster:

1.  **Clone the repository**:

    ```bash
    git clone https://github.com/MaiSerry/hadoop-HACluster.git
    cd hadoop-HACluster
    ```

2.  **Build the Docker image**:

    The `Dockerfile` defines the base Hadoop image. Build it once. This image will be tagged as `hadoop-ha:1.0`.

    ```bash
    docker build -t hadoop-ha:1.0.
    ```

3.  **Start the Hadoop HA Cluster**:

    This command will bring up all services (NameNodes, JournalNodes, ZooKeeper, DataNodes) in detached mode (`-d`).

    ```bash
    docker-compose up -d
    ```

4.  **Initialize HDFS and format NameNode**:

    This step is crucial and should only be performed once during the initial setup of the cluster. It formats the HDFS filesystem.

    ```bash
    docker exec -it mymaster01 hdfs namenode -format
    ```

5.  **Start ZKFC and initialize ZooKeeper for HA**:

    Initialize the ZooKeeper state for automatic NameNode failover. This also needs to be done only once.

    ```bash
    docker exec -it mymaster01 hdfs zkfc -formatZK
    ```

6.  **Start the HDFS and YARN services**:

    Launch the core Hadoop services across the cluster. This includes starting the NameNodes, DataNodes, ResourceManagers, and NodeManagers.

    ```bash
    docker exec -it mymaster01 start-dfs.sh
    docker exec -it mymaster01 start-yarn.sh
    ```


    
## Technologies
- Hadoop 3.4.2
- ZooKeeper 3.8.6
- Docker + Docker Compose
- Ubuntu 24.04
- Java 17 (OpenJDK)

## Contributing

Contributions are welcome! If you have suggestions for improvements, bug fixes, or new features, please feel free to open a Pull Request




