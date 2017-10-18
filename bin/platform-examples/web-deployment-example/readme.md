# Web Deployment Sample

This is a sample to demonstrate web deployment of a DolphinPlatform application

## Getting Started
These instructions will produce a war file which can be deployed in an App server
like Tomcat. Following are the outcome of the steps
* A **war** is generated for deploying it on app server.
* **Polymer client** will be part of war & can be started directly by typing the url
of client's html file in a browser
* **JavaFx client** will be part of war & can be started directly by typing the url
of client's jnlp file in the browser

### Prerequisites

Go to the polymer-client directory of the project
```
cd dolphin-platform-examples/platform-examples/web-deployment-example/polymer-client
```

Install bower dependencies(if asked for polymer version, 
choose option which has version described as __>=1.0.4__ __<=1.4.0__)
```
bower install
```

### Installing

#### Polymer Client

If you want to change the dolphin connection URL then,
open index.html of polymer-client
```
vi index.html
```

Edit the client context URL, Make sure you have war file Name in the URL (http://localhost:8080/WebDeployment/dolphin)

#### JavaFx Client
If you want to change the dolphin connection URL then,
open **ClientApplication** file which resides in 
**dolphin-platform-examples/platform-examples/web-deployment-example/client/src/main/java/com/canoo/webdeployment**
directory.

If you want to provide your own keystore then add your keystore file in 
**dolphin-platform-examples/platform-examples/web-deployment-example/client/keystore** 
directory & then replace the file name in the **pom.xml** file of client as well.


#### Generate war file
Build the client
```
cd dolphin-platform-examples/platform-examples/web-deployment-example/web-deployment-client
gradle clean build
```

Build the server
```
cd dolphin-platform-examples/platform-examples/web-deployment-example/web-deployment-server
gradle clean build
```

Copy the generated war file from __server/build/libs__ directory & 
paste it in tomcat __webapps__ directory


### Usage

After the application is successfully deployed, you can then run below client's

#### Polymer Client
```
http://localhost:8080/WebDeployment/polymer/index.html
```
 
#### JavaFx Client
```
http://localhost:8080/WebDeployment/fxclient/fxclient.jnlp
```
After downloading file, you run as a Webstart application

## Authors

* **Janak Mulani (janak.mulani@canoo.com)**
* **Ganesh Deshvini (ganesh.Deshvini@canoo.com)**