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

  rootObj: ObjLight = null;
  rootObj$: Observable<ObjLight> = null;
  currentObjId = '';
  currentObj: ObjPage = null;
  currentObj$: Observable<ObjPage> = null;
  menuTop: ObjLight[] = null;
  menuTop$: Observable<ObjLight[]> = null;
  parentsList: ObjLight[] = null;
  parentsList$: Observable<ObjLight[]> = null;
  menuItems: Observable<any[]> = null;

  myUser: any = null;

  constructor(private http: HttpClient) {
    console.log(environment.apiUrl + '/ui/rootobj');

    this.rootObj$ = this.http.get<ObjLight>('/ui/rootobj');
    this.menuTop$ = this.http.get<ObjLight[]>('/ui/topmenu');
    // this.currentObj$ = this.http.get<ObjPage>('/ui/obj/' + this.currentObjId);

    this.rootObj$.subscribe(data => {
      this.rootObj = data;
      if (this.currentObjId === '') { this.currentObjId = this.rootObj.id; }
    });
    this.menuTop$.subscribe(data => { this.menuTop = data; });

    /*
    this.currentObj$.subscribe({
      next: data => { this.currentObj = data; }
      , error: err => console.error('CoreService.currentObjObserver.pipe error: ' + err)
      , complete: () => {
        // console.log('MainComponent.paramMapObserver.pipe: complete notification');
      }
    });
     */
  }

  getRootObj() { return this.rootObj$; }

  getMenuTop() { return this.menuTop$; }

  getCurrentObj(objId: string) {
    // if(this.currentObj == null) {
    this.currentObj$ = this.http.get<ObjPage>('/ui/obj/' + objId);
    // }
    return this.currentObj$;
  }

  getMenuItems(objId: string): Observable<any[]> {
    // console.log('CoreService.getMenuItems: objId=' + objId);
    this.menuItems = this.http.get<any[]>('/ui/menutree/' + objId);
    return this.menuItems;
    /*
    return [
      {id: '-13', name: 'Downloads', icon: 'glyphicon-folder-close'},
      {id: '-14', name: 'About us', icon: 'glyphicon-folder-close'},
    ];
     */
  }

  getParents(objId: string) {
    // console.log('CoreService.getParents: objId=' + objId);
    this.parentsList$ = this.http.get<ObjLight[]>('/ui/parentlist/' + objId);
    return this.parentsList$;
  }

  login(username: string, password: string) {
    console.log('CoreService.login: ' + username + ' ' + password);
    this.http.post<any>('/api/user/login', {login: username, pwd: password} ).subscribe(data => {
      console.log(data);
      this.myUser = data;
    });
  }

  logout() {
    this.myUser = null;
    this.http.post<any>('/api/user/logout', {} ).subscribe(data => {
      console.log(data);
    });
  }
}
