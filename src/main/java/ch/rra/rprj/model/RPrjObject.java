package ch.rra.rprj.model;




/*
mysql> desc rprj_objects;
        +------------------+--------------+------+-----+---------------------+-------+
        | Field            | Type         | Null | Key | Default             | Extra |
        +------------------+--------------+------+-----+---------------------+-------+
        | id               | varchar(16)  | NO   | PRI | NULL                |       |
        | owner            | varchar(16)  | NO   | MUL | NULL                |       |
        | group_id         | varchar(16)  | NO   | MUL | NULL                |       |
        | permissions      | char(9)      | NO   |     | rwx------           |       |
        | creator          | varchar(16)  | NO   | MUL | NULL                |       |
        | creation_date    | datetime     | YES  |     | NULL                |       |
        | last_modify      | varchar(16)  | NO   | MUL | NULL                |       |
        | last_modify_date | datetime     | YES  |     | NULL                |       |
        | deleted_by       | varchar(16)  | YES  | MUL | NULL                |       |
        | deleted_date     | datetime     | NO   |     | 0000-00-00 00:00:00 |       |
        | father_id        | varchar(16)  | YES  | MUL | NULL                |       |
        | name             | varchar(255) | NO   |     | NULL                |       |
        | description      | text         | YES  |     | NULL                |       |
        +------------------+--------------+------+-----+---------------------+-------+
        13 rows in set (0.01 sec)
*/
public class RPrjObject {
}
