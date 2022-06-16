package ru.job4j;

import ru.job4j.grabber.HabrCareerParse;
import ru.job4j.grabber.PsqlStore;
import ru.job4j.grabber.Store;
import ru.job4j.grabber.entity.Post;
import ru.job4j.grabber.utils.ISOParser;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Properties cfg = new Properties();
        try (InputStream in = App.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            cfg.load(in);
            Store store = new PsqlStore(cfg);
            HabrCareerParse habr = new HabrCareerParse(new ISOParser());
            String link = "http://career.habr.com";
            List<Post> posts = habr.list(link);
            store.save(posts.get(0));
            store.save(posts.get(1));
            store.getAll().forEach(System.out::println);
            System.out.println(store.findById(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
