package tamk.ohsyte;

import java.time.MonthDay;
import java.time.Year;

public class DateCategoryFilter extends EventFilter {

    Category cat;
    int filterSwitch = 0;


    public DateCategoryFilter(MonthDay monthday, Category category) {

        this.monthday = monthday;
        this.year = Year.now().getValue();
        this.filterSwitch = 1;
        this.cat = category;

    }

    public DateCategoryFilter(MonthDay monthday, int year, Category category) {

        this.monthday = monthday;
        this.year = year;
        this.filterSwitch = 2;
        this.cat = category;

    }

    @Override
    public boolean accepts(Event event) {

        if (this.filterSwitch == 1) {
            if (this.monthday.equals(event.getMonthDay())) {
                if (this.cat.toString().equals(event.getCategory().toString())) {
                    return true;
                } else {
                    return false;
                }

            } else {
                return false;
            }
        } else {
            if (event instanceof SingularEvent se) {
                if (this.monthday.equals(se.getMonthDay()) && this.year == se.getYear()) {
                   if (this.cat.toString().equals(event.getCategory().toString())) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

    }
}
