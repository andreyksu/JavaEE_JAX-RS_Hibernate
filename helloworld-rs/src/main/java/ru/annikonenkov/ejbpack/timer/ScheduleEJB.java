package ru.annikonenkov.ejbpack.timer;

import org.jboss.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import java.text.SimpleDateFormat;
import java.util.Date;

@Singleton
public class ScheduleEJB {
    private static Logger log = Logger.getLogger(ScheduleEJB.class.getName());

    @Schedule(second = "*/60", minute = "*", hour = "*")
    public void goSchedule() {
        Date currentTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        log.info("ScheduleEJB.goSchedule() invoked at " + simpleDateFormat.format(currentTime));
    }
}