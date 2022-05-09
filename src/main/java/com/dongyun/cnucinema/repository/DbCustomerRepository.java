package com.dongyun.cnucinema.repository;

import com.dongyun.cnucinema.spec.entity.Authority;
import com.dongyun.cnucinema.spec.entity.Customer;
import com.dongyun.cnucinema.spec.enums.Sex;
import com.dongyun.cnucinema.spec.repository.CustomerRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.*;

@Repository
public class DbCustomerRepository implements CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    public DbCustomerRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<Customer> findByUsername(String username) {
        List<Customer> result = jdbcTemplate.query(
                "select * from Customer join Authority A on Customer.username = A.username where Customer.username = ?",
                customerRowWithAuthoritiesMapper(), username);


        return result.stream().findAny();
    }

    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query("select * from Customer", customerRowMapper());
    }

    @Override
    public void save(Customer customer) {
        SimpleJdbcInsert customerInsert = new SimpleJdbcInsert(jdbcTemplate);
        customerInsert.withTableName("Customer");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", customer.getUsername());
        parameters.put("name", customer.getName());
        parameters.put("password", customer.getEncryptedPassword());
        parameters.put("email", customer.getEmail());
        parameters.put("birth_date", customer.getBirthDate());
        parameters.put("sex", String.valueOf(customer.getSex()));

        customerInsert.execute(new MapSqlParameterSource(parameters));

        // 기본 권한은 USER.
        SimpleJdbcInsert authorityInsert = new SimpleJdbcInsert(jdbcTemplate);
        authorityInsert.withTableName("Authority");

        parameters = new HashMap<>();
        parameters.put("username", customer.getUsername());
        parameters.put("authority_name", "USER");

        authorityInsert.execute(new MapSqlParameterSource(parameters));
    }
    
    private RowMapper<Customer> customerRowWithAuthoritiesMapper() {
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


            customer.getAuthorities().add(new SimpleGrantedAuthority(rs.getString("authority_name")));
            while (rs.next()) {
                customer.getAuthorities().add(new SimpleGrantedAuthority(rs.getString("authority_name")));
            }
            return customer;
        };
    }

    private RowMapper<Customer> customerRowMapper() {
        return (rs, rowNum) ->
            Customer.builder()
                    .username(rs.getString("username"))
                    .name(rs.getString("name"))
                    .encryptedPassword(rs.getString("password"))
                    .email(rs.getString("email"))
                    .birthDate(LocalDate.parse(rs.getString("birth_date")))
                    .sex(Sex.valueOf(rs.getString("sex")))
                    .build();
    }
}
