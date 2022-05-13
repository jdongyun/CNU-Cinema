package com.dongyun.cnucinema.repository;

import com.dongyun.cnucinema.spec.entity.Schedule;
import com.dongyun.cnucinema.spec.repository.ScheduleRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class DbScheduleRepository implements ScheduleRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DbScheduleRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Schedule> findBySid(Long id) {
        String sql = "select Schedule.*, T2.seats , (T2.seats - IFNULL(sum(T.seats), 0)) remain_seats " +
                "from Schedule " +
                "left outer join Ticketing T on Schedule.sid = T.sid " +
                "join Theater T2 on Schedule.tname = T2.tname " +
                "where T.status IS NULL or T.status <> 'C' AND Schedule.sid = :sid " +
                "group by Schedule.sid";
        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("sid", id),
                scheduleRowMapper()).stream().findAny();
    }

    @Override
    public List<Schedule> findByMid(Long id) {
        String sql = "select Schedule.*, T2.seats , (T2.seats - IFNULL(sum(T.seats), 0)) remain_seats " +
                "from Schedule " +
                "left outer join Ticketing T on Schedule.sid = T.sid " +
                "join Theater T2 on Schedule.tname = T2.tname " +
                "where T.status IS NULL or T.status <> 'C' AND Schedule.mid = :mid " +
                "group by Schedule.sid";
        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("mid", id),
                scheduleRowMapper());
    }

    @Override
    public List<Schedule> findByTname(String tname) {
        String sql = "select Schedule.*, T2.seats , (T2.seats - IFNULL(sum(T.seats), 0)) remain_seats " +
                "from Schedule " +
                "left outer join Ticketing T on Schedule.sid = T.sid " +
                "join Theater T2 on Schedule.tname = T2.tname " +
                "where T.status IS NULL or T.status <> 'C' AND Schedule.tname = :tname " +
                "group by Schedule.sid";
        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("tname", tname),
                scheduleRowMapper());
    }

    @Override
    public List<Schedule> findByTnameWithShowAtBetween(String tname, LocalDateTime showAtStart, LocalDateTime showAtEnd) {
        String sql = "select * from Schedule where tname = :tname AND show_at between :show_at_start and :show_at_end";
        Map<String, Object> params = new HashMap<>();
        params.put("tname", tname);
        params.put("show_at_start", Timestamp.valueOf(showAtStart));
        params.put("show_at_end", Timestamp.valueOf(showAtEnd));

        return jdbcTemplate.query(sql, params, scheduleRowMapper());
    }

    @Override
    public List<Schedule> findAll() {
        String sql = "select Schedule.*, T2.seats , (T2.seats - IFNULL(sum(T.seats), 0)) remain_seats " +
                "from Schedule " +
                "left outer join Ticketing T on Schedule.sid = T.sid " +
                "join Theater T2 on Schedule.tname = T2.tname " +
                "where T.status IS NULL or T.status <> 'C' " +
                "group by Schedule.sid";
        return jdbcTemplate.query(sql, scheduleRowMapper());
    }

    @Override
    public void save(Schedule schedule) {
        String sql = "INSERT INTO Schedule (mid, tname, show_at) " +
                "VALUES (:mid, :tname, :show_at) ON DUPLICATE KEY UPDATE " +
                "mid = :mid, tname = :tname, show_at = :show_at";
        Map<String, Object> params = new HashMap<>();
        params.put("mid", schedule.getMid());
        params.put("tname", schedule.getTname());
        params.put("show_at", schedule.getShowAt());

        jdbcTemplate.update(sql, params);
    }

    private RowMapper<Schedule> scheduleRowMapper() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        return (rs, rowNum) ->
                Schedule.builder()
                    .sid(rs.getLong("sid"))
                    .mid(rs.getLong("mid"))
                    .tname(rs.getString("tname"))
                    .showAt(LocalDateTime.parse(rs.getString("show_at"), formatter))
                    .remainSeats(rs.getInt("remain_seats"))
                    .build();
    }
}
