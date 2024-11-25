package codes.nikdot;

import org.duckdb.DuckDBConnection;

import java.sql.DriverManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class B5ResultsExport {

    private String fileTemplate;

    private boolean outputCSV;
    private boolean outputParquet;
    private boolean outputDuckDB;
    private boolean outputJSON;

    private Object[][] modelTopWords;
    private DuckDBConnection conn;

    public static void main(String[] args) {
        System.out.println("Exporting results...");
    }

    public void setFileTemplate(String fileTemplate) {
        this.fileTemplate = fileTemplate;
    }

    public void setOutputCSV(boolean outputCSV) {
        this.outputCSV = outputCSV;
    }

    public void setOutputParquet(boolean outputParquet) {
        this.outputParquet = outputParquet;
    }

    public void setOutputDuckDB(boolean outputDuckDB) {
        this.outputDuckDB = outputDuckDB;
    }

    public void setOutputJSON(boolean outputJSON) {
        this.outputJSON = outputJSON;
    }

    public void setModelTopWords(Object[][] modelTopWords) {
        this.modelTopWords = modelTopWords;
    }

    public void exportModelResults() {
        System.out.println("Initialising working DuckDB...");
        String datePattern = "yyyyMMddHHmmss";
        DateFormat dateFormat = new SimpleDateFormat(datePattern);
        Date today = Calendar.getInstance().getTime();
        String nowAsString = dateFormat.format(today);

        String duckDBConnetion = "jdbc:duckdb:";

        StringBuilder tableCreateStringBuilder = new StringBuilder()
                .append("CREATE TABLE IF NOT EXISTS model_top_words (")
                .append("topic_id INT, ");
        for (int i = 1; i <= modelTopWords[0].length; i++) {
            tableCreateStringBuilder.append("word_").append(i).append(" VARCHAR, ");
        }
        tableCreateStringBuilder.append("generated_datekey BIGINT);");

        String tableCreateString = tableCreateStringBuilder.toString();
        String fileRoot = fileTemplate.replace(".json", "_ModelResults");

        if (outputDuckDB) {
            duckDBConnetion += fileRoot + ".duckdb";
        }

        try {
            this.conn = (DuckDBConnection) DriverManager.getConnection(duckDBConnetion);
        } catch (Exception e) {
            System.out.println("Error! Cannot create db engine");
            e.printStackTrace();
        }

        try {
            conn.createStatement().execute(tableCreateString);
        } catch (Exception e) {
            System.out.println("Error! Cannot create table");
            e.printStackTrace();
        }

        try (var appender = conn.createAppender(DuckDBConnection.DEFAULT_SCHEMA, "model_top_words")) {
            for (int topic = 0; topic < modelTopWords.length; topic++) {
                appender.beginRow();
                appender.append(topic);
                for (int i = 0; i < modelTopWords[topic].length; i++) {
                    appender.append(modelTopWords[topic][i].toString());
                }
                appender.append(nowAsString);
                appender.endRow();
            }
        } catch (Exception e) {
            System.out.println("Error! Cannot append data to table");
            e.printStackTrace();
        }

        if (outputDuckDB) {
            System.out.println("DuckDB table created and data appended successfully!");
        }

        if (outputCSV) {
            System.out.println("Exporting to CSV...");
            String csvOut = "COPY model_top_words TO '" + fileRoot + ".csv' (FORMAT CSV, HEADER);";
            try {
                conn.createStatement().execute(csvOut);
            } catch (Exception e) {
                System.out.println("Error! Cannot export to CSV");
                e.printStackTrace();
            }
        }

        if (outputParquet) {
            System.out.println("Exporting to Parquet...");
            String parquetOut = "COPY model_top_words TO '" + fileRoot + ".parquet' (FORMAT PARQUET);";
            try {
                conn.createStatement().execute(parquetOut);
            } catch (Exception e) {
                System.out.println("Error! Cannot export to Parquet");
                e.printStackTrace();
            }
        }

        if (outputJSON) {
            System.out.println("Exporting to JSON...");
            String jsonOut = "COPY model_top_words TO '" + fileRoot + ".json' (FORMAT JSON);";
            try {
                conn.createStatement().execute(jsonOut);
            } catch (Exception e) {
                System.out.println("Error! Cannot export to JSON");
                e.printStackTrace();
            }
        }

        try {
            conn.close();
        } catch (Exception e) {
            System.out.println("Error! Cannot close connection");
            e.printStackTrace();
        }
    }


}
