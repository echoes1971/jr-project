# NOTES

Links
- [MarkDown](https://www.markdownguide.org/cheat-sheet/)
- [CLI](https://angular.io/cli)
- [Angular Forms](https://angular.io/guide/forms)
- [Angular Proxy](https://github.com/angular/angular-cli/blob/master/docs/documentation/stories/proxy.md)

---
### Powershell
```
.\node_modules\.bin\ng.cmd
.\node_modules\.bin\ng.cmd generate component footer
.\node_modules\.bin\ng.cmd generate service core

ng build --configuration=production

& 'C:\Program Files\nodejs\node.exe' 'C:\Program Files\nodejs\node_modules\npm\bin\npm-cli.js' run
& 'C:\Program Files\nodejs\npm.cmd' run-script
```

---
### Bootstrap
Install bootstrap:
```
npm install --save bootstrap
npm install --save @ng-bootstrap/ng-bootstrap
```
Add `import { NgbModule } from '@ng-bootstrap/ng-bootstrap';` to the app module,
then add `@import '~bootstrap/dist/css/bootstrap.css';` to styles.css

See:
- [ng-bootstrap](https://ng-bootstrap.github.io/#/getting-started) and its [components](https://ng-bootstrap.github.io/#/components/)
- [bootstrap](https://getbootstrap.com/docs/4.4/components/) for original documentation
---
