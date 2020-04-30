import { Component, OnInit } from '@angular/core';
import {CoreService} from '../core.service';
import {ActivatedRoute} from '@angular/router';
import {ObjLight} from '../objlight';
import {ObjPage} from '../objpage';
import {mergeMap} from 'rxjs/operators';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

  constructor(private coreService: CoreService, private route: ActivatedRoute) { }

  rootObj: ObjLight = {id: '', name: '', _icon: ''};
  currentObj: ObjPage = {id: '', name: '', _icon: '', html: '<b>Loading...</b>'};
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
          this.currentObj = curObj;
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
          return myindex;
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

  getCurrentUser(): any { return this.coreService.myUser; }
}
