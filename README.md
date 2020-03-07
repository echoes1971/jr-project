**JR-Prj**

Start the DB in `./docker/` and initialise it as the script says.

Run the tests with: `./mvnw -Dtest=ModelTest test`


**TODO**
- Port the DBEObject class
- Port ObjectMgr
- dbschema.php
  - RRA Framework classes and objmgr
  - RRA Contacts: classes
  - CMS: classes
- Implement a rest/json login with Spring

**DONE**
- DBMgr
  - login()
  - exists()
  - db_version()
  - search()
