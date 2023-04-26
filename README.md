# akka-sample-cluster-kubernetes-scala
akka sample cluster with kubernetes discovery in scala

This is an example SBT project showing how to create an Akka Cluster on
Kubernetes.

It is not always necessary to use Akka Cluster when deploying an Akka
application to Kubernetes: if your application can be designed as independent
stateless services that do not need coordination, deploying them on Kubernetes
as individual Akka application without Akka Cluster can be a good fit. When
state or coordination between nodes is necessary, this is where the
[Akka Cluster features](https://doc.akka.io/docs/akka/current/typed/cluster.html)
become interesting and it is worth consider making the nodes form an Akka
Cluster.

## Kubernetes Instructions
    
### Docker Desktop for Kubernetes
For Windows and Mac users, may be handier to use a Kubernetes cluster on [Docker-Desktop](https://www.docker.com/products/docker-desktop).
If you use Kubernetes on Docker Desktop, after turning it on, you should first issue:

    export KUBECONFIG=~/.kube/config
    kubectl config set-context docker-desktop
    
A script that comprises all steps involved is `scripts/test_docker_desktop.sh`. To run it, do:

    cd akka-sample-cluster-kubernetes-scala
    scripts/test_docker_desktop.sh

### Minikube
If you are using minikube for Kubernetes, please run the included scripts in the `scripts` directory.

## Starting

First, package the application and make it available locally as a docker image:

    sbt Docker/publishLocal

Then `akka-cluster.yml` should be sufficient to deploy a 2-node Akka Cluster, after
creating a namespace for it:

    kubectl apply -f kubernetes/namespace.json
    kubectl config set-context --current --namespace=appka-1
    kubectl apply -f kubernetes/akka-cluster.yml
    
Finally, create a service so that you can then test [http://127.0.0.1:8080](http://127.0.0.1:8080)
for 'hello world':

    kubectl expose deployment appka --type=LoadBalancer --name=appka-service

You can inspect the Akka Cluster membership status with the [Cluster HTTP Management](https://doc.akka.io/docs/akka-management/current/cluster-http-management.html).

    curl http://127.0.0.1:8558/cluster/members/

To check what you have done in Kubernetes so far, you can do:

    kubectl get deployments
    kubectl get pods
    kubectl get replicasets
    kubectl cluster-info dump
    kubectl logs appka-79c98cf745-abcdee   # pod name
    
To wipe everything clean and start over, do:

    kubectl delete namespaces appka-1

## Running in a real Kubernetes cluster

#### Publish to a registry the cluster can access e.g. Dockerhub with the kubakka user

The app image must be in a registry the cluster can see. The build.sbt uses DockerHub by default.
Start with `sbt -Ddocker.registry=your-registry` if your cluster can't access DockerHub.
  
The user for the registry is defined with `sbt -Ddocker.username=your-user`

To push an image to docker hub run:

 `sbt -Ddocker.username=your-user docker:publish`

And remove the `imagePullPolicy: Never` from the deployments. Then you can use the same `kubectl` commands
as described in the [Starting](#starting) section.

## How it works

This example uses [Akka Cluster Bootstrap](https://doc.akka.io/docs/akka-management/current/bootstrap/index.html)
to initialize the cluster, using the [Kubernetes API discovery mechanism](https://doc.akka.io/docs/akka-management/current/discovery/index.html#discovery-method-kubernetes-api) 
to find peer nodes.
