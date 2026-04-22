package ru.vsu.cs.course1;

/**
 * Класс для хранения разобранных параметров командной строки.
 */
public class InputArgs {
    public String inputFile;
    public String outputFile;

    public InputArgs(String inputFile, String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    @Override
    public String toString() {
        return "InputArgs{inputFile='" + inputFile + "', outputFile='" + outputFile + "'}";
    }
}
