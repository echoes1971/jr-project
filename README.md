**JR-Prj**

Start the DB in `./docker/` and follow the instructions to initialize the DB.

Run the tests with: `./mvnw -Dtest=ModelTest test`

Run spring-boot: `./mvnw spring-boot:run`
maybe adding some memory before: `export MAVEN_OPTS=-Xmx1024m`

**TODO**
- Angular FE
  - main component: list childs?
  - object icons: handle properly
  - search component
- Controllers
  - UIController
    - getMenuItems
- main.php
  - menu_tree
  - mypeople: profile of the current user
- dbschema.php
  - Core
    - DBEntity: setValues
  - CMS
    - DBEFile
    - DBEEvent
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
  - Foreign keys?
    
- Implement a rest/json login with Spring

**DONE**
- Angular FE
  - login
  - logout
  - menu items
  - manage current object
- Controllers
  - UIController
    - getCurrentObject
    - getParentsList
- dbschema.php
  - CMS
    - DBEFolder
    - DBELink
    - DBENews
    - DBENote
    - DBEPage
  - Contacts
    - DBECountry
    - DBECompany
    - DBEPeople
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
