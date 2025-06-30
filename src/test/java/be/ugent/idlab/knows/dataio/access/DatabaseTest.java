package be.ugent.idlab.knows.dataio.access;

import be.ugent.idlab.knows.dataio.record.CSVRecord;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.*;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.io.input.BOMInputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Testcontainers
public class DatabaseTest {

    private static final String QUERY = "SELECT * FROM Patient";

    private void runTest(Access access, CSVRecord expected) {
        try {
            List<CSVRecord> actual = getCSVFromDB(access);
            Assertions.assertTrue(actual.contains(expected));

        } catch (SQLException | IOException e) {
            System.err.print("An error occurred during test execution: ");
            e.printStackTrace();
            Assertions.fail();
        }
    }

    private void initializeDatabase(String scriptPath, JdbcDatabaseContainer<?> container) {
        try (Connection conn = DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword())) {
            ScriptRunner runner = new ScriptRunner(conn);
            Reader reader = new BufferedReader(new FileReader(scriptPath));
            runner.setLogWriter(null); // ScriptRunner will output the contents of the SQL file to System.out by default

            runner.runScript(reader);
        } catch (SQLException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Access getRDBAccess(DatabaseType type, JdbcDatabaseContainer<?> container) {
        return new RDBAccess(
                container.getJdbcUrl(),
                type,
                container.getUsername(),
                container.getPassword(),
                QUERY,
                "text/csv");
    }

    private List<CSVRecord> getCSVFromDB(Access access) throws SQLException, IOException {
        try (BOMInputStream inputStream = new BOMInputStream(access.getInputStream());
             CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                     .withSkipLines(0)
                     .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                     .build()
        ) {

            List<String[]> records = reader.readAll();
            final String[] header = records.get(0);
            return records.subList(1, records.size()).stream()
                    .filter(r -> r.length != 0 && !(r.length == 1 && r[0] == null))
                    .map(r -> new CSVRecord(header, r, access.getDataTypes()))
                    .collect(Collectors.toList());
        } catch (CsvException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Due to the use of JUnit5 Testcontainers bindings, different test cases are separated into nested classes,
    //  in order to only start the required container
    @Nested
    class PostgreSQLTest {
        @Container
        private final PostgreSQLContainer<?> postgreSQLContainer = (PostgreSQLContainer<?>) new PostgreSQLContainer("postgres:latest")
                .withUsername("postgres")
                .withPassword("")
                .withDatabaseName("test")
                .withEnv("POSTGRES_HOST_AUTH_METHOD", "trust");

        @Test
        public void testPostgres() {
            System.out.println(postgreSQLContainer.getJdbcUrl());
            initializeDatabase("src/test/resources/db_setup/postgres_setup.sql", postgreSQLContainer);
            Access access = getRDBAccess(DatabaseType.POSTGRES, postgreSQLContainer);

            String[] header = new String[]{"id", "firstname", "lastname", "sex", "weight", "height", "birthdate", "entrancedate", "paidinadvance", "photo"};
            String[] expectedValues = new String[]{"10", "Monica", "Geller", "female", "80.25", "1.65", "1981-10-10", "2009-10-10 12:12:22", "f", "89504E470D0A1A0A0000000D49484452000000050000000508060000008D6F26E50000001C4944415408D763F9FFFEBFC37F062005C3201284D031F18258CD04000EF535CBD18E0E1F0000000049454E44AE426082"};
            CSVRecord expected = new CSVRecord(header, expectedValues, access.getDataTypes());

            runTest(access, expected);
        }
    }

    @Nested
    class MSSQLTest {
        @Container
        private final MSSQLServerContainer<?> mssqlServerContainer = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:latest")
                .acceptLicense()
                .withPassword("YourSTRONG!Passw0rd;");

        @Test
        public void testMSSQL() {
            initializeDatabase("src/test/resources/db_setup/mssql_setup.sql", mssqlServerContainer);
            Access access = getRDBAccess(DatabaseType.SQL_SERVER, mssqlServerContainer);

            String[] header = new String[]{"ID", "FirstName", "LastName", "Sex", "Weight", "Height", "BirthDate", "EntranceDate", "PaidInAdvance", "Photo"};
            String[] expectedValues = new String[]{"10", "Monica", "Geller", "female", "80.25", "1.65", "1981-10-10", "2009-10-10 12:12:22.0", "0", "383935303445343730443041314130413030303030303044343934383434"};
            CSVRecord expected = new CSVRecord(header, expectedValues, access.getDataTypes());

            runTest(access, expected);
        }
    }

    @Nested
    class MySQLTestcase {
        @Container
        private final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest")
                .withUsername("root")
                .withPassword("")
                .withConfigurationOverride("db_setup/mysql_override")
                .withEnv("allowPublicKeyRetrieval", "true")
                .withEnv("useSSL", "false");

        @Test
        public void testMySQL() {
            initializeDatabase("src/test/resources/db_setup/mysql_setup.sql", mySQLContainer);
            Access access = getRDBAccess(DatabaseType.MYSQL, mySQLContainer);

            String[] header = new String[]{"ID", "FirstName", "LastName", "Sex", "Weight", "Height", "BirthDate", "EntranceDate", "PaidInAdvance", "Photo"};
            String[] expectedValues = new String[]{"10", "Monica", "Geller", "female", "80.25", "1.65", "1981-10-10", "2009-10-10 12:12:22", "0", "89504E470D0A1A0A0000000D49484452000000050000000508060000008D6F26E50000001C4944415408D763F9FFFEBFC37F062005C3201284D031F18258CD04000EF535CBD18E0E1F0000000049454E44AE426082"};
            CSVRecord expected = new CSVRecord(header, expectedValues, access.getDataTypes());

            runTest(access, expected);
        }
    }

    @Nested
    class OracleTestcase {
        @Container
        private final OracleContainer oracleContainer = new OracleContainer("gvenzl/oracle-xe:latest")
                .withUsername("rmlmapper_test")
                .withPassword("test")
                .withEnv("NLS_LANG", "American_America.WE8ISO8859P1")
                .waitingFor(Wait.forLogMessage(".*DATABASE IS READY TO USE!.*", 1));

        @Test
        public void testOracle() {
            initializeDatabase("src/test/resources/db_setup/oracle_setup.sql", oracleContainer);
            Access access = getRDBAccess(DatabaseType.ORACLE, oracleContainer);

            CSVRecord expected = new CSVRecord(
                    new String[]{"ID", "NAME"},
                    new String[]{"10", "Venus"},
                    access.getDataTypes()
            );

            runTest(access, expected);
        }
    }
}
