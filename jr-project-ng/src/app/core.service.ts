import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

import { environment } from '../environments/environment';
import {Observable} from 'rxjs';
import {IDBEObject} from './iobjects';
import {DBEObject, User} from './dbschema';

@Injectable({
  providedIn: 'root'
})
export class CoreService {

  rootObj: IDBEObject = null;
  rootObj$: Observable<IDBEObject> = null;
  currentObjId = '';
  currentObj: DBEObject = null;
  currentObj$: Observable<DBEObject> = null;
  menuTop: IDBEObject[] = null;
  menuTop$: Observable<IDBEObject[]> = null;
  parentsList: IDBEObject[] = null;
  parentsList$: Observable<IDBEObject[]> = null;
  menuItems: Observable<any[]> = null;

  myUser: User = null;

  constructor(private http: HttpClient) {
    console.log(environment.apiUrl + '/ui/rootobj');

    this.rootObj$ = this.http.get<IDBEObject>('/ui/rootobj');
    this.menuTop$ = this.http.get<IDBEObject[]>('/ui/topmenu');
    // this.currentObj$ = this.http.get<ObjPage>('/ui/obj/' + this.currentObjId);

    this.rootObj$.subscribe(data => {
      this.rootObj = data;
      if (this.currentObjId === '') { this.currentObjId = this.rootObj.id; }
    });
    this.menuTop$.subscribe(data => { this.menuTop = data; });
  }

  getRootObj() { return this.rootObj$; }

  getMenuTop() { return this.menuTop$; }

  getCurrentObj(objId: string) {
    this.currentObj$ = this.http.get<DBEObject>('/ui/obj/' + objId);
    return this.currentObj$;
  }

  getMenuItems(objId: string): Observable<any[]> {
    this.menuItems = this.http.get<any[]>('/ui/menutree/' + objId);
    return this.menuItems;
  }

  getParents(objId: string) {
    // console.log('CoreService.getParents: objId=' + objId);
    this.parentsList$ = this.http.get<IDBEObject[]>('/ui/parentlist/' + objId);
    return this.parentsList$;
  }

  login(username: string, password: string) {
    // console.log('CoreService.login: ' + username + ' ' + password);
    this.http.post<any>('/api/user/login', {login: username, pwd: password} ).subscribe(data => {
      const user = new User();
      user.setValues(data);
      this.myUser = user;
    });
  }

  logout() {
    this.myUser = null;
    this.http.post<any>('/api/user/logout', {} ).subscribe(data => {
      console.log(data);
    });
  }

  currentUser() {
    this.http.get<any>('/api/user/current').subscribe(data => {
      // console.log('currentUser:');
      // console.log(data);
      if (data == null) { return; }
      // console.log(data.groups);
      const user = new User();
      user.setValues(data);
      // console.log(user.groups);
      this.myUser = user;
    });
  }
}
