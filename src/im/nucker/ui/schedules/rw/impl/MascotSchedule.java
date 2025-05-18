package im.nucker.ui.schedules.rw.impl;

import im.nucker.ui.schedules.rw.Schedule;
import im.nucker.ui.schedules.rw.TimeType;

public class MascotSchedule
        extends Schedule {
    @Override
    public String getName() {
        return "Талисман";
    }

    @Override
    public TimeType[] getTimes() {
        return new TimeType[]{TimeType.NINETEEN_HALF};
    }
}
