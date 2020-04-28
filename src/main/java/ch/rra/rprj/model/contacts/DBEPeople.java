package ch.rra.rprj.model.contacts;

import ch.rra.rprj.model.core.DBEObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

/*
mysql> desc rprj_people;
+-------------------+--------------+------+-----+---------------------+-------+
| Field             | Type         | Null | Key | Default             | Extra |
+-------------------+--------------+------+-----+---------------------+-------+
| id                | varchar(16)  | NO   | PRI | NULL                |       |
| owner             | varchar(16)  | NO   | MUL | NULL                |       |
| group_id          | varchar(16)  | NO   | MUL | NULL                |       |
| permissions       | char(9)      | NO   |     | rwx------           |       |
| creator           | varchar(16)  | NO   | MUL | NULL                |       |
| creation_date     | datetime     | YES  |     | NULL                |       |
| last_modify       | varchar(16)  | NO   | MUL | NULL                |       |
| last_modify_date  | datetime     | YES  |     | NULL                |       |
| deleted_by        | varchar(16)  | YES  | MUL | NULL                |       |
| deleted_date      | datetime     | NO   |     | 0000-00-00 00:00:00 |       |
| father_id         | varchar(16)  | YES  | MUL | NULL                |       |
| name              | varchar(255) | NO   |     | NULL                |       |
| description       | text         | YES  |     | NULL                |       |

| street            | varchar(255) | YES  |     | NULL                |       |
| zip               | varchar(255) | YES  |     | NULL                |       |
| city              | varchar(255) | YES  |     | NULL                |       |
| state             | varchar(255) | YES  |     | NULL                |       |
| fk_countrylist_id | varchar(16)  | YES  | MUL | NULL                |       |
| fk_companies_id   | varchar(16)  | YES  | MUL | NULL                |       |
| fk_users_id       | varchar(16)  | YES  | MUL | NULL                |       |
| phone             | varchar(255) | YES  |     | NULL                |       |
| office_phone      | varchar(255) | YES  |     | NULL                |       |
| mobile            | varchar(255) | YES  |     | NULL                |       |
| fax               | varchar(255) | YES  |     | NULL                |       |
| email             | varchar(255) | YES  |     | NULL                |       |
| url               | varchar(255) | YES  |     | NULL                |       |
| codice_fiscale    | varchar(20)  | YES  |     | NULL                |       |
| p_iva             | varchar(16)  | YES  |     | NULL                |       |
+-------------------+--------------+------+-----+---------------------+-------+
*/

@Entity
@Table(name="rprj_people")
public class DBEPeople extends DBEObject {

    @Column(name = "street", columnDefinition = "VARCHAR(255)")
    private String street;

    @Column(name = "zip", columnDefinition = "VARCHAR(255)")
    private String zip;

    @Column(name = "city", columnDefinition = "VARCHAR(255)")
    private String city;

    @Column(name = "state", columnDefinition = "VARCHAR(255)")
    private String state;

    @Column(name = "fk_countrylist_id", columnDefinition = "VARCHAR(16)")
    private String fk_countrylist_id;

    @Column(name = "fk_companies_id", columnDefinition = "VARCHAR(16)")
    private String fk_companies_id;

    @Column(name = "fk_users_id", columnDefinition = "VARCHAR(16)")
    private String fk_users_id;

    @Column(name = "phone", columnDefinition = "VARCHAR(255)")
    private String phone;

    @Column(name = "office_phone", columnDefinition = "VARCHAR(255)")
    private String office_phone;

    @Column(name = "mobile", columnDefinition = "VARCHAR(255)")
    private String mobile;

    @Column(name = "fax", columnDefinition = "VARCHAR(255)")
    private String fax;

    @Column(name = "email", columnDefinition = "VARCHAR(255)")
    private String email;

    @Column(name = "url", columnDefinition = "VARCHAR(255)")
    private String url;

    @Column(name = "codice_fiscale", columnDefinition = "VARCHAR(20)")
    private String codice_fiscale;

