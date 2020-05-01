/* tslint:disable:variable-name */

export class DBEntity {
  _class = 'DBEntity';
  _icon = 'glyphicon-cog';

  constructor() {}

  setValues(o: object) {
    for (const [key, value] of Object.entries(o)) { this[key] = value; }
  }

  toString(): string {
    let ret = this._class + '{';
    for (const [key, value] of Object.entries(this)) {
      ret += key + ': \'' + value + '\', ';
    }
    return ret + '}';
  }

  /* View stuff: start. */
  renderView(): string { return 'nope'; }
  /* View stuff: end. */
}

export class DBEObject extends DBEntity {
  _class = 'DBEObject';
  _icon = 'glyphicon-cog';

  id?: string = null;
  owner?: string = null;
  group_id?: string = null;
  permissions?: string = null;
  creator?: string = null;
  creation_date?: string = null;
  last_modify?: string = null;
  last_modify_date?: string = null;
  deleted_by?: string = null;
  deleted_date?: string = null;
  father_id?: string = null;
  name?: string = null;
  description?: string = null;

  constructor(id?: string, owner?: string, group_id?: string, permissions?: string, creator?: string,
              creation_date?: string, last_modify?: string, last_modify_date?: string,
              deleted_by?: string, deleted_date?: string, father_id?: string, name?: string,
              description?: string) {
    super();
    this.id = id;
    this.owner = owner;
    this.group_id = group_id;
    this.permissions = permissions;
    this.creator = creator;
    this.creation_date = creation_date;
    this.last_modify = last_modify;
    this.last_modify_date = last_modify_date;
    this.deleted_by = deleted_by;
    this.deleted_date = deleted_date;
    this.father_id = father_id;
    this.name = name;
    this.description = description;
  }

  canRead(x: string = ''): boolean {
    switch (x) {
      case 'U':
        return this.permissions[0] === 'r';
      case 'G':
        return this.permissions[3] === 'r';
      default:
        return this.permissions[6] === 'r';
    }
  }
  canWrite(x: string = ''): boolean {
    switch (x) {
      case 'U':
        return this.permissions[1] === 'w';
      case 'G':
        return this.permissions[4] === 'w';
      default:
        return this.permissions[7] === 'w';
    }
  }
  canExecute(x: string = ''): boolean {
    switch (x) {
      case 'U':
        return this.permissions[2] === 'x';
      case 'G':
        return this.permissions[5] === 'x';
      default:
        return this.permissions[8] === 'x';
    }
  }
}

/* **** Core **** */

export class User extends DBEntity {
  _class = 'User';
  _icon = 'glyphicon-user';

  id?: string = null;
  login?: string = null;
  pwd?: string = null;
  pwd_salt?: string = null;
  fullname?: string = null;
  group_id?: string = null;
  groups?: any[] = null;

  constructor(id?: string, login?: string, pwd?: string, pwd_salt?: string, fullname?: string,
              group_id?: string, groups?: any[]) {
    super();
    this.id = id;
    this.login = login;
    this.pwd = pwd;
    this.pwd_salt = pwd_salt;
    this.fullname = fullname;
    this.group_id = group_id;
    this.groups = groups;
  }

  hasGroup(group_id: string): boolean {
    let ret = false;
    for (const i in this.groups) {
      const g = this.groups[i];
      // console.log('Group: ' + g.id + '=>' + (g.id === group_id));
      if (g.id === group_id) {
        ret = true;
        break;
      }
    }
    return ret;
  }
}

export class Group extends DBEntity {
  _class = 'Group';
  _icon = 'glyphicon-asterisk';

  id?: string = null;
  name?: string = null;
  description?: string = null;
  users?: any[] = null;

  constructor(id?: string, name?: string, description?: string, users?: any[]) {
    super();
    this.id = id;
    this.name = name;
    this.description = description;
    this.users = users;
  }
}

export class DBELog extends DBEntity {
  _class = 'DBELog';
  _icon = 'glyphicon-cog';

  ip?: string = null;
  data?: string = null;
  ora?: string = null;
  count?: number = null;
  url?: string = null;
  note?: string = null;
  note2?: string = null;

  constructor(ip?: string, data?: string, ora?: string, count?: number, url?: string, note?: string,
              note2?: string) {
    super();
    this.ip = ip;
    this.data = data;
    this.ora = ora;
    this.count = count;
    this.url = url;
    this.note = note;
    this.note2 = note2;
  }
}

export class DBEDBVersion extends DBEntity {
  _class = 'DBEDBVersion';
  _icon = 'glyphicon-cog';

  model_name?: string = null;
  version?: number = null;

  constructor(model_name?: string, version?: number) {
    super();
    this.model_name = model_name;
    this.version = version;
  }
}

