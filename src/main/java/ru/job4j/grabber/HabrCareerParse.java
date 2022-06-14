package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerParse implements DateTimeParser {
    private static final String SOURCE_LINK = "http://career.habr.com";

    private static String pageLink;

    @Override
    public LocalDateTime parser(String parser) {
        return LocalDateTime.parse(parser, DateTimeFormatter.ISO_DATE_TIME);
    }

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements rows = document.select(".job_show_description__vacancy_description");
        return rows.first().child(0).text();
    }

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 5; i++) {
            pageLink = String.format("%s/vacancies/java_developer?page=%s", SOURCE_LINK, i);
            Connection connection = Jsoup.connect(pageLink);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");

            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String time = row.select(".vacancy-card__date").first().child(0).attr("datetime");
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                System.out.printf("%s %s time: %s%n", vacancyName, link, time);
            });
        }
    }
}