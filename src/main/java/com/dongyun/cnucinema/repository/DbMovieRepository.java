package com.dongyun.cnucinema.repository;

import com.dongyun.cnucinema.spec.entity.Actor;
import com.dongyun.cnucinema.spec.entity.Movie;
import com.dongyun.cnucinema.spec.entity.Schedule;
import com.dongyun.cnucinema.spec.enums.MovieRating;
import com.dongyun.cnucinema.spec.repository.MovieRepository;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DbMovieRepository implements MovieRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DbMovieRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Movie> findByMid(Long id) {
        String sql = "select * from Movie m join Actor a on m.mid = a.mid where m.mid = :mid";
        List<Movie> result = jdbcTemplate.query(sql,
                new MapSqlParameterSource("mid", id),
                movieRowWithActorsMapper());

        return result.stream().findAny();
    }

    @Override
    public List<Movie> findByTitleContains(String title) {
        String sql = "select * from Movie join Actor A on Movie.mid = A.mid where title like :title";
        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("title", "%" + title + "%"),
                movieRowWithActorsExtractor());
    }

    @Override
    public List<Movie> findByScheduleShowAtDate(LocalDate showAtDate) {
        String sql = "select * from Movie join Schedule S on Movie.mid = S.mid where DATE(S.show_at) = :show_at_date";
        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("show_at_date", showAtDate.toString()),
                movieRowWithSchedulesExtractor());
    }

    @Override
    public List<Movie> findByTitleContainsAndScheduleShowAtDate(String title, LocalDate showAtDate) {
        String sql = "select * from Movie join Schedule S on Movie.mid = S.mid where Movie.title LIKE :title and DATE(S.show_at) = :show_at_date";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", "%" + title + "%");
        params.addValue("show_at_date", showAtDate.toString());

        return jdbcTemplate.query(sql, params, movieRowWithSchedulesExtractor());
    }


    @Override
    public List<Movie> findAll() {
        String sql = "select * from Movie join Actor A on Movie.mid = A.mid";
        return jdbcTemplate.query(sql, movieRowWithActorsExtractor());
    }

    private RowMapper<Movie> movieRowWithActorsMapper() {
        return (rs, rowNum) -> {
            Movie movie = Movie.builder()
                    .mid(rs.getLong("mid"))
                    .title(rs.getString("title"))
                    .openDay(LocalDate.parse(rs.getString("open_day")))
                    .rating(MovieRating.valueOf(rs.getString("rating")))
                    .length(rs.getInt("length"))
                    .director(rs.getString("director"))
                    .actors(new ArrayList<>())
                    .schedules(new ArrayList<>())
                    .build();

            for (; !rs.isAfterLast(); rs.next()) {
                movie.getActors().add(Actor.builder()
                        .mid(rs.getLong("mid"))
                        .name(rs.getString("name"))
                        .build());
            }
            return movie;
        };
    }

    private ResultSetExtractor<List<Movie>> movieRowWithActorsExtractor() {
        return (rs) -> {
            List<Movie> movies = new ArrayList<>();
            if (!rs.next()) return movies;

            while (!rs.isAfterLast()) {
                Movie movie = Movie.builder()
                        .mid(rs.getLong("mid"))
                        .title(rs.getString("title"))
                        .openDay(LocalDate.parse(rs.getString("open_day")))
                        .rating(MovieRating.valueOf(rs.getString("rating")))
                        .length(rs.getInt("length"))
                        .director(rs.getString("director"))
                        .actors(new ArrayList<>())
                        .schedules(new ArrayList<>())
                        .build();

                for (; !rs.isAfterLast(); rs.next()) {
                    if (movie.getMid() != rs.getLong("mid")) break;
                    movie.getActors().add(Actor.builder()
                            .mid(rs.getLong("mid"))
                            .name(rs.getString("name"))
                            .build());
                }
                movies.add(movie);
            }

            return movies;
        };
    }

    private ResultSetExtractor<List<Movie>> movieRowWithSchedulesExtractor() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        return (rs) -> {
            List<Movie> movies = new ArrayList<>();
            if (!rs.next()) return movies;

            while (!rs.isAfterLast()) {
                Movie movie = Movie.builder()
                        .mid(rs.getLong("mid"))
                        .title(rs.getString("title"))
                        .openDay(LocalDate.parse(rs.getString("open_day")))
                        .rating(MovieRating.valueOf(rs.getString("rating")))
                        .length(rs.getInt("length"))
                        .director(rs.getString("director"))
                        .actors(new ArrayList<>())
                        .schedules(new ArrayList<>())
                        .build();

                for (; !rs.isAfterLast(); rs.next()) {
                    if (movie.getMid() != rs.getLong("mid")) break;
                    movie.getSchedules().add(Schedule.builder()
                            .sid(rs.getLong("sid"))
                            .mid(rs.getLong("mid"))
                            .showAt(LocalDateTime.parse(rs.getString("show_at"), formatter))
                            .tname(rs.getString("tname"))
                            .build());
                }
                movies.add(movie);
            }

            return movies;
        };
    }
}
