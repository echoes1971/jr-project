import { Component, OnInit } from '@angular/core';
import {CoreService} from '../core.service';
import {ObjLight} from '../objlight';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css'],
})
export class MenuComponent implements OnInit {
  rootObj: ObjLight = {id: '', name: '', icon: ''};

  indent = '';
  menuItems = [];

  constructor(private coreService: CoreService) { }

  ngOnInit(): void {
    this.coreService.getRootObj().subscribe(data => { this.rootObj = data; });
    this.menuItems = this.coreService.getMenuItems();
  }

}
