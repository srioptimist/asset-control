package sri.optimist;


import org.junit.*;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataTranslatorTest {

    private DataTranslator dataTranslator;
    private static Properties props = new Properties();

    @BeforeClass
    public static void init() {
        InputStream in = DataTranslatorTest.class.getClass().getResourceAsStream("/config.properties");
        try {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Before
    public void setUp() {
        dataTranslator = new DataTranslator();
    }

    @Test
    public void testTranslate() {
        dataTranslator.translate();
        File outputFile = new File(props.getProperty(Constants.RESULT_FILE_NAME));
        assertNotNull(outputFile);
        Scanner scanner = null;
        try {
            scanner = new Scanner(outputFile);
            assertEquals("OURID\tOURCOL1\tOURCOL3",scanner.nextLine());
            assertEquals("OURIDXXX\tVAL21\tVAL23",scanner.nextLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  finally {
            scanner.close();
        }
    }


}
