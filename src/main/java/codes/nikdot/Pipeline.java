package codes.nikdot;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "pipeline", mixinStandardHelpOptions = true, version = "pipeline 2024.1001",
        description = "Runs the pipeline to load a text file, lemmanise processing, and outputs a json file.")
public class Pipeline implements Runnable {
    @Parameters(paramLabel = "INPUT FILE", description = "The file to process.")
    String inputFile;

    @Parameters(paramLabel = "OUTPUT FILE", description = "The file to write the output to.")
    String outputFile;

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

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Pipeline()).execute(args);
        System.exit(exitCode);
    }

}
