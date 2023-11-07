package com.hellowiz.api.db;

import com.hellowiz.api.api.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class PersonInMemory implements PersonDAO {
    private final AtomicInteger counter;

    private final HashMap<Integer, Person> mockDB;
    public PersonInMemory() {
        this.counter = new AtomicInteger();
        this. mockDB = new HashMap<>();

        createTable();
    }
    @Override
    public void createTable() {
        Person sam = new Person("Sam", "sam@memail.net");
        sam.setId(counter.incrementAndGet());
        mockDB.put(sam.getId(), sam);

        Person sal = new Person("Sal", "salutations@hi.com");
        sal.setId(counter.incrementAndGet());
        mockDB.put(sal.getId(), sal);

        Person sula = new Person("Sula", "s@la.io");
        sula.setId(counter.incrementAndGet());
        mockDB.put(sula.getId(), sula);
    }

    @Override
    public int insert(String name, String email) {
        Person newPerson = new Person(name, email);
        newPerson.setId(counter.incrementAndGet());
        mockDB.put(newPerson.getId(), newPerson);
        return newPerson.getId();
    }

    @Override
    public int updateById(int id, String name, String email) {
        if (mockDB.containsKey(id)) {
            Person personChanges = new Person(id, name, email);
            mockDB.put(id, personChanges);
            return 1;
        }
        return 0;
    }

    @Override
    public Person findById(int id) {
        return mockDB.get(id);
    }

    @Override
    public Person findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        Optional<Person> foundPerson = mockDB.values().stream().filter(person ->
                person.getEmail().equals(email)).findFirst();

        return foundPerson.orElse(null);

    }

    @Override
    public List<Person> findAll() {
        return new ArrayList<>(mockDB.values());
    }

    @Override
    public int deleteById(int id) {
        if (mockDB.containsKey(id)) {
            Person person = mockDB.get(id);
            mockDB.remove(id);
            return 1;
        }
        return 0;
    }

    @Override
    public boolean isConnected() {
        return this.counter != null && this.mockDB != null;
    }

}
