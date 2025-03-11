package tamk.ohsyte;

import java.time.MonthDay;

public abstract class EventFilter {

    protected MonthDay monthday;
    protected int year;

    public abstract boolean accepts(Event event);

}


