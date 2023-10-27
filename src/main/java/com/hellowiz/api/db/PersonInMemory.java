package com.hellowiz.api.db;

import com.hellowiz.api.api.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class PersonInMemory implements PersonDAO {
    private final AtomicLong counter;

    private final HashMap<Long, Person> mockDB;
    public PersonInMemory() {
        this.counter = new AtomicLong();
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
    public Person insert(String name, String email) {
        Person newPerson = new Person(name, email);
        newPerson.setId(counter.incrementAndGet());
        mockDB.put(newPerson.getId(), newPerson);
        return newPerson;
    }

    @Override
    public Person updateById(long id, String name, String email) {
        if (mockDB.containsKey(id)) {
            Person existingPerson = mockDB.get(id);
            Person personChanges = new Person(name, email);
            existingPerson.update(personChanges);
            return existingPerson;
        }
        return null;
    }

    @Override
    public Person findById(long id) {
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
    public Person deleteById(long id) {
        if (mockDB.containsKey(id)) {
            Person person = mockDB.get(id);
            mockDB.remove(id);
            return person;
        }
        return null;
    }

    @Override
    public boolean isConnected() {
        return this.counter != null && this.mockDB != null;
    }
}
