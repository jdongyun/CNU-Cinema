package com.dongyun.cnucinema.repository;

import com.dongyun.cnucinema.spec.entity.Schedule;
import com.dongyun.cnucinema.spec.repository.ScheduleRepository;
import com.dongyun.cnucinema.spec.vo.ScheduleStatsVo;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class DbScheduleRepository implements ScheduleRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final DataSource dataSource;

    private final DateTimeFormatter dateTimeFormatter;

    public DbScheduleRepository(NamedParameterJdbcTemplate jdbcTemplate, DataSource dataSource, DateTimeFormatter dateTimeFormatter) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public Optional<Schedule> findBySid(Long id) {
        String sql = "select Schedule.*, T2.seats , (T2.seats - IFNULL(sum(T.seats), 0)) remain_seats " +
                "from Schedule " +
                "left outer join Ticketing T on Schedule.sid = T.sid and (T.status IS NULL or T.status <> 'C') " +
                "join Theater T2 on Schedule.tname = T2.tname " +
                "where Schedule.sid = :sid " +
                "group by Schedule.sid";
        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("sid", id),
                scheduleRowMapper()).stream().findAny();
    }

    @Override
    public List<Schedule> findByMid(Long id) {
        String sql = "select Schedule.*, T2.seats , (T2.seats - IFNULL(sum(T.seats), 0)) remain_seats " +
                "from Schedule " +
                "left outer join Ticketing T on Schedule.sid = T.sid and (T.status IS NULL or T.status <> 'C') " +
                "join Theater T2 on Schedule.tname = T2.tname " +
                "where mid = :mid " +
                "group by Schedule.sid";
        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("mid", id),
                scheduleRowMapper());
    }

    @Override
    public List<Schedule> findByTname(String tname) {
        String sql = "select Schedule.*, T2.seats , (T2.seats - IFNULL(sum(T.seats), 0)) remain_seats " +
                "from Schedule " +
                "left outer join Ticketing T on Schedule.sid = T.sid and (T.status IS NULL or T.status <> 'C') " +
                "join Theater T2 on Schedule.tname = T2.tname " +
                "where Schedule.tname = :tname " +
                "group by Schedule.sid";
        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("tname", tname),
                scheduleRowMapper());
    }

    @Override
    public List<Schedule> findByTnameWithShowAtBetween(String tname, LocalDateTime showAtStart, LocalDateTime showAtEnd) {
        String sql = "select Schedule.*, T2.seats , (T2.seats - IFNULL(sum(T.seats), 0)) remain_seats " +
                "from Schedule " +
                "left outer join Ticketing T on Schedule.sid = T.sid and (T.status IS NULL or T.status <> 'C') " +
                "join Theater T2 on Schedule.tname = T2.tname " +
                "where Schedule.tname = :tname AND show_at between :show_at_start and :show_at_end " +
                "group by Schedule.sid";
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
                "left outer join Ticketing T on Schedule.sid = T.sid and (T.status IS NULL or T.status <> 'C' ) " +
                "join Theater T2 on Schedule.tname = T2.tname " +
                "group by Schedule.sid";
        return jdbcTemplate.query(sql, scheduleRowMapper());
    }

    @Override
    public List<ScheduleStatsVo> findStatsByRcAtBetween(LocalDate startDate, LocalDate endDate) {
        String sql = """
                select
                    T.sid,
                    Schedule.show_at,
                    M.title,
                    T.username,
                    C.name,
                    sum(seats) total_seats
                from Schedule
                         join Ticketing T on Schedule.sid = T.sid
                         join Movie M on Schedule.mid = M.mid
                         join Customer C on T.username = C.username
                where
                        status <> 'C' and DATE(rc_at) between :start_date and :end_date
                group by T.sid, T.username with rollup
                """;

        return jdbcTemplate.query(sql,
                new MapSqlParameterSource("start_date", startDate)
                    .addValue("end_date", endDate),
                scheduleStatsVoRowMapper());
    }

    @Override
    public Long save(Schedule schedule) {
        if (schedule.getSid() == null) {
            return insert(schedule);
        } else {
            return update(schedule);
        }
    }

    private Long insert(Schedule schedule) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource)
                .withTableName("Schedule")
                .usingGeneratedKeyColumns("sid");

        Map<String, Object> params = new HashMap<>();
        params.put("mid", schedule.getMid());
        params.put("tname", schedule.getTname());
        params.put("show_at", schedule.getShowAt());
        return insert.executeAndReturnKey(params).longValue();
    }

    private Long update(Schedule schedule) {
        String sql = "UPDATE Schedule SET mid = :mid, tname = :tname, show_at = :show_at " +
                "WHERE sid = :sid";
        Map<String, Object> params = new HashMap<>();
        params.put("sid", schedule.getSid());
        params.put("mid", schedule.getMid());
        params.put("tname", schedule.getTname());
        params.put("show_at", schedule.getShowAt());

        jdbcTemplate.update(sql, params);
        return schedule.getSid();
    }

    private RowMapper<Schedule> scheduleRowMapper() {
        return (rs, rowNum) ->
                Schedule.builder()
                    .sid(rs.getLong("sid"))
                    .mid(rs.getLong("mid"))
                    .tname(rs.getString("tname"))
                    .showAt(LocalDateTime.parse(rs.getString("show_at"), dateTimeFormatter))
                    .remainSeats(rs.getInt("remain_seats"))
                    .build();
    }

    private RowMapper<ScheduleStatsVo> scheduleStatsVoRowMapper() {
        return (rs, rowNum) ->
                ScheduleStatsVo.builder()
                    .scheduleShowAt(
                            rs.getObject("sid", Integer.class) == null ?
                                    null : LocalDateTime.parse(rs.getString("show_at"), dateTimeFormatter))
                    .movieTitle(
                            rs.getObject("sid", Integer.class) == null ?
                                    null : rs.getString("title"))
                    .customerName(
                            rs.getString("username") == null ?
                                    null : rs.getString("name"))
                    .seats(rs.getInt("total_seats"))
                    .build();
    }
}
