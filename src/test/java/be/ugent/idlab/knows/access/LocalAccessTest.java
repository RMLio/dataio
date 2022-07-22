package be.ugent.idlab.knows.access;

import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.fail;

public class LocalAccessTest {

    @Test
    public void expect_error(){
        Access acces = new LocalFileAccess("not_existing_file.csv", "");
        try {
            acces.getInputStream();
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        fail("Access was supposed to throw an error");
    }
}