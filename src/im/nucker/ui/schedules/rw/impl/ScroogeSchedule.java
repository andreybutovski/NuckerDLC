package im.nucker.ui.schedules.rw.impl;

import im.nucker.ui.schedules.rw.Schedule;
import im.nucker.ui.schedules.rw.TimeType;

public class ScroogeSchedule
        extends Schedule {
    @Override
    public String getName() {
        return "Скрудж";
    }

    @Override
    public TimeType[] getTimes() {
        return new TimeType[]{TimeType.FIFTEEN_HALF};
    }
}
