package ch.rra.rprj.model.contacts;


import ch.rra.rprj.model.core.DBEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/*
mysql> desc rprj_countrylist;
+--------------------------+--------------+------+-----+---------+-------+
| Field                    | Type         | Null | Key | Default | Extra |
+--------------------------+--------------+------+-----+---------+-------+
| id                       | varchar(16)  | NO   | PRI | NULL    |       |
| Common_Name              | varchar(255) | YES  |     | NULL    |       |
| Formal_Name              | varchar(255) | YES  |     | NULL    |       |
| Type                     | varchar(255) | YES  |     | NULL    |       |
| Sub_Type                 | varchar(255) | YES  |     | NULL    |       |
| Sovereignty              | varchar(255) | YES  |     | NULL    |       |
| Capital                  | varchar(255) | YES  |     | NULL    |       |
| ISO_4217_Currency_Code   | varchar(255) | YES  |     | NULL    |       |
| ISO_4217_Currency_Name   | varchar(255) | YES  |     | NULL    |       |
| ITU_T_Telephone_Code     | varchar(255) | YES  |     | NULL    |       |
| ISO_3166_1_2_Letter_Code | varchar(255) | YES  |     | NULL    |       |
| ISO_3166_1_3_Letter_Code | varchar(255) | YES  |     | NULL    |       |
| ISO_3166_1_Number        | varchar(255) | YES  |     | NULL    |       |
| IANA_Country_Code_TLD    | varchar(255) | YES  |     | NULL    |       |
+--------------------------+--------------+------+-----+---------+-------+
*/

@Entity
@Table(name="rprj_countrylist")
public class DBECountry extends DBEntity {
    @Id
    @GeneratedValue(generator="UUID")
    @GenericGenerator(
            name="UUID",
            strategy="ch.rra.rprj.model.core.IdGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(16)")
    protected String id;

    @Column(name = "Common_Name", columnDefinition = "VARCHAR(255)")
    protected String Common_Name;

    @Column(name = "Formal_Name", columnDefinition = "VARCHAR(255)")
    protected String Formal_Name;

    @Column(name = "Type", columnDefinition = "VARCHAR(255)")
    protected String Type;

    @Column(name = "Sub_Type", columnDefinition = "VARCHAR(255)")
    protected String Sub_Type;

    @Column(name = "Sovereignty", columnDefinition = "VARCHAR(255)")
    protected String Sovereignty;

    @Column(name = "Capital", columnDefinition = "VARCHAR(255)")
    protected String Capital;

    @Column(name = "ISO_4217_Currency_Code", columnDefinition = "VARCHAR(255)")
    protected String ISO_4217_Currency_Code;

    @Column(name = "ISO_4217_Currency_Name", columnDefinition = "VARCHAR(255)")
    protected String ISO_4217_Currency_Name;

    @Column(name = "ITU_T_Telephone_Code", columnDefinition = "VARCHAR(255)")
    protected String ITU_T_Telephone_Code;

    @Column(name = "ISO_3166_1_2_Letter_Code", columnDefinition = "VARCHAR(255)")
    protected String ISO_3166_1_2_Letter_Code;

    @Column(name = "ISO_3166_1_3_Letter_Code", columnDefinition = "VARCHAR(255)")
    protected String ISO_3166_1_3_Letter_Code;

    @Column(name = "ISO_3166_1_Number", columnDefinition = "VARCHAR(255)")
    protected String ISO_3166_1_Number;

    @Column(name = "IANA_Country_Code_TLD", columnDefinition = "VARCHAR(255)")
    protected String IANA_Country_Code_TLD;

    public DBECountry() {
    }

    public DBECountry(String common_Name, String formal_Name, String type, String sub_Type, String sovereignty, String capital, String ISO_4217_Currency_Code, String ISO_4217_Currency_Name, String ITU_T_Telephone_Code, String ISO_3166_1_2_Letter_Code, String ISO_3166_1_3_Letter_Code, String ISO_3166_1_Number, String IANA_Country_Code_TLD) {
        Common_Name = common_Name;
        Formal_Name = formal_Name;
        Type = type;
        Sub_Type = sub_Type;
        Sovereignty = sovereignty;
        Capital = capital;
        this.ISO_4217_Currency_Code = ISO_4217_Currency_Code;
        this.ISO_4217_Currency_Name = ISO_4217_Currency_Name;
        this.ITU_T_Telephone_Code = ITU_T_Telephone_Code;
        this.ISO_3166_1_2_Letter_Code = ISO_3166_1_2_Letter_Code;
        this.ISO_3166_1_3_Letter_Code = ISO_3166_1_3_Letter_Code;
        this.ISO_3166_1_Number = ISO_3166_1_Number;
        this.IANA_Country_Code_TLD = IANA_Country_Code_TLD;
    }

