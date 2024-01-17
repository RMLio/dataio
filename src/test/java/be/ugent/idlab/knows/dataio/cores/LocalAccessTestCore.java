package be.ugent.idlab.knows.dataio.cores;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class LocalAccessTestCore extends TestCore {


    public String getResultUTF8(Access access, Charset encoding) throws FileNotFoundException {
        InputStream input;
        try {
            input = access.getInputStream();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, encoding));
        StringBuilder string_input = new StringBuilder();

        // read in initial line, followed by the rest
        try {
            String str = reader.readLine();
            if (str != null) {
                string_input.append(str);
            }
            reader.lines().forEach(l -> string_input.append('\n').append(l));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return string_input.toString();
    }

    public String getInput(Access access) {
        byte[] b;
        try {
            b = access.getInputStream().readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Charset charset = ((LocalFileAccess) access).getEncoding();

        return new String(b, charset);
    }

    public String getInput(Path path, Charset encoding) throws IOException {
        return Files.readString(path, encoding);
    }

    public String readWithUTF8(Path file) {
        StringBuilder text = new StringBuilder();
        try (Stream<String> s = Files.lines(file, StandardCharsets.UTF_8)) {
            s.forEach(l -> {
                System.out.println(l);
                text.append(l).append("\n");
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return text.toString();
    }
}
