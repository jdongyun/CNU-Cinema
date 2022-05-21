package com.dongyun.cnucinema.repository;

import com.dongyun.cnucinema.spec.entity.Customer;
import com.dongyun.cnucinema.spec.enums.Sex;
import com.dongyun.cnucinema.spec.repository.CustomerRepository;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.*;

@Repository
public class DbCustomerRepository implements CustomerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DbCustomerRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Optional<Customer> findByUsername(String username) {
        String sql = "select * from Customer " +
                "left outer join Authority A on Customer.username = A.username " +
                "where Customer.username = :username";
        List<Customer> result = jdbcTemplate.query(sql,
                new MapSqlParameterSource("username", username),
                customerRowMapper());

        return result.stream().findAny();
    }

    @Override
    public List<Customer> findAll() {
        String sql = "select * from Customer " +
                "left outer join Authority A on Customer.username = A.username";
        return jdbcTemplate.query(sql, customerRowExtractor());
    }

    @Override
    public void save(Customer customer) {
        // Customer 생성.
        String sql = "INSERT INTO Customer (username, name, password, email, birth_date, sex) " +
                "VALUES (:username, :name, :password, :email, :birth_date, :sex) ON DUPLICATE KEY UPDATE " +
                "name = :name, password = :password, email = :email, birth_date = :birth_date, sex = :sex";
        Map<String, Object> params = new HashMap<>();
        params.put("username", customer.getUsername());
        params.put("name", customer.getName());
        params.put("password", customer.getEncryptedPassword());
        params.put("email", customer.getEmail());
        params.put("birth_date", customer.getBirthDate().toString());
        params.put("sex", String.valueOf(customer.getSex()));

        jdbcTemplate.update(sql, params);

        // Authority 생성. 기본값은 USER.
        params = new HashMap<>();
        params.put("username", customer.getUsername());
        params.put("authority_name", "ROLE_USER");

        jdbcTemplate.update("INSERT INTO Authority (username, authority_name) " +
                "VALUES (:username, :authority_name) ON DUPLICATE KEY UPDATE " +
                "authority_name = :authority_name", params);
    }

    private ResultSetExtractor<List<Customer>> customerRowExtractor() {
        return (rs) -> {
            List<Customer> customers = new ArrayList<>();
            if (!rs.next()) return customers;

            while (!rs.isAfterLast()) {
                Customer customer = Customer.builder()
                        .username(rs.getString("username"))
                        .name(rs.getString("name"))
                        .encryptedPassword(rs.getString("password"))
                        .email(rs.getString("email"))
                        .birthDate(LocalDate.parse(rs.getString("birth_date")))
                        .sex(Sex.valueOf(rs.getString("sex")))
                        .authorities(new HashSet<>())
                        .build();

                for (; !rs.isAfterLast(); rs.next()) {
                    if (!Objects.equals(customer.getUsername(), rs.getString("username"))) break;
                    if (rs.getString("authority_name") != null)
                        customer.getAuthorities().add(new SimpleGrantedAuthority(rs.getString("authority_name")));
                }
                customers.add(customer);
            }
            return customers;
        };
    }


    private RowMapper<Customer> customerRowMapper() {
        return (rs, rowNum) -> {
            Customer customer = Customer.builder()
                    .username(rs.getString("username"))
                    .name(rs.getString("name"))
                    .encryptedPassword(rs.getString("password"))
                    .email(rs.getString("email"))
                    .birthDate(LocalDate.parse(rs.getString("birth_date")))
                    .sex(Sex.valueOf(rs.getString("sex")))
                    .authorities(new HashSet<>())
                    .build();

            for (; !rs.isAfterLast(); rs.next()) {
                if (!Objects.equals(customer.getUsername(), rs.getString("username"))) break;
                if (rs.getString("authority_name") != null)
                    customer.getAuthorities().add(new SimpleGrantedAuthority(rs.getString("authority_name")));
            }
            return customer;
        };
    }
}
