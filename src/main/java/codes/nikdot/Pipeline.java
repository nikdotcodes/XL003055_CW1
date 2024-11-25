package codes.nikdot;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

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

    @Option(names = {"-c", "--output-csv"}, defaultValue = "false", description = "Output CSV file for Topic Modelling Results.")
    boolean outputCSV;

    @Option(names = {"-p", "--output-parquet"}, defaultValue = "false", description = "Output Parquet file for Topic Modelling Results.")
    boolean outputParquet;

    @Option(names = {"-d", "--output-duckdb"}, defaultValue = "false", description = "Output DuckDB file for Topic Modelling Results.")
    boolean outputDuckDB;

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
     * The main logic of the pipeline. It performs the following steps:<br>
     * 1. Creates a temporary JSON file name by replacing the .txt extension of the input file with _temp.json.<br>
     * 2. Initializes instances of B1TextLoader and B2Lemmatiser.<br>
     * 3. Sets the input and output file paths for both the text loader and the lemmatiser.<br>
     * 4. Prints a message indicating the start of processing.<br>
     * 5. Loads the text file and saves its content to the temporary JSON file.<br>
     * 6. Lemmatises the content of the temporary JSON file and saves the result to the output file.<br>
     * 7. Prints a message indicating the completion of processing.
     */
    @Override
    public void run() {
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

        System.out.println("Loading stop words...");
        lem.loadStopWords();
        lem.startLemmanisation();

        B3DescriptiveStatistics descriptiveStatistics = new B3DescriptiveStatistics();
        descriptiveStatistics.setLemmasFile(outputFile);
        descriptiveStatistics.setCorpusWordCountFile(corpusWordCountFile);
        descriptiveStatistics.setDocumentWordCountFile(documentWordCountFile);
        descriptiveStatistics.startCreatingStatistics();

        B4TopicModelling topicModelling = new B4TopicModelling();
        topicModelling.setLemmaFile(outputFile);
        topicModelling.setTopicModelFile(topicModelFile);
        topicModelling.startTopicModelling();

        System.out.println("Processing complete.");
        System.out.println("Exporting results...");

        B5ResultsExport results = new B5ResultsExport();
        results.setFileTemplate(outputFile);
        results.setModelTopWords(topicModelling.modelTopWords);
        results.setOutputCSV(outputCSV);
        results.setOutputParquet(outputParquet);
        results.setOutputDuckDB(outputDuckDB);
        results.setOutputJSON(outputJSON);

        results.exportModelResults();

        System.out.println("Export complete.");
    }
}