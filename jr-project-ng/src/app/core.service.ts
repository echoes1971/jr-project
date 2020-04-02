import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

import { environment } from '../environments/environment';
import { ObjLight } from './objlight';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CoreService {

  rootObj: Observable<ObjLight> = null;
  menuTop: Observable<ObjLight[]> = null;
  parentsList: Observable<ObjLight[]> = null;

  constructor(private http: HttpClient) {
    console.log(environment.apiUrl + '/ui/rootobj');

    this.rootObj = this.http.get<ObjLight>('/ui/rootobj');
    this.menuTop = this.http.get<ObjLight[]>('/ui/topmenu');
    this.parentsList = this.http.get<ObjLight[]>('/ui/parentlist');
  }

  getRootObj() { return this.rootObj; }

  getMenuTop() { return this.menuTop; }

  getMenuItems(): ObjLight[] {
    return [
      {id: '-13', name: 'Downloads', icon: 'glyphicon-folder-close'},
      {id: '-14', name: 'About us', icon: 'glyphicon-folder-close'},
    ];
  }

  getParents() { return this.parentsList; }

}
