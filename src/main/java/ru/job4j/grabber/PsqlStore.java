package ru.job4j.grabber;

import ru.job4j.grabber.entity.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private final Connection cnn;

    private Post getPostSQL(ResultSet rs) throws SQLException {
        return new Post(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("text_"),
                rs.getString("link"),
                rs.getTimestamp("created").toLocalDateTime()
        );
    }

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            this.cnn = DriverManager.getConnection(
                    cfg.getProperty("jdbc.url"),
                    cfg.getProperty("jdbc.login"),
                    cfg.getProperty("jdbc.password"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement st = cnn.prepareStatement(
                "insert into \"html_parser\".post"
                        + "(name, text_, link, created) "
                        + "values(?, ?, ?, ?)"
                        + "on conflict (link) do nothing;"
        )) {
            st.setString(1, post.getTitle());
            st.setString(2, post.getDescription());
            st.setString(3, post.getLink());
            st.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            st.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> list = new ArrayList<>();
        try (PreparedStatement st = cnn.prepareStatement("select * from \"html_parser\".post")) {
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(getPostSQL(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement ps
                     = cnn.prepareStatement("select * from \"html_parser\".post where id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    post = getPostSQL(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}
