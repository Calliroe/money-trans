package ru.jtc.moneytrans;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.MountableFile;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = MoneyTransApplication.class)
public abstract class AbstractIntegrationTest {
    @Autowired
    protected TestRestTemplate restTemplate;
    private static GenericContainer postgres = new GenericContainer("postgres:10.7")
            .withExposedPorts(5432)
            .withEnv("POSTGRES_DB", "integration-tests-db_2")
            .withEnv("POSTGRES_USER", "postgres")
            .withEnv("POSTGRES_PASSWORD", "admin")
            .withCommand("-c log_statement=all")
            .withCopyFileToContainer(MountableFile.forClasspathResource("changelog.yml"), "/docker-entrypoint-initdb.d/changelog.yml");

    static {
        postgres.start();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        private static final String JDBC_URL =
                String.format("jdbc:postgresql://%s:%d/integration-tests-db_2", postgres.getContainerIpAddress(), postgres.getFirstMappedPort());


        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of(springApplicationProperties())
                    .applyTo(context.getEnvironment(), TestPropertyValues.Type.SYSTEM_ENVIRONMENT, "test");
        }

        private static String[] springApplicationProperties() {
            return new String[]{
                    "spring.datasource.url=" + JDBC_URL,
                    "spring.datasource.username=postgres",
                    "spring.datasource.password=admin",
                    "spring.liquibase.enabled=true",
                    "spring.liquibase.url=" + JDBC_URL,
                    "spring.liquibase.user=postgres",
                    "spring.liquibase.password=admin"
            };
        }
    }

}