package com.hellowiz.api.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.executable.ExecutableType;
import jakarta.validation.executable.ValidateOnExecution;

import java.util.Objects;

public class Person {
    @NotNull
    private Integer id;


    private String name;


    private String email;

    @JsonCreator
    private Person() {
        // Jackson deserialization
        //  -> indicates that this constructor is used by Jackson during the deserialization process to create an instance of the Person class from JSON data.

        // Jackson uses reflection to create objects and populate their fields based on the JSON data it encounters.
        // When it encounters a private constructor with no arguments like this, it can create an instance of the class without needing to call a public constructor.
        // This is especially useful when you want to create immutable objects, as it allows Jackson to set the field values directly.

        // By default, Jackson uses the no-argument constructor for deserialization if one is available.
        // If there's no no-argument constructor available, Jackson can still use constructors with arguments, but they need to be annotated with @JsonCreator and each argument should be annotated with @JsonProperty to map JSON properties to constructor parameters.
    }

    public Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @JsonCreator
    public Person(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("email") String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @JsonProperty
    public int getId() {
        return id;
    }
    @JsonProperty
    // MOCK used to mock creating of a model with a PK
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getEmail() {
        return email;
    }

    @JsonProperty
    public void setEmail(String email) {
        this.email = email;
    }

    // Equals method used in testing the model deserialization
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) &&
            Objects.equals(name, person.name) &&
            Objects.equals(email, person.email);
    }

    // When you override the equals method in a class, it's a good practice to also override the hashCode method
    // This ensures that objects that are equal (according to your equals method) produce the same hash code.
    // Necessary if you plan to use these objects as keys in collections like HashMap or HashSet.
    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    // Providing a meaningful toString implementation makes it easier to debug and print Person objects in a human-readable format.
    @Override
    public String toString() {
            return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public static class Request {
        @JsonProperty
        @NotNull(message = NAME_NULL_ERROR)
        @Size(min = 2, max = 100, message = NAME_LENGTH_ERROR)
        private String name;

        @JsonProperty
        @NotNull(message = EMAIL_NULL_ERROR)
        @Email(regexp = ".+@.+\\..+", message = EMAIL_VALIDATION_ERROR)
        @Size(min = 5, max = 100, message = EMAIL_LENGTH_ERROR)
        private String email;

        @JsonCreator
        public Request() {
            // Jackson deserialization
        }

        public Request(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        // Equals method used in testing the model deserialization
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person.Request personRequest = (Person.Request) o;
            return Objects.equals(name, personRequest.name) &&
                Objects.equals(email, personRequest.email);
        }

        // This ensures that objects that are equal (according to your equals method) produce the same hash code.
        // Necessary if you plan to use these objects as keys in collections like HashMap or HashSet.
        @Override
        public int hashCode() {
            return Objects.hash(name, email);
        }

        // Providing a meaningful toString implementation makes it easier to debug and print Person objects in a human-readable format.
        @Override
        public String toString() {
            return "Person.Request{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
        }

        public static final String NAME_LENGTH_ERROR = "Name must be between 2 and 100 characters long";
        public static final String NAME_NULL_ERROR = "Name must not be null";
        public static final String EMAIL_VALIDATION_ERROR = "Please provide a valid email address";
        public static final String EMAIL_LENGTH_ERROR = "Email must be between 5 and 100 characters long";
        public static final String EMAIL_NULL_ERROR = "Email must not be null";
    }
}
