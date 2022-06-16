Project title:  
pw-borrower-service  
  
Project Description:  
pw-borrower-service manages all the customer related functionality like customer creation, updation etc.  
  
Requirements:  
For building and running the application you need:  
-> JAVA version 11  
-> Maven 4.0.0  
  
How to Install and Run the Project:  
Prerequisite:  
In order to start the pw-borrower-service in local first we have to run the pw-configuration-service in local.  
  
Steps to run the pw-borrower-service are below:  
Step 1: Change the "spring.cloud.config.uri=http://localhost:8080/" in application.properties file.  
Step 2: Run as adding VM arguments "-Dspring.profiles.active=local,native".  
  
Contributing:  
Step 1: Fork it or Create your feature branch: git checkout -b <'my-new-feature'>  
Step 2: Commit your changes: git commit -am <'Add some comment'>  
Step 3: Push to the branch: git push origin <'my-new-feature'>  
Step 4: Submit a pull request  
