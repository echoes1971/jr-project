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

  rootObj: ObjLight = {id: '', name: '', icon: ''};
  currentObj: ObjPage = {id: '', name: '', icon: '', html: '<b>Loading...</b>'};
  indent = '';
  menuItems = [];

  currentObjId = null;
  parentsList: ObjLight[] = [];

  constructor(private coreService: CoreService, private route: ActivatedRoute) { }

  paramMapObserver = {
    next: params => {
      this.currentObjId = params.get('objId');
      this.coreService.currentObjId = params.get('objId');
      // console.log('MainComponent.paramMapObserver: currentObjId=' + this.currentObjId);
      this.coreService.getRootObj().pipe(
        mergeMap(rootObj => {
          this.rootObj = rootObj;
          if (this.currentObjId == null) { this.currentObjId = this.rootObj.id; }
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

  _createMenuLevel(indent: string, myindex: number, items: any[], menuItems: any[]): number {
    items.forEach((value, index2, array2) => {
      this.menuItems[myindex] = [indent, value];
      this.menuItems[myindex][1].icon = 'glyphicon-folder-close'; // TODO do it when fetching from the back end
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

}
