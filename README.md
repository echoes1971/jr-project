**JR-Prj**

Start the DB in `./docker/` and follow the instructions to initialize the DB.

Run the tests with: `./mvnw -Dtest=ModelTest test`

Run spring-boot: `./mvnw spring-boot:run`
maybe adding some memory before: `export MAVEN_OPTS=-Xmx1024m`

**TODO**
- dbschema.php
  - Core
    - DBEntity: setValues
  - Contacts
    - DBECountry
    - DBECompany
    - DBEPeople
  - CMS
    - DBEFile
    - DBELink
    - DBEEvent
    - DBENews
  - Projects
    - DBEProject
    - DBEProjectCompanyRole
    - DBEProjectCompany
    - DBEProjectPeopleRole
    - DBEProjectPeople
    - DBEProjectProjectRole
    - DBEProjectProject
    - DBETimetrack
    - DBETodo
    - DBETodoTipo
    
- Implement a rest/json login with Spring

**DONE**
- dbschema.php
  - CMS
    - DBEFolder
    - DBENote
    - DBEPage
  - Core
    - ObjectMgr
      - objectByName
      - fullObjectByName
      - objectById
      - fullObjectById
      - dbeById: cool stuff
    - DBEObject
    - DBELog
    - DBMgr
    - DBEDBVersion
    - DBEGroup
    - DBEUser

**TECH INFO**

Reduce log: see https://github.com/eugenp/tutorials/blob/master/persistence-modules/hibernate5-2/src/test/resources/logback.xml
