**JR-Prj**

Start the DB in `./docker/` and initialise it as the script says.

Run the tests with: `./mvnw -Dtest=ModelTest test`


**TODO**
- dbschema.php
  - Framework
    - DBEObject
    - ObjectMgr
  - Contacts
    - DBECountry
    - DBECompany
    - DBEPeople
  - CMS
    - DBEEvent
    - DBEFile
    - DBEFolder
    - DBELink
    - DBENote
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
  - Framework
    - DBELog
    - DBMgr
      - login()
      - exists()
      - db_version()
      - search()
    - DBEDBVersion
    - DBEGroup
    - DBEUser
