package net.woggioni.worth.serialization.binary;

import lombok.SneakyThrows;
import net.woggioni.worth.buffer.LookAheadTextInputStream;
import net.woggioni.worth.serialization.json.JSONParser;
import net.woggioni.worth.value.ObjectValue;
import net.woggioni.worth.xface.Parser;
import net.woggioni.worth.xface.Value;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JBONTest {

    private String[] testFiles = new String[]{"/test.json", "/wordpress.json"};

    private InputStream getTestSource(String filename) {
        return getClass().getResourceAsStream(filename);
    }

    @Test
    @SneakyThrows
    public void consistencyTest() {
        Value.Configuration cfg = Value.Configuration.builder()
                .objectValueImplementation(ObjectValue.Implementation.TreeMap).build();
        for (String testFile : testFiles) {
            Value parsedValue;
            try (InputStream is = getTestSource(testFile)) {
                Parser parser = new JSONParser(cfg);
                parsedValue = parser.parse(is);
            }
            byte[] dumpedJBON;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                JBONDumper.newInstance().dump(parsedValue, baos);
                dumpedJBON = baos.toByteArray();
            }
            Value reParsedValue;
            try (InputStream is = new ByteArrayInputStream(dumpedJBON)) {
                Parser parser = new JBONParser(cfg);
                reParsedValue = parser.parse(is);
            }
            Assert.assertEquals(parsedValue, reParsedValue);
            byte[] reDumpedJBON;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                JBONDumper.newInstance().dump(reParsedValue, baos);
                reDumpedJBON = baos.toByteArray();
            }
            Assert.assertArrayEquals(dumpedJBON, reDumpedJBON);
        }
    }

    @Test
    @SneakyThrows
    public void comparativeTest() {
        for (String testFile : testFiles) {
            Value originalValue = new JSONParser().parse(getTestSource(testFile));

            Path outputFile = Files.createTempFile(Paths.get("/tmp"), "worth", null);
            try (OutputStream os = new FileOutputStream(outputFile.toFile())) {
                JBONDumper jbonDumper = new JBONDumper();
                jbonDumper.dump(originalValue, os);
            }
            Value binarySerializedValue;
            try (InputStream is = new FileInputStream(outputFile.toFile())) {
                JBONParser jbonParser = new JBONParser();
                binarySerializedValue = jbonParser.parse(is);
            }
            Assert.assertEquals(originalValue, binarySerializedValue);
        }
    }
}
