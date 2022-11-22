package be.ugent.idlab.knows.access;

import be.ugent.idlab.knows.access.utils.ContainerManager;
import be.ugent.idlab.knows.source.CSVSource;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.shaded.org.apache.commons.io.input.BOMInputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseTest {

    private static final String QUERY = "SELECT * FROM Patient";
    private final ContainerManager manager = new ContainerManager();

    @AfterEach
    public void afterTest() {
        JdbcDatabaseContainer<?> container = manager.getContainer();
        if (container != null && container.isRunning()) {
            container.stop();
        }
    }

    private void runTest(Access access, CSVSource expected) {
        try {
            List<CSVSource> actual = getCSVFromDB(access);
            Assertions.assertTrue(actual.contains(expected));

        } catch (SQLException | IOException e) {
            System.err.print("An error occurred during test execution: ");
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test()
    public void testPostgres() {
        manager.startPostgresContainer();
        manager.prepareDatabase("src/test/resources/db_setup/postgres_setup.sql");
        Access access = getRDBAccess(DatabaseType.POSTGRES);

        String[] header = new String[]{"id", "firstname", "lastname", "sex", "weight", "height", "birthdate", "entrancedate", "paidinadvance", "photo"};
        String[] expectedValues = new String[]{"10", "Monica", "Geller", "female", "80.25", "1.65", "1981-10-10", "2009-10-10 12:12:22", "f", "89504E470D0A1A0A0000000D49484452000000050000000508060000008D6F26E50000001C4944415408D763F9FFFEBFC37F062005C3201284D031F18258CD04000EF535CBD18E0E1F0000000049454E44AE426082"};
        CSVSource expected = new CSVSource(header, expectedValues, access.getDataTypes());

        runTest(access, expected);
    }

    @Test
    public void testMySQL() {
        manager.startMySQLContainer();
        manager.prepareDatabase("src/test/resources/db_setup/mysql_setup.sql");
        Access access = getRDBAccess(DatabaseType.MYSQL);

        String[] header = new String[]{"ID", "FirstName", "LastName", "Sex", "Weight", "Height", "BirthDate", "EntranceDate", "PaidInAdvance", "Photo"};
        String[] expectedValues = new String[]{"10", "Monica", "Geller", "female", "80.25", "1.65", "1981-10-10", "2009-10-10 12:12:22", "0", "89504E470D0A1A0A0000000D49484452000000050000000508060000008D6F26E50000001C4944415408D763F9FFFEBFC37F062005C3201284D031F18258CD04000EF535CBD18E0E1F0000000049454E44AE426082"};
        CSVSource expected = new CSVSource(header, expectedValues, access.getDataTypes());

        runTest(access, expected);
    }

    @Test
    public void testMSSQL() {
        manager.startMSSQLContainer();
        manager.prepareDatabase("src/test/resources/db_setup/mssql_setup.sql");
        Access access = getRDBAccess(DatabaseType.SQL_SERVER);

        String[] header = new String[]{"ID", "FirstName", "LastName", "Sex", "Weight", "Height", "BirthDate", "EntranceDate", "PaidInAdvance", "Photo"};
        String[] expectedValues = new String[]{"10", "Monica", "Geller", "female", "80.25", "1.65", "1981-10-10", "2009-10-10 12:12:22.0", "0", "383935303445343730443041314130413030303030303044343934383434"};
        CSVSource expected = new CSVSource(header, expectedValues, access.getDataTypes());

        runTest(access, expected);
    }

    @Test
    public void testOracle() {
        manager.startOracleContainer();
        manager.prepareDatabase("src/test/resources/db_setup/oracle_setup.sql");
        Access access = getRDBAccess(DatabaseType.ORACLE);

        CSVSource expected = new CSVSource(
                new String[]{"ID", "NAME"},
                new String[]{"10", "Venus"},
                access.getDataTypes()
        );

        runTest(access, expected);
    }

    private Access getRDBAccess(DatabaseType type) {
        return new RDBAccess(
                this.manager.getDbURL(),
                type,
                this.manager.getContainer().getUsername(),
                this.manager.getContainer().getPassword(),
                QUERY,
                "text/csv");
    }

    private List<CSVSource> getCSVFromDB(Access access) throws SQLException, IOException {
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
                    .map(r -> new CSVSource(header, r, access.getDataTypes()))
                    .collect(Collectors.toList());
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }
}
