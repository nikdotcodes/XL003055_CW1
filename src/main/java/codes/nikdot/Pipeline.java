package codes.nikdot;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The Pipeline class is the main entry point for the pipeline application.
 * It uses the Picocli library to handle command-line arguments and orchestrates
 * the process of loading a text file, lemmatising its content, and saving the output to a JSON file.
 */
@Command(name = "pipeline", mixinStandardHelpOptions = true, version = "pipeline 2024.1002", description = "Runs through " +
        "a series of pipeline blocks (B1 & B2) to read documents from a .txt file, lemmatise & tokenise the text, and " +
        "outputs the original & changes to a json file.")
public class Pipeline implements Runnable {

    /**
     * The path to the input text file to be processed.
     */
    @Parameters(paramLabel = "INPUT FILE", description = "The file to process.")
    String inputFile;

    /**
     * The path to the output JSON file where the processed content will be saved.
     */
    @Parameters(paramLabel = "OUTPUT FILE", description = "The file to write the output to.")
    String outputFile;

    /**
     * Flag to indicate whether to output results in CSV format.
     */
    @Option(names = {"-c", "--output-csv"}, defaultValue = "false", description = "Output CSV file for Topic Modelling Results.")
    boolean outputCSV;

    /**
     * Flag to indicate whether to output results in Parquet format.
     */
    @Option(names = {"-p", "--output-parquet"}, defaultValue = "false", description = "Output Parquet file for Topic Modelling Results.")
    boolean outputParquet;

    /**
     * Flag to indicate whether to output results in DuckDB format.
     */
    @Option(names = {"-d", "--output-duckdb"}, defaultValue = "false", description = "Output DuckDB file for Topic Modelling Results.")
    boolean outputDuckDB;

    /**
     * Flag to indicate whether to output results in JSON format.
     */
    @Option(names = {"-j", "--output-json"}, defaultValue = "false", description = "Output JSON file for Topic Modelling Results.")
    boolean outputJSON;

    /**
     * The entry point of the application. It uses Picocli to parse the command-line arguments
     * and execute the Pipeline command.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new Pipeline()).execute(args);
        System.exit(exitCode);
    }

    /**
     * The main logic of the pipeline. It performs the following steps:
     * 1. Creates a temporary JSON file name by replacing the .txt extension of the input file with _temp.json.
     * 2. Initializes instances of B1TextLoader and B2Lemmatiser.
     * 3. Sets the input and output file paths for both the text loader and the lemmatiser.
     * 4. Prints a message indicating the start of processing.
     * 5. Loads the text file and saves its content to the temporary JSON file.
     * 6. Lemmatises the content of the temporary JSON file and saves the result to the output file.
     * 7. Generate descriptive statistics for the lemmatised content.
     * 8. Perform topic modelling on the lemmatised content.
     * 9. Export the results of the topic modelling to various formats based on the specified flags.
     */
    @Override
    public void run() {
        String datePattern = "HH:mm:ss.SSS";
        DateFormat dateFormat = new SimpleDateFormat(datePattern);
        Date beginning = Calendar.getInstance().getTime();
        String beginningAsString = dateFormat.format(beginning);

        String workingFile = inputFile.replace(".txt", "_temp.json");
        String corpusWordCountFile = outputFile.replace(".json", "_CorpusWordCount.csv");
        String documentWordCountFile = outputFile.replace(".json", "_DocumentWordCount.csv");
        String topicModelFile = outputFile.replace(".json", "_TopicData.txt");

        B1TextLoader loader = new B1TextLoader();
        B2Lemmatiser lem = new B2Lemmatiser();

        loader.setInputFile(inputFile);
        loader.setOutputFile(workingFile);
        lem.setInputFile(workingFile);
        lem.setOutputFile(outputFile);

        System.out.printf("Processing %s to %s%n", inputFile, outputFile);

        loader.loadTextFile();
        loader.saveDocumentsToJSON();

        Date lemStart = Calendar.getInstance().getTime();
        String lemStartAsString = dateFormat.format(lemStart);

        System.out.println("Loading stop words...");
        lem.loadStopWords();
        lem.startLemmanisation();

        Date lemEnd = Calendar.getInstance().getTime();
        String lemEndAsString = dateFormat.format(lemEnd);

        B3DescriptiveStatistics descriptiveStatistics = new B3DescriptiveStatistics();
        descriptiveStatistics.setLemmasFile(outputFile);
        descriptiveStatistics.setCorpusWordCountFile(corpusWordCountFile);
        descriptiveStatistics.setDocumentWordCountFile(documentWordCountFile);
        descriptiveStatistics.startCreatingStatistics();

        Date createStatisticsStart = Calendar.getInstance().getTime();
        String createStatisticsStartAsString = dateFormat.format(createStatisticsStart);

        B4TopicModelling topicModelling = new B4TopicModelling();
        topicModelling.setLemmaFile(outputFile);
        topicModelling.setTopicModelFile(topicModelFile);
        topicModelling.startTopicModelling();

        Date createStatisticsEnd = Calendar.getInstance().getTime();
        String createStatisticsEndAsString = dateFormat.format(createStatisticsEnd);

        System.out.println("Processing complete.");
        System.out.println("Exporting results...");

        Date exportStart = Calendar.getInstance().getTime();
        String exportStartAsString = dateFormat.format(exportStart);

        B5ResultsExport results = new B5ResultsExport();
        results.setFileTemplate(outputFile);
        results.setModelTopWords(topicModelling.modelTopWords);
        results.setOutputCSV(outputCSV);
        results.setOutputParquet(outputParquet);
        results.setOutputDuckDB(outputDuckDB);
        results.setOutputJSON(outputJSON);

        results.exportModelResults();

        Date exportEnd = Calendar.getInstance().getTime();
        String exportEndAsString = dateFormat.format(exportEnd);

        System.out.println("Export complete.");
        System.out.println("--------------------");
        System.out.println("Pipeline Summary");
        System.out.println("--------------------");
        System.out.printf("Start: %s%n", beginningAsString);
        System.out.printf("Text Loading: %s - %s%n", beginningAsString, lemStartAsString);
        System.out.printf("Lemmatisation: %s - %s%n", lemStartAsString, lemEndAsString);
        System.out.printf("Descriptive Statistics: %s - %s%n", createStatisticsStartAsString, createStatisticsEndAsString);
        System.out.printf("Topic Modelling: %s - %s%n", createStatisticsEndAsString, exportStartAsString);
        System.out.printf("Exporting Results: %s - %s%n", exportStartAsString, exportEndAsString);
        System.out.printf("Total Time: %s%n", exportEnd.getTime() - beginning.getTime());
    }
}