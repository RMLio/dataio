package be.ugent.idlab.knows.dataio.access;

import java.util.Arrays;
import java.util.List;

/*
    NOTE: The Oracle, Denodo Platform and Athena drivers have to be installed manually, because they are not on Maven due to licensing.
 */
public enum DatabaseType {

    MYSQL("MySQL",
            "mysql:",
            "mysql",
            "com.mysql.cj.jdbc.Driver"),
    POSTGRES("PostgreSQL",
            "postgresql:",
            "postgres",
            "org.postgresql.Driver"),
    SQL_SERVER("Microsoft SQL Server",
            "sqlserver:",
            "sqlserver",
            "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    ORACLE("Oracle",
            "oracle:thin:@",
            "oracle",
            "oracle.jdbc.OracleDriver"),
    DB2("IBM DB2",
            "as400:",
            "ibm",
            "com.ibm.as400.access.AS400JDBCDriver"),
    H2("H2",
            "h2:tcp:",
            "h2",
            "org.h2.Driver"),
    DENODO("Denodo",
            "vdb:",
            "denodo",
            "com.denodo.vdp.jdbc.Driver"),
    ATHENA("Athena",
            "awsathena:",
            "athena",
            "com.simba.athena.jdbc.Driver"),
    SNOWFLAKE("Snowflake",
            "snowflake:",
            "snowflake",
            "net.snowflake.client.jdbc.SnowflakeDriver");

    private final String name;
    private final String jdbcPrefix;
    private final String driverSubstring;
    private final String driver;

    DatabaseType(String name, String jdbcPrefix, String driverSubstring, String driver) {
        this.name = name;
        this.jdbcPrefix = jdbcPrefix;
        this.driverSubstring = driverSubstring;
        this.driver = driver;
    }

    public String toString() {
        return this.name;
    }

    public String getJDBCPrefix() {
        return this.jdbcPrefix;
    }

    public String getDriverSubstring() {
        return this.driverSubstring;
    }

    public String getDriver() {
        return this.driver;
    }

    /*
        Retrieves the Database enum type from a given (driver) string
     */
    public static DatabaseType getDBtype(String db) {
        String dbLower = db.toLowerCase();
        List<DatabaseType> dbs = Arrays.asList(DatabaseType.values());

        int i = 0;

        while (i < dbs.size() && !dbLower.contains(dbs.get(i).getDriverSubstring())) {
            i++;
        }

        if (i < dbs.size()) {
            return dbs.get(i);
        } else {
            throw new Error("Couldn't find a driver for the given DB: " + db);
        }
    }
}
