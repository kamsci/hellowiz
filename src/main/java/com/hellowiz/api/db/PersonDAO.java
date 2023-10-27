package com.hellowiz.api.db;

import com.hellowiz.api.api.Person;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface PersonDAO {
    @SqlUpdate("create table person (id int primary key, name varchar(100)), email varchar(300))")
    void createTable();

    @SqlUpdate("insert into values (:id, :name, :email)")
    Person insert(@Bind("name") String name, @Bind("email") String email);

    @SqlQuery("select person from table where id = :id and insert new values (:name, :email)")
    Person updateById(@Bind("id") long id, @Bind("name") String name, @Bind("email") String email);
    @SqlQuery("select person from table where id = :id")
    Person findById(@Bind("id") long id);

    @SqlQuery("select person from table where email = :email")
    Person findByEmail(@Bind("email") String email);

    @SqlQuery("select all persons from table")
    List<Person> findAll();

    @SqlQuery("select all from table where id = :id")
    Person deleteById(@Bind("id") long id);

    boolean isConnected();
}
