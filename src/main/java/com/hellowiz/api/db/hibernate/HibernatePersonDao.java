package com.hellowiz.api.db.hibernate;

import com.hellowiz.api.api.Person;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;

public class HibernatePersonDao extends AbstractDAO<Person> {
    public HibernatePersonDao(SessionFactory factory) {
        super(factory);
    }

    public Person findById(Long id) {
        return get(id);
    }

    public long create(Person person) {
        return persist(person).getId();
    }

    public List<Person> findAll() {
        return list(namedTypedQuery("com.example.helloworld.core.Person.findAll"));
    }
}
