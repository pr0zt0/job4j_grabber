package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) throws Exception {
        String pathProperties = "src/main/resources/rabbit.properties";
        int interval = ReadProperties.readInterval(pathProperties, o -> o.toString().contains("rabbit.interval"));
        Class.forName(ReadProperties.readConnection(pathProperties, o -> o.toString().contains("jdbc.driver")));
        try (Connection cn = DriverManager.getConnection(
                ReadProperties.readConnection(pathProperties, o -> o.toString().contains("jdbc.url")),
                ReadProperties.readConnection(pathProperties, o -> o.toString().contains("jdbc.login")),
                ReadProperties.readConnection(pathProperties, o -> o.toString().contains("jdbc.password"))
        )) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("con", cn);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(5)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();

        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("con");
            try (PreparedStatement statement = cn.prepareStatement(
                    "insert into \"html_parser\".rabbit(created_date) values(?)"
            )) {
                statement.setDate(1, Date.valueOf(LocalDate.now()));
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}