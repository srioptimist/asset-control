package sri.optimist;


import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class DataTranslator {

    List<List<String>> rows = new ArrayList<>();
    List<String> columns = new ArrayList<>();
    Map<String, String> columnMap = new HashMap<>();
    Map<String, String> rowMap = new HashMap<>();
    List<List<String>> result = new ArrayList<>();

    private static Properties props = new Properties();

    static {
        InputStream in = DataTranslator.class.getClass().getResourceAsStream("/config.properties");
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

    public void translate() {
        parseTemplateFile();
        parseColumnConfigFile();
        translateColumnsAndUpdateRows();
        parseRowConfigFile();
        filterRowsAndTranslate();
        writeResultToFile();
    }

    private void parseTemplateFile() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(getSource(props.getProperty(Constants.TEMPLATE_FILE_NAME)));
            if (!scanner.hasNextLine()) {
                throw new UnsupportedOperationException(); // Template file is empty
            }
            String firstLine = scanner.nextLine();
            columns = Arrays.asList(firstLine.split(Constants.TAB_SEPARATOR));
            while (scanner.hasNextLine()) {
                String[] rowValues = scanner.nextLine().split(Constants.TAB_SEPARATOR);
                List<String> row = new ArrayList<String>(Arrays.asList(rowValues));
                rows.add(row);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private void parseColumnConfigFile() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(getSource(props.getProperty(Constants.COLUMN_CONFIG_FILE_NAME)));
            while (scanner.hasNextLine()) {
                String[] columnValues = scanner.nextLine().split("\t");
                if (columnValues.length == 2) {
                    columnMap.put(columnValues[0], columnValues[1]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private void translateColumnsAndUpdateRows() {
        List<String> translatedColumns = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            if (columnMap.containsKey(columns.get(i))) {
                translatedColumns.add(columnMap.get(columns.get(i)));
            } else { // remove row values when column not found
                for (int j = 0; j < rows.size(); j++) {
                    rows.get(j).remove(i);
                }
            }
        }
        result.add(translatedColumns);
    }

    private void parseRowConfigFile() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(getSource(props.getProperty(Constants.ROW_CONFIG_FILE_NAME)));
            while (scanner.hasNextLine()) {
                String[] rowValues = scanner.nextLine().split("\t");
                rowMap.put(rowValues[0], rowValues[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private void filterRowsAndTranslate() {
        for (int i = 0; i < rows.size(); i++) {
            String key = rows.get(i).get(0);
            if (rowMap.containsKey(key)) {
                rows.get(i).remove(0);
                rows.get(i).add(0, rowMap.get(key)); // translate row_id to vendor values
                result.add(rows.get(i));
            }
        }
    }

    private void writeResultToFile() {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(props.getProperty(Constants.RESULT_FILE_NAME));
            for (int i = 0; i < result.size(); i++) {
                for (int j = 0; j < result.get(i).size(); j++) {
                    String value = (j + 1 < result.get(i).size()) ? result.get(i).get(j).concat(Constants.TAB_SEPARATOR) : result.get(i).get(j);
                    fileWriter.write(value);
                }
                fileWriter.write(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private File getSource(String fileName) throws URISyntaxException {
        return new File(this.getClass().getClassLoader().getResource(fileName).toURI());
    }
}
