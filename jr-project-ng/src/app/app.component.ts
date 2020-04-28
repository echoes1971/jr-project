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

  rootObj: ObjLight = {id: '', name: '', _icon: ''};
  menuTop = [];

  constructor(private coreService: CoreService) {}

  ngOnInit(): void {
    this.coreService.getRootObj().subscribe(data => { this.rootObj = data; });
    this.coreService.getMenuTop().subscribe((data: ObjLight[]) => { this.menuTop = data; });
  }

  getCurrentId(): string { return this.coreService.currentObjId; }
  getCurrentUser(): any { return this.coreService.myUser; }

  logout() { this.coreService.logout(); }
}
