package com.hellowiz.api.db.support;

import com.codahale.metrics.MetricRegistry;
import de.softwareforge.testing.postgres.embedded.DatabaseInfo;
import de.softwareforge.testing.postgres.embedded.DatabaseManager;
import de.softwareforge.testing.postgres.embedded.EmbeddedPostgres;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.jackson.Jackson;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class DatabaseSupport {
    private DatabaseSupport() {
        throw new AssertionError("DatabaseSupport can not be instantiated");
    }

    public static void withPgDatabase(Consumer<Jdbi> jdbiConsumer) throws Exception {
        // TODO: Debug failure when trying to initialize an embedded PostgreSQL database using the initdb command.
        // The initdb command is used to create a new PostgreSQL database cluster, and it seems that something went wrong during this initialization process.
        try (DatabaseManager manager = DatabaseManager.singleDatabase()
            // same as EmbeddedPostgres.defaultInstance()
            .withInstancePreparer(EmbeddedPostgres.Builder::withDefaults)
            .build()
            .start()) {
            DatabaseInfo databaseInfo = manager.getDatabaseInfo();
            Jdbi jdbi = Jdbi.create(databaseInfo.asDataSource());
            jdbi.installPlugin(new SqlObjectPlugin())
                .installPlugin(new PostgresPlugin());

            jdbiConsumer.accept(jdbi);
        } catch (Exception e) {
            LoggerFactory.getLogger(DatabaseSupport.class).error("Failed to create test database", e);
            throw e;
        }
    }

//    public static void withH2Database(Consumer<Jdbi> jdbiConsumer) {

//    Create a Jdbi instance with H2 database for testing
//    TODO: need to add H2 database and the connection URL to test environment.
//    If you're using an in-memory database, it should be created and configured before running your tests.
//    Jdbi jdbi = Jdbi.create("jdbc:h2:mem:testdb;MODE=PostgreSQL");
//            jdbi.installPlugin(new SqlObjectPlugin())
//        .installPlugin(new PostgresPlugin());

//        Environment env = new Environment( "test-env");
//        dbi = new DBIFactory().build( env, getDataSourceFactory(), "test" );
//        dbi.registerArgumentFactory(new JodaDateTimeArgumentFactory());
//        dbi.registerMapper(new JodaDateTimeMapper(Optional.absent()));
//    }
//
//    static DataSourceFactory getDataSourceFactory()
//    {
//        DataSourceFactory dataSourceFactory = new DataSourceFactory();
//        dataSourceFactory.setDriverClass( "org.h2.Driver" );
//        dataSourceFactory.setUrl( "jdbc:h2:mem:testDb" );
//        dataSourceFactory.setUser( "sa" );
//        dataSourceFactory.setPassword( "" );
//        return dataSourceFactory;
//    }

}
