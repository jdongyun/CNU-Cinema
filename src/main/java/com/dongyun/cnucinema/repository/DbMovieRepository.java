package com.dongyun.cnucinema.repository;

import com.dongyun.cnucinema.spec.entity.Actor;
import com.dongyun.cnucinema.spec.entity.Movie;
import com.dongyun.cnucinema.spec.entity.Schedule;
import com.dongyun.cnucinema.spec.enums.MovieRating;
import com.dongyun.cnucinema.spec.repository.MovieRepository;
import com.dongyun.cnucinema.spec.vo.MovieRankStatsVo;
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

    private final DateTimeFormatter dateTimeFormatter;

    public DbMovieRepository(NamedParameterJdbcTemplate jdbcTemplate, DateTimeFormatter dateTimeFormatter) {
        this.jdbcTemplate = jdbcTemplate;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public Optional<Movie> findByMid(Long id) {
        // 영화 ID로 해당하는 영화를 찾는 질의.
        LocalDateTime now = LocalDateTime.now();

        String sql = """
                select M.*, A.*,
                    IFNULL(S.total_reserved, 0) total_reserved_seats,
                    IFNULL(S.total_watched, 0) total_watched_seats
                from Movie M
                    join Actor A on M.mid = A.mid
                    left join (
                        select
                            S.mid,
                            SUM(CASE WHEN T.status = 'R' THEN T.seats ELSE 0 END) total_reserved,
                            SUM(CASE WHEN S.show_at <= :now and T.status = 'R' THEN T.seats ELSE 0 END) total_watched
                        from Schedule S
                                 left join Ticketing T on T.sid = S.sid
                        group by S.mid
                    ) S on S.mid = M.mid
                where M.mid = :mid
                """;
        List<Movie> result = jdbcTemplate.query(sql,
                new MapSqlParameterSource("mid", id)
                        .addValue("now", now),
                movieRowWithActorsMapper());

        return result.stream().findAny();
    }

    @Override
    public List<Movie> findByTitleContains(String title) {
        // 영화 제목의 일부로 영화 목록을 가져오는 질의.
        LocalDateTime now = LocalDateTime.now();

        String sql = """
                select M.*, A.*,
                    IFNULL(S.total_reserved, 0) total_reserved_seats,
                    IFNULL(S.total_watched, 0) total_watched_seats
                from Movie M
                    join Actor A on M.mid = A.mid
                    left join (
                        select
                            S.mid,
                            SUM(CASE WHEN T.status = 'R' THEN T.seats ELSE 0 END) total_reserved,
                            SUM(CASE WHEN S.show_at <= :now and T.status = 'R' THEN T.seats ELSE 0 END) total_watched
                        from Schedule S
                            left join Ticketing T on T.sid = S.sid
                        group by S.mid
                    ) S on S.mid = M.mid
                where M.title LIKE :title
                """;
        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("title", "%" + title + "%")
                        .addValue("now", now),
                movieRowWithActorsExtractor());
    }

    @Override
    public List<Movie> findByScheduleShowAtDate(LocalDate showAtDate) {
        // 관람 예정일로 해당하는 영화 목록을 가져오는 질의.
        LocalDateTime now = LocalDateTime.now();

        String sql = """
                select M.*, A.*,
                    IFNULL(S.total_reserved, 0) total_reserved_seats,
                    IFNULL(S.total_watched, 0) total_watched_seats
                from Movie M
                    join Actor A on M.mid = A.mid
                    left join (
                        select
                            S.mid,
                            SUM(CASE WHEN T.status = 'R' THEN T.seats ELSE 0 END) total_reserved,
                            SUM(CASE WHEN S.show_at <= :now and T.status = 'R' THEN T.seats ELSE 0 END) total_watched
                        from Schedule S
                            left join Ticketing T on T.sid = S.sid
                        group by S.mid
                    ) S on S.mid = M.mid
                where M.mid in (select mid from Schedule where DATE(show_at) = :show_at_date)
                """;
        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("show_at_date", showAtDate.toString())
                        .addValue("now", now),
                movieRowWithActorsExtractor());
    }

    @Override
    public List<Movie> findByTitleContainsAndScheduleShowAtDate(String title, LocalDate showAtDate) {
        // 영화 제목의 일부와 관람 예정일로 해당하는 영화 목록을 가져오는 질의.
        LocalDateTime now = LocalDateTime.now();

        String sql = """
                select M.*, A.*,
                    IFNULL(S.total_reserved, 0) total_reserved_seats,
                    IFNULL(S.total_watched, 0) total_watched_seats
                from Movie M
                    join Actor A on M.mid = A.mid
                    left join (
                        select
                            S.mid,
                            SUM(CASE WHEN T.status = 'R' THEN T.seats ELSE 0 END) total_reserved,
                            SUM(CASE WHEN S.show_at <= :now and T.status = 'R' THEN T.seats ELSE 0 END) total_watched
                        from Schedule S
                            left join Ticketing T on T.sid = S.sid
                        group by S.mid
                    ) S on S.mid = M.mid
                where 
                    M.title LIKE :title and
                    S.mid = M.mid and
                    M.mid in (select mid from Schedule where DATE(show_at) = :show_at_date)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", "%" + title + "%");
        params.addValue("show_at_date", showAtDate.toString());
        params.addValue("now", now);

        return jdbcTemplate.query(sql, params, movieRowWithActorsExtractor());
    }


    @Override
    public List<Movie> findAll() {
        // 모든 영화를 가져오는 질의.
        LocalDateTime now = LocalDateTime.now();

        String sql = """
                select M.*, A.*,
                    IFNULL(S.total_reserved, 0) total_reserved_seats,
                    IFNULL(S.total_watched, 0) total_watched_seats
                from Movie M
                    join Actor A on M.mid = A.mid
                    left join (
                        select
                            S.mid,
                            SUM(CASE WHEN T.status = 'R' THEN T.seats ELSE 0 END) total_reserved,
                            SUM(CASE WHEN S.show_at <= :now and T.status = 'R' THEN T.seats ELSE 0 END) total_watched
                        from Schedule S
                            left join Ticketing T on T.sid = S.sid
                        group by S.mid
                    ) S on S.mid = M.mid
                """;
        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("now", now),
                movieRowWithActorsExtractor());
    }

    @Override
    public List<MovieRankStatsVo> findByRcAtBetweenWithRank(LocalDate startDate, LocalDate endDate) {
        // 특정 기간 동안의 영화 예매 기록의 순위를 가져오는 질의.
        String sql = """
                select
                    rank() over (order by sum(seats) desc) movie_rank,
                    Movie.title,
                    sum(seats) total_seats
                from Movie
                    join Schedule S on Movie.mid = S.mid
                    join Ticketing T on S.sid = T.sid
                where T.status <> 'C' and DATE(rc_at) between :start_date and :end_date
                group by Movie.mid
                order by movie_rank
                """;

        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("start_date", startDate)
                        .addValue("end_date", endDate),
                movieRankStatsVoRowMapper());
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
                    .totalReservedSeats(rs.getInt("total_reserved_seats"))
                    .totalWatchedSeats(rs.getInt("total_watched_seats"))
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
                        .totalReservedSeats(rs.getInt("total_reserved_seats"))
                        .totalWatchedSeats(rs.getInt("total_watched_seats"))
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

    private RowMapper<MovieRankStatsVo> movieRankStatsVoRowMapper() {
        return (rs, rowNum) ->
            MovieRankStatsVo.builder()
                .rank(rs.getInt("movie_rank"))
                .movieTitle(rs.getString("title"))
                .seats(rs.getInt("total_seats"))
                .build();
    }
}
