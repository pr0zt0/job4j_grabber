package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ISOParser implements DateTimeParser {

    @Override
    public LocalDateTime parser(String parser) {
        return LocalDateTime.parse(parser, DateTimeFormatter.ISO_DATE_TIME);
    }
}
