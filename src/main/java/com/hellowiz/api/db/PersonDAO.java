package com.hellowiz.api.db;

import com.hellowiz.api.api.Person;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

/*
    Interface to use JDBI’s SQL Objects API, which allows you to write DAO classes as interfaces
    https://www.dropwizard.io/en/stable/manual/jdbi3.html
 */
public interface PersonDAO {
    @SqlUpdate("CREATE TABLE persons (id SERIAL PRIMARY KEY, name VARCHAR(100) NOT NULL, email varchar(100) UNIQUE NOT NULL)")
    void createTable();

    @SqlUpdate("INSERT INTO persons (name, email) values (:name, :email)")
    @GetGeneratedKeys("id")
    int insert(@Bind("name") String name, @Bind("email") String email);

    @SqlUpdate("UPDATE persons SET name = :name, email = :email WHERE id = :id")
    int updateById(@Bind("id") long id, @Bind("name") String name, @Bind("email") String email);
    @SqlQuery("SELECT * FROM persons WHERE id = :id")
    Person findById(@Bind("id") long id);

    @SqlQuery("SELECT * FROM persons WHERE email = :email")
    Person findByEmail(@Bind("email") String email);

    @SqlQuery("SELECT * FROM persons")
    List<Person> findAll();

    @SqlUpdate("DELETE FROM persons WHERE id = :id")
    int deleteById(@Bind("id") int id);

    boolean isConnected();
}
