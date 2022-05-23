package com.dongyun.cnucinema.repository;

import com.dongyun.cnucinema.spec.entity.Ticketing;
import com.dongyun.cnucinema.spec.enums.TicketingStatus;
import com.dongyun.cnucinema.spec.repository.TicketingRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class DbTicketingRepository implements TicketingRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final DataSource dataSource;

    private final DateTimeFormatter dateTimeFormatter;

    public DbTicketingRepository(NamedParameterJdbcTemplate jdbcTemplate, DataSource dataSource, DateTimeFormatter dateTimeFormatter) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public Long save(Ticketing ticketing) {
        if (ticketing.getId() == null) {
            return insert(ticketing);
        } else {
            return update(ticketing);
        }
    }

    @Override
    public Optional<Ticketing> findById(Long id) {
        String sql = "select * from Ticketing T " +
                "JOIN Schedule S on T.sid = S.sid and T.id = :id " +
                "JOIN Movie M on S.mid = M.mid ";

        List<Ticketing> result = jdbcTemplate.query(sql,
                new MapSqlParameterSource("id", id),
                ticketingRowMapper());

        return result.stream().findAny();
    }

    @Override
    public List<Ticketing> findBySid(Long sid) {
        String sql = "select * from Ticketing T " +
                "JOIN Schedule S on T.sid = S.sid and T.sid = :sid " +
                "JOIN Movie M on S.mid = M.mid ";

        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("sid", sid),
                ticketingRowMapper());
    }

    @Override
    public List<Ticketing> findByUsername(String username) {
        String sql = "select * from Ticketing T " +
                "JOIN Schedule S on T.sid = S.sid and T.username = :username " +
                "JOIN Movie M on S.mid = M.mid ";

        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("username", username),
                ticketingRowMapper());
    }

    @Override
    public List<Ticketing> findByUsernameAndReservedOrderByRcAtDesc(String username) {
        LocalDateTime now = LocalDateTime.now();

        String sql = "select * from Ticketing T " +
                "JOIN Schedule S on T.sid = S.sid and T.username = :username and T.status = 'R' and S.show_at > :date " +
                "JOIN Movie M on S.mid = M.mid " +
                "order by T.rc_at desc";

        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("username", username)
                        .addValue("date", now),
                ticketingRowMapper());
    }

    @Override
    public List<Ticketing> findByUsernameAndCancelledOrderByRcAtDesc(String username) {
        String sql = "select * from Ticketing T " +
                "JOIN Schedule S on T.sid = S.sid and T.username = :username and T.status = 'C' " +
                "JOIN Movie M on S.mid = M.mid " +
                "order by T.rc_at desc";

        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("username", username),
                ticketingRowMapper());
    }

    @Override
    public List<Ticketing> findByUsernameAndWatchedOrderByShowAtDesc(String username) {
        LocalDateTime now = LocalDateTime.now();

        String sql = "select * from Ticketing T " +
                "JOIN Schedule S on T.sid = S.sid and T.username = :username and T.status = 'R' and S.show_at <= :date " +
                "JOIN Movie M on S.mid = M.mid " +
                "order by S.show_at desc";

        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("username", username)
                        .addValue("date", now),
                ticketingRowMapper());
    }

    @Override
    public List<Ticketing> findByUsernameAndReservedAndRcAtBetweenOrderByRcAtDesc(String username, LocalDateTime startAt, LocalDateTime endAt) {
        LocalDateTime now = LocalDateTime.now();

        String sql = "select * from Ticketing T " +
                "JOIN Schedule S on T.sid = S.sid and T.username = :username and T.status = 'R' and S.show_at > :date " +
                "JOIN Movie M on S.mid = M.mid " +
                "where T.rc_at between :start_at and :end_at " +
                "order by T.rc_at desc";

        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("username", username)
                        .addValue("date", now)
                        .addValue("start_at", startAt)
                        .addValue("end_at", endAt),
                ticketingRowMapper());
    }

    @Override
    public List<Ticketing> findByUsernameAndCancelledAndRcAtBetweenOrderByRcAtDesc(String username, LocalDateTime startAt, LocalDateTime endAt) {
        LocalDateTime now = LocalDateTime.now();

        String sql = "select * from Ticketing T " +
                "JOIN Schedule S on T.sid = S.sid and T.username = :username and T.status = 'C' " +
                "JOIN Movie M on S.mid = M.mid " +
                "where T.rc_at between :start_at and :end_at " +
                "order by T.rc_at desc";

        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("username", username)
                        .addValue("start_at", startAt)
                        .addValue("end_at", endAt),
                ticketingRowMapper());
    }

    @Override
    public List<Ticketing> findByUsernameAndWatchedAndRcAtBetweenOrderByShowAtDesc(String username, LocalDateTime startAt, LocalDateTime endAt) {
        LocalDateTime now = LocalDateTime.now();

        String sql = "select * from Ticketing T " +
                "JOIN Schedule S on T.sid = S.sid and T.username = :username and T.status = 'R' and S.show_at <= :date " +
                "JOIN Movie M on S.mid = M.mid " +
                "where T.rc_at between :start_at and :end_at " +
                "order by S.show_at desc";

        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("username", username)
                        .addValue("date", now)
                        .addValue("start_at", startAt)
                        .addValue("end_at", endAt),
                ticketingRowMapper());
    }

    private Long insert(Ticketing ticketing) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource)
                .withTableName("Ticketing")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("sid", ticketing.getSid());
        params.put("username", ticketing.getUsername());
        params.put("rc_at", ticketing.getRcAt());
        params.put("seats", ticketing.getSeats());
        params.put("status", ticketing.getStatus().toString());

        return insert.executeAndReturnKey(params).longValue();
    }

    private Long update(Ticketing ticketing) {
        String sql = "UPDATE Ticketing SET " +
                "username = :username, sid = :sid, rc_at = :rc_at, seats = :seats, status = :status " +
                "WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", ticketing.getId());
        params.put("sid", ticketing.getSid());
        params.put("username", ticketing.getUsername());
        params.put("rc_at", ticketing.getRcAt());
        params.put("seats", ticketing.getSeats());
        params.put("status", ticketing.getStatus().toString());

        jdbcTemplate.update(sql, params);

        return ticketing.getId();
    }

    private RowMapper<Ticketing> ticketingRowMapper() {
        return (rs, rowNum) ->
                Ticketing.builder()
                        .id(rs.getLong("id"))
                        .sid(rs.getLong("sid"))
                        .username(rs.getString("username"))
                        .rcAt(LocalDateTime.parse(rs.getString("rc_at"), dateTimeFormatter))
                        .seats(rs.getInt("seats"))
                        .status(TicketingStatus.valueOf(rs.getString("status")))
                        .movieTitle(rs.getString("title"))
                        .scheduleShowAt(LocalDateTime.parse(rs.getString("show_at"), dateTimeFormatter))
                        .build();
    }
}
