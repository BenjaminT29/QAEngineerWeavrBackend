# QA Engineer test

### This is a sample project to test APIs using JUnit and RestAssured. In this project, some test cases were written for a number of endpoints by Go Rest (https://gorest.co.in/)

### Test Strategy:
-First, the API contract was analyzed by examining the document.Then some scenarios were created and executed manually. 
<br>-One-step requests were sent to API to verify whether the end points were working properly.
<br>-Simple request and response validation was performed with positive and negative tests.
<br>-In the next step, the flow is created and validated using CRUD operators.
<br>-After the manual test phase, automation tests were executed on the created framework. 

The scenarios of Update without authentication, delete without authentication
and create with an invalid email were faile because of status code. 
Normally, since they were failed at the manual test stage, 
it doesn’t need to be tested but I wanted to show the failure on the automation too. 
The screenshots for evidence were added to project, in the **src/main/resources/FailureScreenshots.**

### Tools
In this project; RestAssured was used for API automation, JUnit Jupiter as testing framework.
In addition, Project Lombok was preferred for a cleaner code structure.