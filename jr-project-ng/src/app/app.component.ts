import {Component, OnInit} from '@angular/core';
import {CoreService} from './core.service';
import {ObjLight} from './objlight';
import {ActivatedRoute} from '@angular/router';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'JR-Project';

  myUser = null; // { fullname: 'echoes'};

  rootObj: ObjLight = {id: '', name: '', icon: ''};
  menuTop = [];

  // currentObjId = '';

  constructor(private coreService: CoreService, private route: ActivatedRoute) {}

  paramMapObserver = {
    next: params => {
      console.log('SUNCHI');
      console.log(params.get('objId'));
      // this.currentObjId = params.get('objId') || '';
      // this.coreService.currentObjId = params.get('objId');
      // console.log('AppComponent.paramMapObserver: currentObjId=' + this.currentObjId);
    }
    , error: err => console.error('AppComponent.paramMapObserver error: ' + err)
    , complete: () => console.log('AppComponent.paramMapObserver: complete notification')
  };

  ngOnInit(): void {
    this.coreService.getRootObj().subscribe(data => { this.rootObj = data; });
    this.coreService.getMenuTop().subscribe((data: ObjLight[]) => { this.menuTop = data; });

    this.route.paramMap.subscribe(this.paramMapObserver);
  }

  getCurrentId(): string { return this.coreService.currentObjId; }
}
