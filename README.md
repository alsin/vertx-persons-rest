# vertx-persons-rest
A simple project which utilizes Vert.x framework to launch a lightweight HTTP server with the following REST API:
* GET   http://localhost:8080/persons - returns all person records as JSON objects
* GET   http://localhost:8080/persons/:objId - returns a particular person record by its "_id"
* POST  http://localhost:8080/persons - inserts a new person record whether if it does not yet have an "_id" or it has one but it's not in the collection yet or updates the existing record if such "_id" is already found in the collection

## Prerequisites
You have to have:
* Java 8
* Maven
* MongoDB

### MongoDB
MongoDB should be installed and launched before trying It should be enough to run it with default configuration as follows:

    mongod --dbpath path/to/data

## Running the application
In the top folder of the project execute

    mvn clean package

This will produce a fat jar in the ./target folder of the project which you can run simply by executing the following command from the root folder of the project:

    java -jar target/persons-rest-1.0-SNAPSHOT-fat.jar

This will launch an HTTP server at port 8080 and the REST API as described above should be available for usage.
Also, in the root of the project you can find an example configuration JSON file for the application which you can use in the following way when launching from the root of the project:

    java -jar target/persons-rest-1.0-SNAPSHOT-fat.jar -conf example-app-conf.json

This configuration file allows you to overwrite the following application settings:
*  "mongo.db.name" - this is the name of the MongoDB table to use
*  "mongo.host" - host on which your MongoDB server is running
*  "mongo.port" - port on which you MongoDB server is listening
*  "persons.collection.name" - name for you persons collection


## Executing tests

During 'mvn package' execution functional and unit tests are also run. But if you want to execute tests only, run this command in the root folder of the project:

    mvn clean test


