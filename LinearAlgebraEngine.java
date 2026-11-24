package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.ArrayList;
import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // TODO: create executor with given thread count
        executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        try {
            computationRoot.associativeNesting();
            ComputationNode curr;
            while((curr = computationRoot.findResolvable())!=null) {
                loadAndCompute(curr);
                curr.resolve(leftMatrix.readRowMajor());
            }
            return computationRoot;
        }finally {
            try{
                executor.shutdown();
                System.out.println(getWorkerReport());
            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        this.leftMatrix.loadRowMajor(node.getChildren().get(0).getMatrix());
        if (node.getChildren().size() > 1) {
            this.rightMatrix.loadRowMajor(node.getChildren().get(1).getMatrix());
        }
        switch (node.getNodeType()){
            case ADD -> executor.submitAll(createAddTasks());
            case MULTIPLY -> executor.submitAll(createMultiplyTasks());
            case NEGATE -> executor.submitAll(createNegateTasks());
            case TRANSPOSE -> executor.submitAll(createTransposeTasks());
        }
    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        if (leftMatrix.length() > 0 && leftMatrix.getOrientation() == VectorOrientation.COLUMN_MAJOR) {
            leftMatrix.loadRowMajor(leftMatrix.readRowMajor());
        }
        if (rightMatrix.length() > 0 && rightMatrix.getOrientation() == VectorOrientation.COLUMN_MAJOR) {
            rightMatrix.loadRowMajor(rightMatrix.readRowMajor());
        }

        int rows = leftMatrix.length();
        if (rows != rightMatrix.length()) {
            throw new IllegalArgumentException("Matrix row counts do not match");
        }
        if (rows == 0) {
            return List.of(); // nothing to do
        }

        int cols = leftMatrix.get(0).length();
        if (cols != rightMatrix.get(0).length()) {
            throw new IllegalArgumentException("Matrix column counts do not match");
        }

        List<Runnable> tasks = new ArrayList<>(rows);
        for (int i = 0; i < rows; i++) {
            final int row = i;
            tasks.add(() -> {
                // Each task works on one row
                SharedVector l = leftMatrix.get(row);
                SharedVector r = rightMatrix.get(row);
                l.add(r);
            });
        }
        return tasks;
    }

    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
        if (leftMatrix.length() > 0 && leftMatrix.getOrientation() == VectorOrientation.COLUMN_MAJOR) {
            leftMatrix.loadRowMajor(leftMatrix.readRowMajor());
        }
        if (rightMatrix.length() > 0 && rightMatrix.getOrientation() == VectorOrientation.COLUMN_MAJOR) {
            rightMatrix.loadRowMajor(rightMatrix.readRowMajor());
        }
        int rowsL = leftMatrix.length();
        int rowsR = rightMatrix.length();
        if(rowsL == 0 && rowsR == 0) {
            return List.of();
        }
        if (rowsL == 0 || rowsR == 0) {
            throw new IllegalArgumentException("Matrix row counts do not match");
        }
        int colsL = leftMatrix.get(0).length();
        if (rowsR != colsL)
            throw new IllegalArgumentException("Matrix column counts do not match");
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < rowsL; i++) {
            final int row = i;
            tasks.add(() -> {
                // Each task works on one row
                SharedVector l = leftMatrix.get(row);
                l.vecMatMul(rightMatrix);
            });
        }
        return tasks;
    }

    public List<Runnable> createNegateTasks() {
        // TODO: return tasks that negate rows
        if (leftMatrix.length() > 0 && leftMatrix.getOrientation() == VectorOrientation.COLUMN_MAJOR) {
            leftMatrix.loadRowMajor(leftMatrix.readRowMajor());
        }
        int rows = leftMatrix.length();
        if(rows == 0) {
            return List.of();
        }
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            final int row = i;
            tasks.add(() ->{
                SharedVector l = leftMatrix.get(row);
                l.negate();
            });
        }
        return tasks;
    }

    public List<Runnable> createTransposeTasks() {
        // TODO: return tasks that transpose rows
        if (leftMatrix.length() > 0 && leftMatrix.getOrientation() == VectorOrientation.ROW_MAJOR) {
            leftMatrix.loadColumnMajor(leftMatrix.readRowMajor());
        }
        int cols = leftMatrix.length();
        if (cols == 0) {
            return List.of();
        }
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            final int col = i;
            tasks.add(() -> {
                SharedVector l = leftMatrix.get(col);
                l.transpose();
            });
        }
        return tasks;
    }

    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return executor.getWorkerReport();
    }
}
