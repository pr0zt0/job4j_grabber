package ru.job4j.quartz;

import java.io.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReadProperties {

    public static int readInterval(String path, Predicate predicate) throws Exception {
        int interval = -1;
        try (BufferedReader read = new BufferedReader(new FileReader(path))) {
            interval = Integer.parseInt(read.lines()
                    .filter(predicate::test)
                    .map(s -> s.split("=")[1])
                    .collect(Collectors.toList()).get(0));
            } catch (IOException e) {
            e.printStackTrace();
        }
        if (interval == -1) {
            throw new Exception("Not find rabbit.interval");
        }
        return interval;
    }

    public static String readConnection(String path, Predicate predicate) throws Exception {
        String str = null;
        try (BufferedReader read = new BufferedReader(new FileReader(path))) {
            str = read.lines()
                    .filter(predicate::test)
                    .map(s -> s.split("=")[1])
                    .collect(Collectors.toList()).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (str == null) {
            throw new Exception("Not find properties for db");
        }
        return str;
    }
}
