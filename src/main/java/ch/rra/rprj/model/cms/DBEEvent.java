package ch.rra.rprj.model.cms;

import ch.rra.rprj.model.ObjectMgr;
import ch.rra.rprj.model.core.DBEObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.sql.Timestamp;

/*
class DBEEvent extends DBEObject {
	var $_typeName="DBEEvent";
	function getTableName() { return "events"; }

	function getFK() {
		if($this->_fk==null) {
			$this->_fk=array();
		}
		if(count($this->_fk)==0) {
			parent::getFK();
//			$this->_fk[] = new ForeignKey('fk_obj_id','pages','id');
//			$this->_fk[] = new ForeignKey('father_id','pages','id');

			$this->_fk[] = new ForeignKey('fk_obj_id','companies','id');
			$this->_fk[] = new ForeignKey('fk_obj_id','folders','id');
			$this->_fk[] = new ForeignKey('fk_obj_id','people','id');
			$this->_fk[] = new ForeignKey('fk_obj_id','projects','id');
		}
		return $this->_fk;
	}
}
*/
@Entity
@Table(name="rprj_events")
public class DBEEvent extends DBEObject {

    @Column(name = "fk_obj_id", columnDefinition="VARCHAR(16)")
    private String fk_obj_id;
    
    @Column(name="start_date", columnDefinition="datetime")
    private Timestamp start_date;
    @Column(name="end_date", columnDefinition="datetime")
    private Timestamp end_date;
    @Column(name="all_day", columnDefinition="char(1)") //',"not null default '1'"); // Bool - An all day event?
    private char all_day;

    @Column(name="url", columnDefinition="varchar(255)") // ',"default null"); // An Url
    private String url;

    @Column(name="alarm", columnDefinition="char(1)") //',"default '0'"); // Bool - Signal an alarm before?
    private char alarm;
    @Column(name="alarm_minute", columnDefinition="int") //',"default 0"); // Num. time unit
    private Integer alarm_minute;
    @Column(name="alarm_unit", columnDefinition="char(1)") //,"default '0'"); // Time unit 0-2 => minutes, hours, days
    private char alarm_unit;
    @Column(name="before_event", columnDefinition="char(1)") //,"default '0'"); // 0=before event starts 1=after
    private char before_event;

    @Column(name="category", columnDefinition="varchar(255)") //,"default ''"); // Event category
    private String category;

    // Recurrence
    @Column(name="recurrence", columnDefinition="char(1)") //,"default '0'"); // Bool - Recurrence active?
    private char recurrence;
    @Column(name="recurrence_type", columnDefinition="char(1)") //,"default '0'"); // 0=Daily, 1=Weekly, 2=monthly, 3=yearly
    private char recurrence_type;
    // 0: daily
    @Column(name="daily_every_x", columnDefinition="int") //,"default 0"); // every_x_days
    private int daily_every_x;
////  giorno_ogni_tot_giorni INTEGER, -- Ogni quanti giorni ripetere l'evento
    // 1: weekly
    @Column(name="weekly_every_x", columnDefinition="int") //,"default 0"); // every x weeks
    private int weekly_every_x;
    @Column(name="weekly_day_of_the_week", columnDefinition="char(1)") //',"default '0'"); // 0=monday ... 6=sunday
    private char weekly_day_of_the_week;
    // 2: monthly
    @Column(name="monthly_every_x", columnDefinition="int") //',"default 0"); // every x months
    private int monthly_every_x;
    //  1) n-th day of the month
    @Column(name="monthly_day_of_the_month", columnDefinition="int") //',"default 0"); // 0=do not, -5...-1,1 ... 31
    private int monthly_day_of_the_month;
    //  2) n-th week on monday
    @Column(name="monthly_week_number", columnDefinition="int") //',"default 0"); // 0=do not, 1...5
    private int monthly_week_number;
    @Column(name="monthly_week_day", columnDefinition="char(1)") //',"default '0'"); // 0=monday ... 6=sunday
    private char monthly_week_day;
    // 3: Yearly
    //  1) every day XX of month MM
    @Column(name="yearly_month_number", columnDefinition="int") //',"default 0"); // 0=do not, 1...12
    private int yearly_month_number;
    @Column(name="yearly_month_day", columnDefinition="int") //',"default 0"); // 0=do not, 1...31
    private int yearly_month_day;
    //  2) every first monday of june
//    @Column(name="yearly_month_number", columnDefinition="int") //',"default 0"); // 0=do not, 1...12
//    private int yearly_month_number;
    @Column(name="yearly_week_number", columnDefinition="int") //',"default 0"); // 0=do not 1...5
    private int yearly_week_number;
    @Column(name="yearly_week_day", columnDefinition="char(1)") //',"default '0'"); // 0=monday ... 6=sunday
    private char yearly_week_day;
    //  3) every n-th day of the year
    @Column(name="yearly_day_of_the_year", columnDefinition="int") //',"default 0"); // 0=do not, 1...366
    private int yearly_day_of_the_year;
    // Recurrence range
    @Column(name="recurrence_times", columnDefinition="int") //',"default 0"); // 0=always 1...N times
    private int recurrence_times;
    // Recurrence until <date>
    @Column(name="recurrence_end_date", columnDefinition="datetime") // 0=always 1...N times
    private Timestamp recurrence_end_date;

