package be.ugent.idlab.knows.dataio.access;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jopendocument.dom.spreadsheet.MutableCell;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static be.ugent.idlab.knows.dataio.utils.Utils.getHashOfString;
import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.io.FilenameUtils.getExtension;

/**
 * This class represents access to a local file.
 */
public class LocalFileAccess implements Access {

    private String path;
    private String basePath;
    private String type;
    private String encoding;

    /**
     * This constructor takes the path and the base path of a file.
     *
     * @param path     the relative path of the file.
     * @param basePath the used base path.
     */
    public LocalFileAccess(String path, String basePath, String type, String encoding) {
        this.path = path;
        this.basePath = basePath;
        this.encoding = encoding;
        this.type = type;
    }

    /**
     * This method returns the InputStream of the local file.
     *
     * @return an InputStream.
     * @throws FileNotFoundException
     */
    @Override
    public InputStream getInputStream() throws IOException {
        File file = new File(this.path);

        if (!file.isAbsolute()) {
            file = getFile(this.basePath, this.path);
        }

        if(Objects.equals("ods", this.type)) {
            String converted = new String(readODS(file).getBytes(), StandardCharsets.UTF_8);
            return IOUtils.toInputStream(converted, Charset.defaultCharset());
        }
        else if (Objects.equals("xlsx", this.type))
            return IOUtils.toInputStream(readExcel(file),StandardCharsets.UTF_8);

        return IOUtils.toInputStream(readWithEncoding(file.toPath(), "utf-8"),StandardCharsets.UTF_8);
    }

    public static String readODS(File file) throws IOException {
        SpreadSheet spreadsheet;
        spreadsheet = SpreadSheet.createFromFile(file);
        //Get row count and column count
        int nColCount = spreadsheet.getSheet(0).getColumnCount();
        int nRowCount = spreadsheet.getSheet(0).getRowCount();

        String data = "";
        //Iterating through each row of the selected sheet
        MutableCell cell;
        for(int nRowIndex = 0; nRowIndex < nRowCount; nRowIndex++) {
            //Iterating through each column
            for(int nColIndex = 0; nColIndex < nColCount; nColIndex++) {
                cell = spreadsheet.getSheet(0).getCellAt(nColIndex, nRowIndex);
                data += cell.getValue()+ ",";
            }
            data = data.replaceAll(",$", "\n");
        }
        return data;
    }

    public static String readExcel(File file) throws FileNotFoundException {
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
                        if(temp % 10 > 0){
                            data += temp + ",";
                        }else {
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

    public static String readWithEncoding(Path file, String encoding) throws FileNotFoundException {
        String text = "";
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file.toFile()), encoding))) {
            String line;
            while ((line = bf.readLine()) != null) {
                text += line + "\n";
            }
        } catch (IOException e) {
            throw new FileNotFoundException(e.getMessage());
        }
        return text;
    }

    @Override
    public InputStreamReader getInputStreamReader() throws FileNotFoundException, UnsupportedEncodingException {
        return null;
    }


    /**
     * This method returns the datatypes of the file.
     * This method always returns null, because the datatypes can't be determined from a local file for the moment.
     *
     * @return the datatypes of the file.
     */
    @Override
    public Map<String, String> getDataTypes() {

        return Map.of(getFullPath(), this.type);
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof LocalFileAccess) {
            LocalFileAccess access = (LocalFileAccess) o;
            return path.equals(access.getPath()) && basePath.equals(access.getBasePath());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getHashOfString(getFullPath());
    }

    /**
     * This method returns the path of the access.
     *
     * @return the relative path.
     */
    public Path getPath() {
        return Path.of(path);
    }

    /**
     * This method returns the base path of the access.
     *
     * @return the base path.
     */
    public String getBasePath() {
        return basePath;
    }

    @Override
    public String toString() {
        return getFullPath();
    }

    @Override
    public String getContentType() {
        return getExtension(this.path);
    }

    /**
     * Path to the resource the Access represents, be it the URL, remote address, filepath...
     */
    @Override
    public String getAccessPath() {
        File file = new File(this.path);

        if (!file.isAbsolute()) {
            file = getFile(this.basePath, this.path);
        }

        return file.getAbsolutePath();
    }

    private String getFullPath(){
        File file = new File(this.path);
        String fullPath = this.path;
        if (!file.isAbsolute()) {
            fullPath = this.basePath + this.path;
        }
        return fullPath;
    }
}