/* **** Contacts **** */
export class DBECompany extends DBEObject {
  _class = 'DBECompany';
  _icon = 'glyphicon-circle-arrow-right';

  street?: string = null;
  zip?: string = null;
  city?: string = null;
  state?: string = null;
  fk_countrylist_id?: string = null;
  phone?: string = null;
  fax?: string = null;
  email?: string = null;
  url?: string = null;
  p_iva?: string = null;

  constructor(id?: string, owner?: string, group_id?: string, permissions?: string, creator?: string,
              creation_date?: string, last_modify?: string, last_modify_date?: string,
              deleted_by?: string, deleted_date?: string, father_id?: string, name?: string,
              description?: string, street?: string, zip?: string, city?: string, state?: string,
              fk_countrylist_id?: string, phone?: string, fax?: string, email?: string, url?: string,
              p_iva?: string) {
    super(id, owner, group_id, permissions, creator, creation_date, last_modify, last_modify_date,
      deleted_by, deleted_date, father_id, name, description);
    // DBECompany
    this.street = street;
    this.zip = zip;
    this.city = city;
    this.state = state;
    this.fk_countrylist_id = fk_countrylist_id;
    this.phone = phone;
    this.fax = fax;
    this.email = email;
    this.url = url;
    this.p_iva = p_iva;
  }
}

export class DBECountry extends DBEntity {
  _class = 'DBECountry';
  _icon = 'glyphicon-globe';

  id?: string = null;
  Common_Name?: string = null;
  Formal_Name?: string = null;
  Type?: string = null;
  Sub_Type?: string = null;
  Sovereignty?: string = null;
  Capital?: string = null;
  ISO_4217_Currency_Code?: string = null;
  ISO_4217_Currency_Name?: string = null;
  ITU_T_Telephone_Code?: string = null;
  ISO_3166_1_2_Letter_Code?: string = null;
  ISO_3166_1_3_Letter_Code?: string = null;
  ISO_3166_1_Number?: string = null;
  IANA_Country_Code_TLD?: string = null;

  constructor(id?: string, Common_Name?: string, Formal_Name?: string, Type?: string, Sub_Type?: string,
              Sovereignty?: string, Capital?: string, ISO_4217_Currency_Code?: string,
              ISO_4217_Currency_Name?: string, ITU_T_Telephone_Code?: string,
              ISO_3166_1_2_Letter_Code?: string, ISO_3166_1_3_Letter_Code?: string,
              ISO_3166_1_Number?: string, IANA_Country_Code_TLD?: string) {
    super();
    this.id = id;
    this.Common_Name = Common_Name;
    this.Formal_Name = Formal_Name;
    this.Type = Type;
    this.Sub_Type = Sub_Type;
    this.Sovereignty = Sovereignty;
    this.Capital = Capital;
    this.ISO_4217_Currency_Code = ISO_4217_Currency_Code;
    this.ISO_4217_Currency_Name = ISO_4217_Currency_Name;
    this.ITU_T_Telephone_Code = ITU_T_Telephone_Code;
    this.ISO_3166_1_2_Letter_Code = ISO_3166_1_2_Letter_Code;
    this.ISO_3166_1_3_Letter_Code = ISO_3166_1_3_Letter_Code;
    this.ISO_3166_1_Number = ISO_3166_1_Number;
    this.IANA_Country_Code_TLD = IANA_Country_Code_TLD;
  }
}

export class DBEPeople extends DBEObject {
  _class = 'DBEPeople';
  _icon = 'glyphicon-user';

  street?: string = null;
  zip?: string = null;
  city?: string = null;
  state?: string = null;
  fk_countrylist_id?: string = null;
  fk_companies_id?: string = null;
  fk_users_id?: string = null;
  phone?: string = null;
  office_phone?: string = null;
  mobile?: string = null;
  fax?: string = null;
  email?: string = null;
  url?: string = null;
  codice_fiscale?: string = null;
  p_iva?: string = null;

  constructor(id?: string, owner?: string, group_id?: string, permissions?: string, creator?: string,
              creation_date?: string, last_modify?: string, last_modify_date?: string,
              deleted_by?: string, deleted_date?: string, father_id?: string, name?: string,
              description?: string, street?: string, zip?: string, city?: string, state?: string,
              fk_countrylist_id?: string, fk_companies_id?: string, fk_users_id?: string,
              phone?: string, office_phone?: string, mobile?: string, fax?: string, email?: string,
              url?: string, codice_fiscale?: string, p_iva?: string) {
    super(id, owner, group_id, permissions, creator, creation_date, last_modify, last_modify_date,
      deleted_by, deleted_date, father_id, name, description);
    // DBEPeople
    this.street = street;
    this.zip = zip;
    this.city = city;
    this.state = state;
    this.fk_countrylist_id = fk_countrylist_id;
    this.fk_companies_id = fk_companies_id;
    this.fk_users_id = fk_users_id;
    this.phone = phone;
    this.office_phone = office_phone;
    this.mobile = mobile;
    this.fax = fax;
    this.email = email;
    this.url = url;
    this.codice_fiscale = codice_fiscale;
    this.p_iva = p_iva;
  }
}