    public DBEEvent() {}

    public DBEEvent(String name, String description) {
        super(name, description);
    }

    public DBEEvent(String id, String owner, String group_id, String permissions,
                     String creator, Timestamp creation_date,
                     String last_modify, Timestamp last_modify_date,
                     String deleted_by, Timestamp deleted_date,
                     String father_id,
                     String name, String description,
                     String fk_obj_id,
                     Timestamp start_date, Timestamp end_date, char all_day,
                     String url,
                     char alarm, int alarm_minute, char alarm_unit, char before_event,
                     String category,
                     char recurrence, char recurrence_type,
                     int daily_every_x,
                     int weekly_every_x, char weekly_day_of_the_week,
                     int monthly_every_x,
                     int monthly_day_of_the_month,
                     int monthly_week_number, char monthly_week_day,
                     int yearly_month_number, int yearly_month_day,
                     int yearly_week_number, char yearly_week_day,
                     int yearly_day_of_the_year,
                     int recurrence_times,
                     Timestamp recurrence_end_date
                     ) {
        super(id, owner, group_id, permissions, creator, creation_date, last_modify, last_modify_date, deleted_by, deleted_date, father_id, name, description);
        this.fk_obj_id = fk_obj_id;

        this.start_date = start_date;
        this.end_date = end_date;
        this.all_day = all_day;

        this.url = url;

        this.alarm = alarm;
        this.alarm_minute = alarm_minute;
        this.alarm_unit = alarm_unit;
        this.before_event = before_event;

        this.category = category;

        this.recurrence = recurrence;
        this.recurrence_type = recurrence_type;

        this.daily_every_x = daily_every_x;

        this.weekly_every_x = weekly_every_x;
        this.weekly_day_of_the_week = weekly_day_of_the_week;

        this.monthly_every_x = monthly_every_x;
        this.monthly_day_of_the_month = monthly_day_of_the_month;
        this.monthly_week_number = monthly_week_number;
        this.monthly_week_day = monthly_week_day;

        this.yearly_month_number = yearly_month_number;
        this.yearly_month_day = yearly_month_day;
        this.yearly_week_number = yearly_week_number;
        this.yearly_week_day = yearly_week_day;
        this.yearly_day_of_the_year = yearly_day_of_the_year;

        this.recurrence_times = recurrence_times;

        this.recurrence_end_date = recurrence_end_date;
    }

    // TODO
    public String getIcon() { return "glyphicon-calendar"; }
}
