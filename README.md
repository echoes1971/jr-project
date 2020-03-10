**JR-Prj**

Start the DB in `./docker/` and follow the instructions to initialize the DB.

Run the tests with: `./mvnw -Dtest=ModelTest test`


**TODO**
- dbschema.php
  - Contacts
    - DBECountry
    - DBECompany
    - DBEPeople
  - CMS
    - DBEEvent
    - DBEFile
    - DBEFolder
    - DBELink
    - DBEPage
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
    - DBENote
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
