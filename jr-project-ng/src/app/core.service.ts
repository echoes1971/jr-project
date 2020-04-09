import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

import { environment } from '../environments/environment';
import { ObjLight } from './objlight';
import {Observable} from 'rxjs';
import {ObjPage} from './objpage';

@Injectable({
  providedIn: 'root'
})
export class CoreService {

  rootObj: Observable<ObjLight> = null;
  currentObjId = '';
  currentObj: Observable<ObjPage> = null;
  menuTop: Observable<ObjLight[]> = null;
  parentsList: Observable<ObjLight[]> = null;

  constructor(private http: HttpClient) {
    console.log(environment.apiUrl + '/ui/rootobj');

    this.rootObj = this.http.get<ObjLight>('/ui/rootobj');
    this.currentObj = this.http.get<ObjPage>('/ui/obj/' + this.currentObjId);
    this.menuTop = this.http.get<ObjLight[]>('/ui/topmenu');
    this.rootObj.subscribe(data => {
      this.currentObjId = data.id;
      this.parentsList = this.http.get<ObjLight[]>('/ui/parentlist/' + this.currentObjId);
    });
  }

  getRootObj() { return this.rootObj; }

  getCurrentObj() {
    // if(this.currentObj == null) {
    this.currentObj = this.http.get<ObjPage>('/ui/obj/' + this.currentObjId);
    // }
    return this.currentObj;
  }

  getMenuTop() { return this.menuTop; }

  getMenuItems(): ObjLight[] {
    return [
      {id: '-13', name: 'Downloads', icon: 'glyphicon-folder-close'},
      {id: '-14', name: 'About us', icon: 'glyphicon-folder-close'},
    ];
  }

  getParents() {
    this.parentsList = this.http.get<ObjLight[]>('/ui/parentlist/' + this.currentObjId);
    return this.parentsList;
  }

}
