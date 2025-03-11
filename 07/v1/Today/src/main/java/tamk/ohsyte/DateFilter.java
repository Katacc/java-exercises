package tamk.ohsyte;

import java.time.MonthDay;
import java.time.Year;



public class DateFilter extends EventFilter {

    /*
     filterSwitch:
     1: month and day
     2: month, day, year
     */
    private int filterSwitch;

    public DateFilter(MonthDay monthday) {

        this.monthday = monthday;
        this.year = Year.now().getValue();
        this.filterSwitch = 1;

    }

    public DateFilter(MonthDay monthday, int year) {

        this.monthday = monthday;
        this.year = year;
        this.filterSwitch = 2;

    }

    @Override
    public boolean accepts(Event event) {


        if (this.filterSwitch == 1) {
            if (this.monthday.equals(event.getMonthDay())) {
                return true;
            } else {
                return false;
            }
        } else {
            if (event instanceof SingularEvent se) {
                if (this.monthday.equals(se.getMonthDay()) && this.year == se.getYear()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }


        // if (event instanceof SingularEvent se) {
        //     if (this.monthday.equals(se.getMonthDay()) && this.year == se.getYear()) {
        //         return true;
        //     } else {
        //         return false;
        //     }
        // } else {
        //     if (this.monthday.equals(event.getMonthDay())) {
        //         return true;
        //     } else {
        //         return false;
        //     }
        // }


    }
}
