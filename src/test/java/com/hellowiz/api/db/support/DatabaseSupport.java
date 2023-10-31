package com.hellowiz.api.db.support;

import de.softwareforge.testing.postgres.embedded.DatabaseInfo;
import de.softwareforge.testing.postgres.embedded.DatabaseManager;
import de.softwareforge.testing.postgres.embedded.EmbeddedPostgres;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class DatabaseSupport {
    private DatabaseSupport() {
        throw new AssertionError("DatabaseSupport can not be instantiated");
    }

    public static void withDatabase(Consumer<Jdbi> jdbiConsumer) throws Exception {
        try (DatabaseManager manager = DatabaseManager.singleDatabase()
            // same as EmbeddedPostgres.defaultInstance()
            .withInstancePreparer(EmbeddedPostgres.Builder::withDefaults)
            .build()
            .start()) {
            DatabaseInfo databaseInfo = manager.getDatabaseInfo();
            Jdbi jdbi = Jdbi.create(databaseInfo.asDataSource());
            jdbi.installPlugin(new SqlObjectPlugin())
                .installPlugin(new PostgresPlugin());

//           // Create a Jdbi instance with H2 database for testing
            // TODO: need to add H2 database and the connection URL to test environment.
            //  If you're using an in-memory database, it should be created and configured before running your tests.
//            Jdbi jdbi = Jdbi.create("jdbc:h2:mem:testdb;MODE=PostgreSQL");
//            jdbi.installPlugin(new SqlObjectPlugin())
//                .installPlugin(new PostgresPlugin());

            jdbiConsumer.accept(jdbi);
        } catch (Exception e) {
            LoggerFactory.getLogger(DatabaseSupport.class).error("Failed to create test database", e);
            throw e;
        }
    }
}
