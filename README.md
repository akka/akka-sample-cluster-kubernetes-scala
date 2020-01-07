# akka-sample-cluster-kubernetes-scala
akka sample cluster with kubernetes discovery in scala

This is an example SBT project showing how to create an Akka Cluster on
Kubernetes.

It is not always necessary to use Akka Cluster when deploying an Akka
application to Kubernetes: if your application can be designed as independent
stateless services that do not need coordination, deploying them on Kubernetes
as individual Akka application without Akka Cluster can be a good fit. When
coordination between nodes is necessary, this is where the
[Akka Cluster features](https://doc.akka.io/docs/akka/current/index-cluster.html)
become interesting and it is worth consider making the nodes form an Akka
Cluster.

## Kubernetes Instructions
    
### Docker Desktop for Kubernetes
If you use Kubernetes on Docker Desktop, after turning it on, you should first issue:

    cd akka-sample-cluster-kubernetes-java/kubernetes
    
    export KUBECONFIG=~/.kube/config
    kubectl config set-context docker-desktop
    
A script does all this is `scripts/test_docker_desktop.sh`. To run it, do:

    cd akka-sample-cluster-kubernetes-java
    scripts/test_docker_desktop.sh

### Minikube
If you are using minikube for Kubernetes, please run the included scripts in the `scripts` directory.


## Starting

First, package the application and make it available locally as a docker image:

    sbt docker:publishLocal

Then `akka-cluster.yml` should be sufficient to deploy a 2-node Akka Cluster, after
creating a namespace for it:

    kubectl apply -f namespace.json
    kubectl config set-context --current --namespace=appka-1
    kubectl apply -f kubernetes/akka-cluster.yml
    
Finally, create a service so that you can then test [http://127.0.0.1:8080](http://127.0.0.1:8080)
for 'hello world':

    kubectl expose deployment appka --type=LoadBalancer --name=appka-service

To check what you have done in Kubernetes so far, you can do:

    kubectl get deployments
    kubectl get pods
    kubectl get replicasets
    kubectl cluster-info dump
    kubectl logs appka-79c98cf745-rhwhz   # pod name
    
To wipe everything clean and start over, do:

    kubectl delete namespaces appka-1
## How it works

This example uses [Akka Cluster Bootstrap](https://developer.lightbend.com/docs/akka-management/current/bootstrap/index.html)
to initialize the cluster, using the [Kubernetes API discovery mechanism](https://developer.lightbend.com/docs/akka-management/current/discovery/index.html#discovery-method-kubernetes-api)
to find peer nodes.
