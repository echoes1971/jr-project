export interface ObjLight {
  _icon: string;
  id: string;
  name: string;
}

export interface IDBEObject {
  _class: string;
  _icon: string;

  id: string;
  owner?: string;
  group_id?: string;
  permissions?: string;
  creator?: string;
  creation_date?: string;
  last_modify?: string;
  last_modify_date?: string;
  deleted_by?: string;
  deleted_date?: string;
  father_id?: string;
  name: string;
  description?: string;
}

export interface ObjPage extends IDBEObject {
  html: string;
}

