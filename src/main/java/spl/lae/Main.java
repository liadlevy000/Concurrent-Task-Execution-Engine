package spl.lae;
import java.io.IOException;
import java.text.ParseException;

import parser.ComputationNode;
import parser.InputParser;
import parser.OutputWriter;

public class Main {
    public static void main(String[] args) throws IOException {
        // TODO: main
        if (args.length != 3) {
            System.err.println("Usage: <numThreads> <input.json> <output.json>");
            return;
        }

        int numThreads;
        try {
            numThreads = Integer.parseInt(args[0]);
            if (numThreads <= 0) {
                OutputWriter.write("Illegal argument: numThreads must be positive", args[2]);
                return;
            }
        } catch (NumberFormatException | IOException e) {
            System.err.println("Invalid number of threads");
            return;
        }

        String inputPath = args[1];
        String outputPath = args[2];

        try {
            // parse input.json
            InputParser parser = new InputParser();
            ComputationNode root = parser.parse(inputPath);

            // run engine
            LinearAlgebraEngine engine = new LinearAlgebraEngine(numThreads);
            ComputationNode result = engine.run(root);

            // write result
            OutputWriter.write(result.getMatrix(), outputPath);

        } catch (ParseException e) {
            try {
                OutputWriter.write("Parse error: " + e.getMessage(), outputPath);
            } catch (IOException ignored) {}
        } catch (IllegalArgumentException e) {
            try {
                OutputWriter.write(e.getMessage(), outputPath);
            } catch (IOException ignored) {}
        } catch (Exception e) {
            try {
                OutputWriter.write("Unexpected error", outputPath);
            } catch (IOException ignored) {}
        }
//        // Test for example
//        System.out.println("Test for example");
//        InputParser parser = new InputParser();
//        String inputFile = "example.json";
//        ComputationNode root = null;
//        try {
//            root = parser.parse(inputFile);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//            return;
//        }
//        LinearAlgebraEngine engine = new LinearAlgebraEngine(10);
//        try{
//            engine.run(root);
//            OutputWriter.write(root.getMatrix(),"output.json" );
//        } catch (Exception e) {
//            OutputWriter.write(e.getMessage(),"output.json" );
//        }
//
//
//        // Test for example1
//        System.out.println("Test for example1");
//        InputParser parser1 = new InputParser();
//        String inputFile1 = "Examples/example1.json";
//        ComputationNode root1 = null;
//        try {
//            root1 = parser1.parse(inputFile1);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//            return;
//        }
//        LinearAlgebraEngine engine1 = new LinearAlgebraEngine(10);
//        try{
//            engine1.run(root1);
//            OutputWriter.write(root1.getMatrix(),"output1.json" );
//        } catch (Exception e) {
//            OutputWriter.write(e.getMessage(),"output1.json" );
//        }
//
//
//        // Test for example2
//        System.out.println("Test for example2");
//        InputParser parser2 = new InputParser();
//        String inputFile2 = "Examples/example2.json";
//        ComputationNode root2 = null;
//        try {
//            root2 = parser2.parse(inputFile2);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//            return;
//        }
//        LinearAlgebraEngine engine2 = new LinearAlgebraEngine(10);
//        try{
//            engine2.run(root2);
//            OutputWriter.write(root2.getMatrix(),"output2.json" );
//        } catch (Exception e) {
//            OutputWriter.write(e.getMessage(),"output2.json" );
//        }
//
//
//        // Test for example3
//        System.out.println("Test for example3");
//        InputParser parser3 = new InputParser();
//        String inputFile3 = "Examples/example3.json";
//        ComputationNode root3 = null;
//        try {
//            root3 = parser3.parse(inputFile3);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//            return;
//        }
//        LinearAlgebraEngine engine3 = new LinearAlgebraEngine(10);
//        try{
//            engine3.run(root3);
//            OutputWriter.write(root3.getMatrix(),"output3.json" );
//        } catch (Exception e) {
//            OutputWriter.write(e.getMessage(),"output3.json" );
//        }
//
//        // Test for example4
//        System.out.println("Test for example4");
//        InputParser parser4 = new InputParser();
//        String inputFile4 = "Examples/example4.json";
//        ComputationNode root4 = null;
//        try {
//            root4 = parser4.parse(inputFile4);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//            return;
//        }
//        LinearAlgebraEngine engine4 = new LinearAlgebraEngine(10);
//        try{
//            engine4.run(root4);
//            OutputWriter.write(root4.getMatrix(),"output4.json" );
//        } catch (Exception e) {
//            OutputWriter.write(e.getMessage(),"output4.json" );
//        }
//
//        // Test for example5
//        System.out.println("Test for example5");
//        InputParser parser5 = new InputParser();
//        String inputFile5 = "Examples/example5.json";
//        ComputationNode root5 = null;
//        try {
//            root5 = parser5.parse(inputFile5);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//            return;
//        }
//        LinearAlgebraEngine engine5 = new LinearAlgebraEngine(10);
//        try{
//            engine5.run(root5);
//            OutputWriter.write(root5.getMatrix(),"output5.json" );
//        } catch (Exception e) {
//            OutputWriter.write(e.getMessage(),"output5.json" );
//        }
//
//
//        // Test for example6
//        System.out.println("Test for example6");
//        InputParser parser6 = new InputParser();
//        String inputFile6 = "Examples/example6.json";
//        ComputationNode root6 = null;
//        try {
//            root6 = parser6.parse(inputFile6);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//            return;
//        }
//        LinearAlgebraEngine engine6 = new LinearAlgebraEngine(10);
//        try{
//            engine6.run(root6);
//            OutputWriter.write(root6.getMatrix(),"output6.json" );
//        } catch (Exception e) {
//            OutputWriter.write(e.getMessage(),"output6.json" );
//        }
//
//
//        // Test for example error add
//        System.out.println("Test for example error add");
//        InputParser parserErr = new InputParser();
//        String inputErr = "Examples/example_error_add.json";
//        ComputationNode rootErr;
//        try {
//            rootErr = parserErr.parse(inputErr);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return;
//        }
//        LinearAlgebraEngine engineErr = new LinearAlgebraEngine(10);
//        try {
//            engineErr.run(rootErr);
//            OutputWriter.write(rootErr.getMatrix(), "output_error_add.json");
//        } catch (Exception e) {
//            OutputWriter.write(e.getMessage(), "output_error_add.json");
//        }
//
//
//        // Test for example error mul
//        System.out.println("Test for example error mul");
//        InputParser parserErrMul = new InputParser();
//        String inputErrMul = "Examples/example_error_mul.json";
//        ComputationNode rootErrMul;
//        try {
//            rootErrMul = parserErrMul.parse(inputErrMul);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return;
//        }
//
//        LinearAlgebraEngine engineErrMul = new LinearAlgebraEngine(10);
//        try {
//            engineErrMul.run(rootErrMul);
//            OutputWriter.write(rootErrMul.getMatrix(), "output_error_mul.json");
//        } catch (Exception e) {
//            OutputWriter.write(e.getMessage(), "output_error_mul.json");
//        }


    }
}
