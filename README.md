**JR-Prj**

Start the DB in `~/java/docker/`

Run the tests with: `./mvnw -Dtest=ModelTest#testSearch test`


**TODO**
- DBMgr
  - login()
- Port the DBEObject class
- Port ObjectMgr
- dbschema.php
  - RRA Framework classes and objmgr
  - RRA Contacts: classes
  - CMS: classes
- Implement a rest/json login with Spring

**DONE**
- DBMgr
  - exists()
  - db_version()
  - search()
