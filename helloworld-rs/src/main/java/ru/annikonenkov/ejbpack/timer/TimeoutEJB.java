package ru.annikonenkov.ejbpack.timer;

import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Singleton
@Startup
public class TimeoutEJB {
    private final static Logger log = Logger.getLogger(TimeoutEJB.class.getName());

    @Resource
    private TimerService tm;

    @Timeout
    public void doTimeout() {
        Date currentTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        log.info("TimeoutEJB.doTimeout() invoked at " + simpleDateFormat.format(currentTime));
    }

    @PostConstruct
    public void doStartup() {
        ScheduleExpression se = new ScheduleExpression();
        se.hour("*").minute("*").second("*/60");// через каждые 2 секунды будет выполнятся метод doTimeout()
        tm.createCalendarTimer(se, new TimerConfig("Hello TimerService", true));// создание таймера.
    }

    @PreDestroy
    public void doDestroy() {
        for (Timer timer : tm.getTimers()) {
            log.info("timer destroy" + timer.getInfo());
            timer.cancel();// уничножение таймера
        }
    }

}