    public String getIcon() { return "glyphicon-file"; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCommon_Name() { return Common_Name; }
    public void setCommon_Name(String common_Name) { Common_Name = common_Name; }

    public String getFormal_Name() { return Formal_Name; }
    public void setFormal_Name(String formal_Name) { Formal_Name = formal_Name; }

    public String getType() { return Type; }
    public void setType(String type) { Type = type; }

    public String getSub_Type() { return Sub_Type; }
    public void setSub_Type(String sub_Type) { Sub_Type = sub_Type; }

    public String getSovereignty() { return Sovereignty; }
    public void setSovereignty(String sovereignty) { Sovereignty = sovereignty; }

    public String getCapital() { return Capital; }
    public void setCapital(String capital) { Capital = capital; }

    public String getISO_4217_Currency_Code() { return ISO_4217_Currency_Code; }
    public void setISO_4217_Currency_Code(String ISO_4217_Currency_Code) { this.ISO_4217_Currency_Code = ISO_4217_Currency_Code; }

    public String getISO_4217_Currency_Name() { return ISO_4217_Currency_Name; }
    public void setISO_4217_Currency_Name(String ISO_4217_Currency_Name) { this.ISO_4217_Currency_Name = ISO_4217_Currency_Name; }

    public String getITU_T_Telephone_Code() { return ITU_T_Telephone_Code; }
    public void setITU_T_Telephone_Code(String ITU_T_Telephone_Code) { this.ITU_T_Telephone_Code = ITU_T_Telephone_Code; }

    public String getISO_3166_1_2_Letter_Code() { return ISO_3166_1_2_Letter_Code; }
    public void setISO_3166_1_2_Letter_Code(String ISO_3166_1_2_Letter_Code) { this.ISO_3166_1_2_Letter_Code = ISO_3166_1_2_Letter_Code; }

    public String getISO_3166_1_3_Letter_Code() { return ISO_3166_1_3_Letter_Code; }
    public void setISO_3166_1_3_Letter_Code(String ISO_3166_1_3_Letter_Code) { this.ISO_3166_1_3_Letter_Code = ISO_3166_1_3_Letter_Code; }

    public String getISO_3166_1_Number() { return ISO_3166_1_Number; }
    public void setISO_3166_1_Number(String ISO_3166_1_Number) { this.ISO_3166_1_Number = ISO_3166_1_Number; }

    public String getIANA_Country_Code_TLD() { return IANA_Country_Code_TLD; }
    public void setIANA_Country_Code_TLD(String IANA_Country_Code_TLD) { this.IANA_Country_Code_TLD = IANA_Country_Code_TLD; }

    @Override
    public String toString() {
        return "DBECountry{" +
                "id='" + id + '\'' +
                (Common_Name!=null ? ", Common_Name='" + Common_Name + '\'' : "") +
                (Formal_Name!=null ? ", Formal_Name='" + Formal_Name + '\'' : "") +
                (Type!=null ? ", Type='" + Type + '\'' : "") +
                (Sub_Type!=null ? ", Sub_Type='" + Sub_Type + '\'' : "") +
                (Sovereignty!=null ? ", Sovereignty='" + Sovereignty + '\'' : "") +
                (Capital!=null ? ", Capital='" + Capital + '\'' : "") +
                (ISO_4217_Currency_Code!=null ? ", ISO_4217_Currency_Code='" + ISO_4217_Currency_Code + '\'' : "") +
                (ISO_4217_Currency_Name!=null ? ", ISO_4217_Currency_Name='" + ISO_4217_Currency_Name + '\'' : "") +
                (ITU_T_Telephone_Code!=null ? ", ITU_T_Telephone_Code='" + ITU_T_Telephone_Code + '\'' : "") +
                (ISO_3166_1_2_Letter_Code!=null ? ", ISO_3166_1_2_Letter_Code='" + ISO_3166_1_2_Letter_Code + '\'' : "") +
                (ISO_3166_1_3_Letter_Code!=null ? ", ISO_3166_1_3_Letter_Code='" + ISO_3166_1_3_Letter_Code + '\'' : "") +
                (ISO_3166_1_Number!=null ? ", ISO_3166_1_Number='" + ISO_3166_1_Number + '\'' : "") +
                (IANA_Country_Code_TLD!=null ? ", IANA_Country_Code_TLD='" + IANA_Country_Code_TLD + '\'' : "") +
                '}';
    }
}
