import { Component, OnInit } from '@angular/core';
import {CoreService} from '../core.service';
import {ActivatedRoute} from '@angular/router';
import {ObjLight} from '../iobjects';
import {mergeMap} from 'rxjs/operators';
import {
  DBECompany,
  DBEFolder,
  DBELink,
  DBENews, DBENote,
  DBEObject,
  DBEPage,
  DBEPeople,
  User
} from '../dbschema';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

  constructor(private coreService: CoreService, private route: ActivatedRoute) {
    const dbo = new DBEPage();
    dbo.html = '<b>Loading...</b>';
    this.currentObj = dbo;
  }

  rootObj: ObjLight = {id: '', name: '', _icon: ''};
  currentObj: DBEObject; // = new DBEPage('html': '<b>Loading...</b>');
    // _class: '', _icon: '', id: '', name: '', html: '<b>Loading...</b>'};
  indent = '';
  menuItems = [];
  contentItems = [];

  currentObjId = null;
  parentsList: ObjLight[] = [];

  paramMapObserver = {
    next: params => {
      this.setCurrentObjId(params.get('objId'));
      // console.log('MainComponent.paramMapObserver: currentObjId=' + this.currentObjId);
      this.coreService.getRootObj().pipe(
        mergeMap(rootObj => {
          this.rootObj = rootObj;
          if (this.currentObjId == null) { this.setCurrentObjId(this.rootObj.id); }
          return this.coreService.getCurrentObj(this.currentObjId);
        }),
        mergeMap(curObj => {
          // console.log('paramMapObserver:');
          // console.log(curObj._class);
          let dbeObject: DBEObject;
          switch (curObj._class) {
            case 'DBECompany':
              dbeObject = new DBECompany();
              break;
            case 'DBEPeople':
              dbeObject = new DBEPeople();
              break;
            case 'DBEFolder':
              dbeObject = new DBEFolder();
              break;
            case 'DBELink':
              dbeObject = new DBELink();
              break;
            case 'DBENews':
              dbeObject = new DBENews();
              break;
            case 'DBENote':
              dbeObject = new DBENote();
              break;
            case 'DBEPage':
              dbeObject = new DBEPage();
              break;
            default:
              dbeObject = new DBEObject();
              break;
          }
          dbeObject.setValues(curObj);
          this.currentObj = dbeObject;
          return this.coreService.getParents(this.currentObj.id);
        }),
        mergeMap(parents => {
          this.parentsList = parents;
          return this.coreService.getMenuItems(this.currentObjId);
        }),
        mergeMap(menuItems => {
          this.menuItems = [];
          this.contentItems = [];
          this._createMenuLevel('', 0, menuItems[this.parentsList[0].id], menuItems);
          return [];
        })
      ).subscribe({
        next: () => {
          // console.log('SUCCESSO');
        }
        , error: err => console.error('MainComponent.paramMapObserver.pipe error: ' + err)
        , complete: () => {
          // console.log('MainComponent.paramMapObserver.pipe: complete notification');
        }
      });
    }
    , error: err => console.error('MainComponent.paramMapObserver error: ' + err)
    , complete: () => console.log('MainComponent.paramMapObserver: complete notification')
  };

  setCurrentObjId(value: string): void {
    if (value == null) { return; }
    this.currentObjId = value;
    this.coreService.currentObjId = value;
  }

  _createMenuLevel(indent: string, myindex: number, items: any[], menuItems: any[]): number {
    items.forEach((value, index2, array2) => {
      if (value.father_id === this.currentObjId) {
        if (value._class === 'DBELink' && value.href.startsWith('http')) {
          this.contentItems.push(value);
          return myindex;
        } else if (value._class === 'DBENote') {
          this.contentItems.push(value);
          // return myindex;
        } else if (value._class === 'DBEPage') {
          this.contentItems.push(value);
          // return myindex;
        } else {
          this.contentItems.push(value);
        }
      }
      this.menuItems[myindex] = [indent, value];
      // console.log(this.menuItems[myindex][1]);
      // console.log('myindex: ' + myindex + ' ' + indent + value.id + ' ' + value.name);
      myindex++;
      if (value.id in menuItems) {
        // console.log('Found: ' + value.id);
        // console.log(menuItems[value.id]);
        myindex = this._createMenuLevel(indent + '&nbsp;', myindex, menuItems[value.id], menuItems);
      }
    });
    return myindex;
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(this.paramMapObserver);
  }

  get currentUser(): User { return this.coreService.myUser; }
}