    @Column(name = "p_iva", columnDefinition = "VARCHAR(16)")
    private String p_iva;

    public DBEPeople() {}

    public DBEPeople(String name, String description) {
        super(name,description);
    }

    public DBEPeople(String id, String owner, String group_id, String permissions, String creator, Timestamp creation_date, String last_modify, Timestamp last_modify_date, String deleted_by, Timestamp deleted_date, String father_id, String name, String description, String street, String zip, String city, String state, String fk_countrylist_id, String fk_companies_id, String fk_users_id, String phone, String office_phone, String mobile, String fax, String email, String url, String codice_fiscale, String p_iva) {
        super(id, owner, group_id, permissions, creator, creation_date, last_modify, last_modify_date, deleted_by, deleted_date, father_id, name, description);
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

    public String getIcon() { return "glyphicon-user"; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getFk_countrylist_id() { return fk_countrylist_id; }
    public void setFk_countrylist_id(String fk_countrylist_id) { this.fk_countrylist_id = fk_countrylist_id; }

    public String getFk_companies_id() { return fk_companies_id; }
    public void setFk_companies_id(String fk_companies_id) { this.fk_companies_id = fk_companies_id; }

    public String getFk_users_id() { return fk_users_id; }
    public void setFk_users_id(String fk_users_id) { this.fk_users_id = fk_users_id; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getOffice_phone() { return office_phone; }
    public void setOffice_phone(String office_phone) { this.office_phone = office_phone; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getCodice_fiscale() { return codice_fiscale; }
    public void setCodice_fiscale(String codice_fiscale) { this.codice_fiscale = codice_fiscale; }

    public String getP_iva() { return p_iva; }
    public void setP_iva(String p_iva) { this.p_iva = p_iva; }

    @Override
    public String toString() {
        return "DBEPeople{" +
                "id='" + id + '\'' +
                (owner!=null ? ", owner='" + owner + '\'' : "") +
                (group_id!=null ? ", group_id='" + group_id + '\'' : "") +
                (permissions!=null ? ", permissions='" + permissions + '\'' : "") +
                (creator!=null ? ", creator='" + creator + '\'' : "") +
                (creation_date!=null ? ", creation_date=" + creation_date : "") +
                (last_modify!=null ? ", last_modify='" + last_modify + '\'' : "") +
                (last_modify_date!=null ? ", last_modify_date=" + last_modify_date : "") +
                (deleted_by!=null && deleted_by.length()>0 ?
                        ", deleted_by='" + deleted_by + '\'' +
                                ", deleted_date=" + deleted_date
                        : "") +
                (father_id!=null ? ", father_id='" + father_id + '\'' : "") +
                ", name='" + name + '\'' +
                (description!=null ? ", description='" + description + '\'' : "") +

                (street!=null ? ", street='" + street + '\'' : "") +
                (zip!=null ? ", zip='" + zip + '\'' : "") +
                (city!=null ? ", city='" + city + '\'' : "") +
                (state!=null ? ", state='" + state + '\'' : "") +
                (fk_countrylist_id!=null ? ", fk_countrylist_id='" + fk_countrylist_id + '\'' : "") +
                (fk_companies_id!=null ? ", fk_companies_id='" + fk_companies_id + '\'' : "") +
                (fk_users_id!=null ? ", fk_users_id='" + fk_users_id + '\'' : "") +
                (phone!=null ? ", phone='" + phone + '\'' : "") +
                (office_phone!=null ? ", office_phone='" + office_phone + '\'' : "") +
                (mobile!=null ? ", mobile='" + mobile + '\'' : "") +
                (fax!=null ? ", fax='" + fax + '\'' : "") +
                (email!=null ? ", email='" + email + '\'' : "") +
                (url!=null ? ", url='" + url + '\'' : "") +
                (codice_fiscale!=null ? ", codice_fiscale='" + codice_fiscale + '\'' : "") +
                (p_iva!=null ? ", p_iva='" + p_iva + '\'' : "") +
                '}';
    }
}
