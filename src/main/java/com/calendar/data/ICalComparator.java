package com.calendar.data;

import java.util.Comparator;

/**
 * Created by NAVER on 2017-08-07.
 */
public class ICalComparator implements Comparator<ICalFilteredEvent> {

    @Override
    public int compare(ICalFilteredEvent a, ICalFilteredEvent b) {
        if (a.getIsAnniversary() > b.getIsAnniversary()) return -1;
        else if (a.getIsAnniversary() < b.getIsAnniversary()) return 1;
        else if (a.getIndex() < b.getIndex()) return -1;
        else if (a.getIndex() > b.getIndex()) return 1;
        else if (a.getPeriod() > b.getPeriod()) return -1;
        else if (a.getPeriod() < b.getPeriod()) return 1;
        else if (a.getStartHour() < b.getStartHour()) return -1;
        else if (a.getStartHour() > b.getStartHour()) return 1;
        return 0;
    }
}
