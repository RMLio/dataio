package be.ugent.idlab.knows.dataio.access.cores;

import be.ugent.idlab.knows.dataio.access.Access;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import org.jopendocument.dom.spreadsheet.MutableCell;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class LocalAccessTestCore extends TestCore {


    public String getResultUTF8(Access access, Charset encoding) throws SQLException, FileNotFoundException, ClassNotFoundException {
        InputStream input;
        try {
            input = access.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, encoding));
        StringBuilder string_input = new StringBuilder();

        String str;
        while (true) {
            try {
                if ((str = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            string_input.append(str).append("\n");
        }
        return string_input.toString();
    }

    public String getResultInputStream(Access access, Charset encoding) throws SQLException, FileNotFoundException {
        InputStream input;
        byte[] b;
        try {
            input = access.getInputStream();
            b = input.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String string_input;
        string_input = new String(b, StandardCharsets.UTF_8);
        return string_input;
    }

    public String readWithUTF8(Path file) {
        StringBuilder text = new StringBuilder();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file.toFile()), StandardCharsets.UTF_8))) {
            String line;
            while ((line = bf.readLine()) != null) {
                text.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return text.toString();
    }

    public String readExcel(File file) throws FileNotFoundException {
        String data = "";
        FileInputStream fis = new FileInputStream(file);
        //creating workbook instance that refers to .xls file
        XSSFWorkbook wb = null;
        try {
            wb = new XSSFWorkbook(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //creating a Sheet object to retrieve object
        XSSFSheet sheet = wb.getSheetAt(0);
        for (Row row : sheet) {
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (cell.getCellType()) {
                    case STRING:    //field that represents string cell type
                        data += cell.getStringCellValue() + ",";
                        break;
                    case NUMERIC:    //field that represents number cell type
                        double temp = cell.getNumericCellValue();
                        if (temp % 10 > 0) {
                            data += temp + ",";
                        } else {
                            data += (int) temp + ",";
                        }
                        break;
                    case BOOLEAN:
                        data += cell.getBooleanCellValue() + ",";
                    default:
                        break;
                }
            }
            data = data.replaceAll(",$", "\n");
        }
        return data;
    }

    public String readODS(File file) throws IOException {
        SpreadSheet spreadsheet;
        spreadsheet = SpreadSheet.createFromFile(file);
        //Get row count and column count
        int nColCount = spreadsheet.getSheet(0).getColumnCount();
        int nRowCount = spreadsheet.getSheet(0).getRowCount();

        StringBuilder data = new StringBuilder();
        //Iterating through each row of the selected sheet
        MutableCell cell;
        for (int nRowIndex = 0; nRowIndex < nRowCount; nRowIndex++) {
            //Iterating through each column
            for (int nColIndex = 0; nColIndex < nColCount; nColIndex++) {
                cell = spreadsheet.getSheet(0).getCellAt(nColIndex, nRowIndex);
                cell.getTextValue();
                data.append(cell.getValue()).append(",");
            }
            data = new StringBuilder(data.toString().replaceAll(",$", "\n"));
        }
        return data.toString();
    }


    public boolean mightBeUTF8(InputStream stream) throws FileNotFoundException {
        byte[] inputBytes;
        try {
            inputBytes = stream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final String converted = new String(inputBytes, StandardCharsets.UTF_8);
        final byte[] outputBytes = converted.getBytes(StandardCharsets.UTF_8);
        return Arrays.equals(inputBytes, outputBytes);
    }

    public boolean isUTF8(InputStream is) throws FileNotFoundException {
        try {
            CharsetMatch[] charsetMatches = new CharsetDetector().setText(is).detectAll();
            List<String> type = Arrays.stream(charsetMatches).map(CharsetMatch::getName).collect(Collectors.toList());
            return type.contains("UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
