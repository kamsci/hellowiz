package com.hellowiz.api.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Person {
    private Integer id;

    private String name;

    private String email;

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

    public Person(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

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
        return Objects.equals(name, person.name) &&
                Objects.equals(email, person.email);
    }

    // When you override the equals method in a class, it's a good practice to also override the hashCode method
    // This ensures that objects that are equal (according to your equals method) produce the same hash code.
    // Necessary if you plan to use these objects as keys in collections like HashMap or HashSet.
    @Override
    public int hashCode() {
        return Objects.hash(name, email);
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

    public Person update(Person personChanges) {
        // update existing person with new name or email
        if (personChanges.getName() != null) {
            this.name = personChanges.getName();
        }
        if (personChanges.getEmail() != null) {
            this.email = personChanges.getEmail();
        }
        return this;
    }

}
