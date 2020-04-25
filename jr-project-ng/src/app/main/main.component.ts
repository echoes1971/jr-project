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
  parentsList: ObjLight[] = [
    {id: '-13', name: '', icon: 'glyphicon-folder-close'},
    {id: '-14', name: '', icon: 'glyphicon-folder-close'},
  ];

  constructor(private coreService: CoreService, private route: ActivatedRoute) { }
/*
  currentObjectObserver = {
    next: data => {
      console.log("currentObjectObserver: start.");
      this.currentObj = data;
      console.log(data);
      this.coreService.getParents(this.currentObj.id).subscribe((data2) => {
        console.log("currentObjectObserver: parents - start.");
        console.log(data2);
        this.parentsList = data2;
        console.log("currentObjectObserver: parents - end.");
      });
      console.log("currentObjectObserver: end.");
    }
    ,error: err => console.error('Observer got an error: ' + err)
    ,complete: () => console.log('MainComponent.currentObjectObserver: complete.')
  };
*/

  ngOnInit(): void {
    /*
    this.route.data.subscribe({
      next: data => {
        console.log("SUNCHI");
        console.log(data);
        console.log("SUNLI");
      },
      error: err => console.error('Observer got an error: ' + err),
      complete: () => console.log('Observer got a complete notification')
    })
     */
    this.route.paramMap.subscribe({
      next: params => {
        this.currentObjId = params.get('objId');
        this.coreService.currentObjId = params.get('objId');
        console.log('Current Object ID: ' + this.currentObjId);
        this.coreService.getCurrentObj().subscribe(
          data => {
            console.log("currentObjectObserver: start.");
            this.currentObj = data;
            console.log(data);
            this.coreService.getParents(this.currentObj.id).subscribe((data2) => {
              console.log("currentObjectObserver: parents - start.");
              console.log(data2);
              this.parentsList = data2;
              console.log("currentObjectObserver: parents - end.");
            });
            console.log("currentObjectObserver: end.");
          });
      },
      error: err => console.error('Observer got an error: ' + err),
      complete: () => console.log('Observer got a complete notification')
    });
    // this.route.paramMap.subscribe(myObserver);

    this.coreService.getRootObj().subscribe(data => {
        this.rootObj = data;
        /*
        this.coreService.getParents(this.rootObj.id).subscribe(data2 => {
          this.parentsList = data2;
        });
         */
    });
    /*
    this.coreService.getCurrentObj().subscribe(data => {
      this.currentObj = data;
    });
     */

    this.coreService.getMenuItems(this.currentObjId).subscribe(data => {
      console.log("MainComponent.getMenuItems: start.");
      this.menuItems = data;
      console.log(data);
      console.log("MainComponent.getMenuItems: end.");
    });
    // this.coreService.getMenuItems().subscribe(data => { this.menuItems = data; });
  }

}
