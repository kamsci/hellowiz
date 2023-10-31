package com.hellowiz.api.db;

import com.hellowiz.api.api.Person;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

/*
    Example of jdbi.handle - Unused
    Dropwizard suggests to use Interface instead of instance
 */
public class PersonDAOImpl implements PersonDAO{
    private final Jdbi jdbi;

    public PersonDAOImpl(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public void createTable() {
        // Already created
    }

    @Override
    public int insert(String name, String email) {
        // TODO: http://jdbi.org/#_generated_keys
//        return jdbi.useHandle(handle -> {
//            Person person = handle.createUpdate("INSERT INTO persons (name, email) VALUES (:name, :email)")
//                    .bind("name", name)
//                    .bind("email", email)
//                    .executeAndReturnGeneratedKeys()
//                    .mapTo(Person.class)
//                    .one();
//            return person.getId();
//        });
        return 0;
    }

    @Override
    public int updateById(long id, String name, String email) {
        return 0;
    }

    @Override
    public Person findById(long id) {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM persons WHERE id = :id")
                .bind("id", id)
                .mapToBean(Person.class)
                .findFirst()
                .orElse(null));
    }

    @Override
    public Person findByEmail(String email) {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM persons WHERE email = :email")
                .bind("email", email)
                .mapToBean(Person.class)
                .findFirst()
                .orElse(null));
    }

    @Override
    public List<Person> findAll() {
        return null;
    }

    @Override
    public int deleteById(int id) {
        return 0;
    }

    @Override
    public boolean isConnected() {
        return jdbi != null;
    }

}
