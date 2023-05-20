package eu.senla.userservice;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class ContainerTest implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @LocalServerPort
    public int port;
    private static final String IMAGE_VERSION = "postgres:11.1";
    private static final String DATABASE_NAME = "testdatabase";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "sa";

    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer(IMAGE_VERSION)
            .withDatabaseName(DATABASE_NAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD);

    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        postgreSQLContainer.start();
        TestPropertyValues.of(
                "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                "hibernate.connection.url=" + postgreSQLContainer.getJdbcUrl(),
                "hibernate.connection.username=" + postgreSQLContainer.getUsername(),
                "hibernate.connection.password=" + postgreSQLContainer.getPassword()
        ).applyTo(configurableApplicationContext.getEnvironment());
    }
}
