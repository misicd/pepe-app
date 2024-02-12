Pepe App
=======================

The Pepe App is the REST API application for maintaining the catalog of persons and pets, 
and linking and unlinking persons with pets.
Unlike the actual world where pets always have an owner, 
in line with the project requirements pets in the Pepe App by default don't have an owner.
Instead, the pet can be linked to the owner via dedicated endpoint.
Similarly, the pet can be unlinked from the owner via dedicated endpoint.

**Run Pepe App as standalone app with in-memory H2 database**
--------------------------------------------------


- cd to project dir
- compile the project:

`mvn clean install`

- run the installed app:

`java -jar -Dspring.datasource.init_db_file.location='app/target/classes/' app/target/app-1.0.0.jar`

**Run Pepe App as standalone app with mySql database in docker container**
--------------------------------------------------


- cd to project dir
- compile the project:

`mvn clean install`

- start docker deamon/desktop

- create mysql db docker container:

`docker run -d -p 6666:3306 --name=pepe-mysql --env="MYSQL_ROOT_PASSWORD=test1234" --env="MYSQL_DATABASE=mydb" mysql`

- create db schema inside mysql db docker container:

`docker exec -i pepe-mysql mysql -uroot -ptest1234 mydb<./app/target/classes/init_db.sql`

- run the installed app with 'int' config profile:

`java -jar -Dspring.profiles.active=int app/target/app-1.0.0.jar`

**API Documentation**
--------------------------------------------------


Once you start the app locally (presuming you have kept the configured port 8081), 
you can access API Documentation using the following link:

`http://localhost:8081/swagger-ui/index.html`
