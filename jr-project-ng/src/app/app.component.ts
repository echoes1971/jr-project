import {Component, OnInit} from '@angular/core';
import {CoreService} from './core.service';
import {ObjLight} from './objlight';



@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'JR-Project';

  myUser = { fullname: 'echoes'};

  rootObj: ObjLight = {id: '', name: '', icon: ''};
  menuTop = [];
  parentsList = [];

  constructor(private coreService: CoreService) {}

  ngOnInit(): void {
    this.coreService.getRootObj().subscribe(data => { this.rootObj = data; });
    this.coreService.getMenuTop().subscribe((data: ObjLight[]) => { this.menuTop = data; });
    this.coreService.getParents().subscribe((data: ObjLight[]) => { this.parentsList = data; });
  }
}
