package be.ugent.idlab.knows.access.utils;

import be.ugent.idlab.knows.access.DatabaseType;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.testcontainers.containers.*;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class responsible for managing container instances
 */
public class ContainerManager {
    private static final String POSTGRES_TAG = "postgres:latest";
    private static final String MYSQL_TAG = "mysql:latest";
    private static final String MSSQL_TAG = "mcr.microsoft.com/mssql/server:latest";
    private static final String ORACLE_TAG = "gvenzl/oracle-xe:latest";
    private JdbcDatabaseContainer<?> container;
    private DatabaseType currentType;
    private String dbURL;

    public void startPostgresContainer() {
        stopPreviousInstance();

        this.container = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_TAG))
                .withUsername("postgres")
                .withPassword("")
                .withEnv("POSTGRES_HOST_AUTH_METHOD", "trust")
                .withDatabaseName("test");
        this.container.start();
        this.currentType = DatabaseType.POSTGRES;
        this.dbURL = this.container.getJdbcUrl();
    }

    public void startMySQLContainer() {
        stopPreviousInstance();
        this.container = new MySQLContainer<>(DockerImageName.parse(MYSQL_TAG))
                .withUsername("root")
                .withPassword("")
                .withEnv("allowPublicKeyRetrieval", "true")
                .withEnv("useSSL", "false")
                .withConfigurationOverride("db_setup/mysql_override");

        this.container.start();
        this.currentType = DatabaseType.MYSQL;
        this.dbURL = this.container.getJdbcUrl();
    }

    public void startMSSQLContainer() {
        stopPreviousInstance();
        this.container = new MSSQLServerContainer<>(DockerImageName.parse(MSSQL_TAG))
                .acceptLicense()
                .withPassword("YourSTRONG!Passw0rd;");
        this.container.start();
        this.currentType = DatabaseType.SQL_SERVER;
        this.dbURL = this.container.getJdbcUrl();
    }

    public void startOracleContainer() {
        stopPreviousInstance();
        this.container = new OracleContainer(DockerImageName.parse(ORACLE_TAG))
                .withUsername("rmlmapper_test")
                .withPassword("test")
                .withEnv("NLS_LANG", "American_America.WE8ISO8859P1")
                .waitingFor(Wait.forLogMessage(".*DATABASE IS READY TO USE!.*", 1));

        this.container.start();
        this.currentType = DatabaseType.ORACLE;
        this.dbURL = this.container.getJdbcUrl();
    }

    public void prepareDatabase(String path) {
        try (Connection conn = DriverManager.getConnection(this.dbURL, this.container.getUsername(), this.container.getPassword())) {
            ScriptRunner runner = new ScriptRunner(conn);
            Reader reader = new BufferedReader(new FileReader(path));
            runner.setLogWriter(null); // ScriptRunner will output the contents of the SQL file to System.out by default

            runner.runScript(reader);
        } catch (SQLException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void stopPreviousInstance() {
        if (this.container != null && this.container.isRunning()) {
            this.container.stop();
        }
    }

    public DatabaseType getCurrentType() {
        return currentType;
    }

    public JdbcDatabaseContainer<?> getContainer() {
        return container;
    }

    public String getDbURL() {
        return dbURL;
    }
}