/* **** CMS **** */
export class DBEFolder extends DBEObject {
  _class = 'DBEFolder';
  _icon = 'glyphicon-folder-close';

  fk_obj_id?: string = null;
  childs_sort_order?: string = null;

  constructor(id?: string, owner?: string, group_id?: string, permissions?: string, creator?: string,
              creation_date?: string, last_modify?: string, last_modify_date?: string,
              deleted_by?: string, deleted_date?: string, father_id?: string, name?: string,
              description?: string, fk_obj_id?: string, childs_sort_order?: string) {
    super(id, owner, group_id, permissions, creator, creation_date, last_modify, last_modify_date,
      deleted_by, deleted_date, father_id, name, description);
    // DBEFolder
    this.fk_obj_id = fk_obj_id;
    this.childs_sort_order = childs_sort_order;
  }
}

export class DBELink extends DBEObject {
  _class = 'DBELink';
  _icon = 'glyphicon-link';

  href?: string = null;
  target?: string = null;
  fk_obj_id?: string = null;

  constructor(id?: string, owner?: string, group_id?: string, permissions?: string, creator?: string,
              creation_date?: string, last_modify?: string, last_modify_date?: string,
              deleted_by?: string, deleted_date?: string, father_id?: string, name?: string,
              description?: string, href?: string, target?: string, fk_obj_id?: string) {
    super(id, owner, group_id, permissions, creator, creation_date, last_modify, last_modify_date,
      deleted_by, deleted_date, father_id, name, description);
    // DBELink
    this.href = href;
    this.target = target;
    this.fk_obj_id = fk_obj_id;
  }
}

export class DBENews extends DBEObject {
  _class = 'DBENews';
  _icon = 'glyphicon-arrow-right';

  html?: string = null;
  fk_obj_id?: string = null;
  language?: string = null;

  constructor(id?: string, owner?: string, group_id?: string, permissions?: string, creator?: string,
              creation_date?: string, last_modify?: string, last_modify_date?: string,
              deleted_by?: string, deleted_date?: string, father_id?: string, name?: string,
              description?: string, html?: string, fk_obj_id?: string, language?: string) {
    super(id, owner, group_id, permissions, creator, creation_date, last_modify, last_modify_date,
      deleted_by, deleted_date, father_id, name, description);
    // DBENews
    this.html = html;
    this.fk_obj_id = fk_obj_id;
    this.language = language;
  }

  renderView(): string { return this.html; }
}

export class DBENote extends DBEObject {
  _class = 'DBENote';
  _icon = 'glyphicon-circle-arrow-right';

  fk_obj_id?: string = null;

  constructor(id?: string, owner?: string, group_id?: string, permissions?: string, creator?: string,
              creation_date?: string, last_modify?: string, last_modify_date?: string,
              deleted_by?: string, deleted_date?: string, father_id?: string, name?: string,
              description?: string, fk_obj_id?: string) {
    super(id, owner, group_id, permissions, creator, creation_date, last_modify, last_modify_date,
      deleted_by, deleted_date, father_id, name, description);
    // DBENote
    this.fk_obj_id = fk_obj_id;
  }

  renderView(): string {
    console.log('DBENote.renderView');
    let ret = '<div class="form_note">';
    ret += '<div class="form_field_name">' + this.name + '</div>';
    ret += '<div class="form_field_description">' + this.description.replace('\n', '<br>') + '</div>';
    ret += '</div>';
    return ret;
  }
}

export class DBEPage extends DBEObject {
  _class = 'DBEPage';
  _icon = 'glyphicon-file';

  html?: string = null;
  fk_obj_id?: string = null;
  language?: string = null;

  constructor(id?: string, owner?: string, group_id?: string, permissions?: string, creator?: string,
              creation_date?: string, last_modify?: string, last_modify_date?: string,
              deleted_by?: string, deleted_date?: string, father_id?: string, name?: string,
              description?: string, html?: string, fk_obj_id?: string, language?: string) {
    super(id, owner, group_id, permissions, creator, creation_date, last_modify, last_modify_date,
      deleted_by, deleted_date, father_id, name, description);
    // DBEPage
    this.html = html;
    this.fk_obj_id = fk_obj_id;
    this.language = language;
  }

  renderView(): string { return this.html; }
}
