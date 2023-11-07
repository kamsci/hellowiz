package com.hellowiz.api.db.support;

import com.hellowiz.api.db.PersonDAO;
import com.hellowiz.api.db.PersonMapper;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class PersonSupport {
    private PersonSupport() {
        throw new AssertionError("PersonSupport can not be instantiated");
    }

    public static String createdName = "Kali";
    public static String createdEmail = "kali@e.io";

    public static void withPersonsDao(Consumer<PersonDAO> jdbiConsumer, boolean createPerson) throws Exception {
            DatabaseSupport.withPgDatabase(jdbi -> {
                try {
                    jdbi.registerRowMapper(new PersonMapper());
                    PersonDAO personDAO = jdbi.onDemand(PersonDAO.class);

                    // Create tables and set up the database schema for testing
                    try (Handle handle = jdbi.open()) {
                        handle.execute(
                            "CREATE TABLE persons (id SERIAL PRIMARY KEY, name VARCHAR(100) NOT NULL, email varchar(100) UNIQUE NOT NULL)");
                    }

                    // Create person for testing
                    if (createPerson) createTestPerson(jdbi, createdName, createdEmail);

                    // Create an instance of your DAO
                    personDAO = jdbi.onDemand(PersonDAO.class);

                    // Create an instance of your DAO
                    jdbiConsumer.accept(personDAO);
                } catch (Exception e) {
                    LoggerFactory.getLogger(PersonSupport.class).error("Failed to use personDao and create test persons table");
                    throw e;
                }
            });
    }

    private static void createTestPerson(Jdbi jdbi, String name, String email) {
         jdbi.useHandle(handle -> {
            int inserted = handle.createUpdate("INSERT INTO persons (name, email) VALUES (:name, :email)")
                    .bind("name", name)
                    .bind("email", email)
                    .execute();
            if (inserted == 0) {
                throw new RuntimeException("Failed to insert person for testing");
            }
        });
    }
}
