package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.entity.Post;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.ISOParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HabrCareerParse implements Parse {
    private static final String SOURCE_LINK = "http://career.habr.com";

    private static final int COUNT_PAGE = 5;

    private final DateTimeParser dateTimeParser;

    private String retrieveDescription(String link) {
        Connection connection = Jsoup.connect(link);
        Document document = null;
        try {
            document = connection.get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Elements rows = Objects.requireNonNull(document).select(".job_show_description__vacancy_description");
        return rows.first().child(0).text();
    }

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private Post getPost(Element el) {
        Element titleElement = el.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        String time = el.select(".vacancy-card__date").first().child(0).attr("datetime");
        String vacancyName = titleElement.text();
        String linkLocal = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        return new Post(vacancyName,
                linkLocal,
                retrieveDescription(linkLocal),
                dateTimeParser.parser(time));
    }

    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < COUNT_PAGE; i++) {
            String pageLink = String.format("%s/vacancies/java_developer?page=%s", link, i);
            Connection connection = Jsoup.connect(pageLink);
            try {
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> posts.add(getPost(row)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return posts;
    }

    public static void main(String[] args) {
        new HabrCareerParse(new ISOParser()).list(SOURCE_LINK).forEach(System.out::println);
    }
}