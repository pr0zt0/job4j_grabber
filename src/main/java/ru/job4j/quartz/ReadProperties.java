package ru.job4j.quartz;

import java.io.*;
import java.util.stream.Collectors;

public class ReadProperties {

    public static int readInterval(String path) throws Exception {
        int interval = -1;
        try (BufferedReader read = new BufferedReader(new FileReader(path))) {
            interval = Integer.parseInt(read.lines()
                    .filter(s -> s.contains("rabbit.interval"))
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
}
