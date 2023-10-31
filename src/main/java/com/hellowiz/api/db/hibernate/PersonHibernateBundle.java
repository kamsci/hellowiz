package com.hellowiz.api.db.hibernate;

import com.hellowiz.api.api.Person;
import com.hellowiz.api.hellowizConfiguration;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;

public class PersonHibernateBundle extends HibernateBundle<hellowizConfiguration> {

    public PersonHibernateBundle() {
        super(Person.class); // Specify the entity class (Person) to be mapped by Hibernate
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(hellowizConfiguration configuration) {
        return configuration.getDataSourceFactory();
    }
}
