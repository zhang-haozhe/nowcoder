# Running the project

The project is developed under the IntelliJ Idea environment. For your best experience, I recommend to run the project
using IntelliJ too.

Below are the necessary steps to install the technologies used in the project.

1. Install Maven and add it to the system path.
2. Install MySQL. Run init_data.sql and init_schema.sql to initialize the database with the tables and the mock data.
3. Install Redis and add it to the system path.
4. Install wkhtmltopdf and add it to the system path.
5. Install Kafka. Add the path to its bin folder to your system path.
6. Fill out secrets.properties and config.properties with all the credentials like the link to your database.

Once all the installation and configuration steps are done, perform below steps before running the project:

1. Go to your kafka installation directory, and then open config/server.directory. Change log.dirs to where your desired
   path to save the logs is.
2. Similarly, open zookeeper.properties under the same directory, and change dataDir to where you wish the logs to be.

Now run the project:

1. CD to your kafka directory, and run the following command to start the zookeeper server:
   <code>.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties</code>
2. Similarly, run the command below to start the Kafka service:
   <code>.\bin\windows\kafka-server-start.bat .\config\server.properties</code>