package com.hellowiz.api.db;

import com.hellowiz.api.api.Person;
import com.hellowiz.api.db.support.PersonSupport;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class PersonDaoTest {

    private final String testName = "Sam";
    private final String testEmail = "s@email.com";

    @Before
    public void setUp() {

    }

    @Test
    public void createsAndFindsPerson() {
        try {
            PersonSupport.withPersonsDao(personDAO -> {
                Integer id = personDAO.insert(testName, testEmail);

                assertThat(id, equalTo(1));

                Person person = personDAO.findById(1);

                assertThat(person.getName(), equalTo(testName));
                assertThat(person.getEmail(), equalTo(testEmail));
            }, false);
        } catch (Exception e) {
            assertThat(e.getMessage(), false);
        }
    }

    @Test
    public void createsAndFindAll() {
        try {
            PersonSupport.withPersonsDao(personDAO -> {
                Integer id = personDAO.insert(testName, testEmail);

                assertThat(id, equalTo(2));

                List<Person> persons = personDAO.findAll();

                assertThat(persons.size(), equalTo(2));

                persons.forEach(person -> {
                    if (person.getId() == 1) {
                        assertThat(person.getName(), equalTo(PersonSupport.createdName));
                        assertThat(person.getEmail(), equalTo(PersonSupport.createdEmail));
                    } else {
                        assertThat(person.getId(), equalTo(2));
                        assertThat(person.getName(), equalTo(testName));
                        assertThat(person.getEmail(), equalTo(testEmail));
                    }
                });
            }, true);
        } catch (Exception e) {
            assertThat(e.getMessage(), false);
        }
    }

    @Test
    public void updatePersonById() {
        try {
            PersonSupport.withPersonsDao(personDAO -> {
                int updatedCount = personDAO.updateById(1, "Cali", "cali@e.io");
                assertThat(updatedCount, equalTo(1));

                Person person = personDAO.findById(1);

                assertThat(person.getName(), equalTo("Cali"));
                assertThat(person.getEmail(), equalTo("cali@e.io"));
            }, true);
        } catch (Exception e) {
            assertThat(e.getMessage(), false);
        }
    }

    @Test
    public void findsPersonById() {
        try {
            PersonSupport.withPersonsDao(personDAO -> {
                Person person = personDAO.findById(1);

                assertThat(person.getName(), equalTo(PersonSupport.createdName));
                assertThat(person.getEmail(), equalTo(PersonSupport.createdEmail));
            }, true);
        } catch (Exception e) {
            assertThat(e.getMessage(), false);
        }
    }

    @Test
    public void findsPersonByEmail() {
        try {
            PersonSupport.withPersonsDao(personDAO -> {
                Person person = personDAO.findByEmail(PersonSupport.createdEmail);

                assertThat(person.getId(), equalTo(1));
                assertThat(person.getName(), equalTo(PersonSupport.createdName));
                assertThat(person.getEmail(), equalTo(PersonSupport.createdEmail));
            }, true);
        } catch (Exception e) {
            assertThat(e.getMessage(), false);
        }
    }

    @Test
    public void deletesPersonById() {
        try {
            PersonSupport.withPersonsDao(personDAO -> {
                int countDeleted = personDAO.deleteById(1);
                assertThat(countDeleted, equalTo(1));

                Person person = personDAO.findById(1);
                assertThat(person, nullValue());
            }, true);
        } catch (Exception e) {
            assertThat(e.getMessage(), false);
        }
    }
}
