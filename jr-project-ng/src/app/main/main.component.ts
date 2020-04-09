import { Component, OnInit } from '@angular/core';
import {CoreService} from '../core.service';
import {ActivatedRoute} from '@angular/router';
import {ObjLight} from '../objlight';
import {ObjPage} from '../objpage';

/*
const myObserver = {
  next: x => {
    console.log('Observer got a next value: ');
    console.log(x);
  },
  error: err => console.error('Observer got an error: ' + err),
  complete: () => console.log('Observer got a complete notification'),
};
*/

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

  currentObjId = '';
  parentsList = [];

  constructor(private coreService: CoreService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe({
      next: params => {
        this.currentObjId = params.get('objId');
        this.coreService.currentObjId = params.get('objId');
        // console.log('Current Object ID: ' + this.currentObjId);
        this.coreService.getCurrentObj().subscribe(data => { this.currentObj = data; });
        this.coreService.getParents().subscribe((data: ObjLight[]) => { this.parentsList = data; });
      },
      error: err => console.error('Observer got an error: ' + err),
      complete: () => console.log('Observer got a complete notification')
    });

    // this.route.paramMap.subscribe(myObserver);

    this.coreService.getRootObj().subscribe(data => {
      this.rootObj = data;
      this.coreService.getParents().subscribe((data2: ObjLight[]) => { this.parentsList = data2; });
    });
    this.coreService.getCurrentObj().subscribe(data => { this.currentObj = data; });

    this.menuItems = this.coreService.getMenuItems();
    // this.coreService.getMenuItems().subscribe(data => { this.menuItems = data; });
  }

}
