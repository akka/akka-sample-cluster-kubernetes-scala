# akka-sample-cluster-kubernetes-scala
akka sample cluster with kubernetes discovery in scala


# akka-sample-cluster-kubernetes-java

This is an example Maven project showing how to create an Akka Cluster on
Kubernetes.

It is not always necessary to use Akka Cluster when deploying an Akka
application to Kubernetes: if your application can be designed as independent
stateless services that do not need coordination, deploying them on Kubernetes
as individual Akka application without Akka Cluster can be a good fit. When
coordination between nodes is necessary, this is where the
[Akka Cluster features](https://doc.akka.io/docs/akka/current/index-cluster.html)
become interesting and it is worth consider making the nodes form an Akka
Cluster.

## Starting

First, package the application and make it available locally as a docker image:

    mvn clean package docker:build

Then akka-cluster.yml should be sufficient to deploy a 2-node Akka Cluster:

    cd akka-sample-cluster-kubernetes-java/kubernetes
    
    kubectl apply -f appka-ns.json
    kubectl config set-context --current --namespace=appka-1
    kubectl apply -f akka-cluster.yml
    kubectl expose deployment appka --type=LoadBalancer --name=appka-service
    kubectl get deployments
    kubectl get pods
    kubectl get replicasets
    
    kubectl cluster-info dump
    kubectl delete namespaces appka-1

## How it works

This example uses [Akka Cluster Bootstrap](https://developer.lightbend.com/docs/akka-management/current/bootstrap/index.html)
to initialize the cluster, using the [Kubernetes API discovery mechanism](https://developer.lightbend.com/docs/akka-management/current/discovery/index.html#discovery-method-kubernetes-api)
to find peer nodes.
