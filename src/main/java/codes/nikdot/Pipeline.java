package codes.nikdot;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * The Pipeline class is the main entry point for the pipeline application.
 * It uses the Picocli library to handle command-line arguments and orchestrates
 * the process of loading a text file, lemmatising its content, and saving the output to a JSON file.
 */
@Command(name = "pipeline", mixinStandardHelpOptions = true, version = "pipeline 2024.1001",
        description = "Runs the pipeline to load a text file, lemmanise processing, and outputs a json file.")
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
     * The main logic of the pipeline. It performs the following steps:
     * 1. Creates a temporary JSON file name by replacing the .txt extension of the input file with _temp.json.
     * 2. Initializes instances of B1TextLoader and B2Lemmatiser.
     * 3. Sets the input and output file paths for both the text loader and the lemmatiser.
     * 4. Prints a message indicating the start of processing.
     * 5. Loads the text file and saves its content to the temporary JSON file.
     * 6. Lemmatises the content of the temporary JSON file and saves the result to the output file.
     * 7. Prints a message indicating the completion of processing.
     */
    @Override
    public void run() {
        String workingFile = inputFile.replace(".txt", "_temp.json");

        B1TextLoader loader = new B1TextLoader();
        B2Lemmatiser lem = new B2Lemmatiser();

        loader.setInputFile(inputFile);
        loader.setOutputFile(workingFile);
        lem.setInputFile(workingFile);
        lem.setOutputFile(outputFile);

        System.out.printf("Processing %s to %s%n", inputFile, outputFile);

        loader.loadTextFile();
        loader.saveDocumentsToJSON();

        lem.startLemmanisation();

        System.out.println("Processing complete.");
    }

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
}