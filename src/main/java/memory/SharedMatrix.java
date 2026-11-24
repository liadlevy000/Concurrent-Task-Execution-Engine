package memory;

import java.lang.reflect.Array;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        // TODO: initialize empty matrix
        vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        // TODO: construct matrix as row-major SharedVectors
        if (matrix == null)
            throw new IllegalArgumentException("matrix is null");
        int rows = matrix.length;
        this.vectors = new SharedVector[rows];
        if (rows == 0)
            return;

        int cols = matrix[0].length;
        for (int i = 0; i < rows; i++) {
            if (matrix[i] == null || matrix[i].length != cols)
                throw new IllegalArgumentException("matrix isn't valid input");
            vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }
    }

    public void loadRowMajor(double[][] matrix) {
        // TODO: replace internal data with new row-major matrix
        if (matrix == null)
            throw new IllegalArgumentException("matrix is null");
        int rows = matrix.length;
        SharedVector[] newVec = new SharedVector[rows];
        if (rows == 0) {
            this.vectors = newVec;
            return;
        }
        int cols = matrix[0].length;
        for (int i = 0; i < rows; i++) {
            if (matrix[i] == null || matrix[i].length != cols)
                throw new IllegalArgumentException("matrix isn't valid input");
            newVec[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }

        this.vectors = newVec;
    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
        if (matrix == null)
            throw new IllegalArgumentException("matrix is null");
        int rows = matrix.length;
        if (rows == 0) {
            this.vectors = new SharedVector[0];
            return;
        }
        int cols = matrix[0].length;
        for (int i = 0; i < rows; i++) {
            if (matrix[i] == null || matrix[i].length != cols)
                throw new IllegalArgumentException("matrix isn't valid input");
        }
        SharedVector[] newVec = new SharedVector[cols];
        for (int j = 0; j < cols; j++) {
            double[] currentVector = new double[rows];
            for (int k = 0; k < rows; k++) {
                currentVector[k] = matrix[k][j];
            }
            newVec[j] = new SharedVector(currentVector, VectorOrientation.COLUMN_MAJOR);
        }
        this.vectors = newVec;
    }

    public double[][] readRowMajor() {
        // TODO: return matrix contents as a row-major double[][]
        if (vectors == null){
            throw new IllegalArgumentException("vectors is null");
        }
        SharedVector[] local = vectors;
        int rows = local.length;
        if (rows == 0){
            return new double[0][0];
        }

        acquireAllVectorReadLocks(local);
        double[][] matrix = null;
        try {
            VectorOrientation first = local[0].getOrientation();
            for (int i = 1; i < rows; i++) {
                if(local[i].getOrientation() != first){
                    throw new IllegalArgumentException("vectors do not match");
                }
            }
            int cols = local[0].length();
            if(first == VectorOrientation.ROW_MAJOR){
                matrix = new double[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        matrix[i][j] = local[i].get(j);
                    }
                }
            }
            else{
                rows = local[0].length();
                cols = local.length;
                matrix = new double[rows][cols];
                for (int j=0; j < rows; j++) {
                    for (int i = 0; i < cols; i++) {
                        matrix[j][i] = local[i].get(j);
                    }
                }
            }
        } finally {
            releaseAllVectorReadLocks(local);
        }
        return matrix;
    }

    public SharedVector get(int index) {
        // TODO: return vector at index
        SharedVector[] local = vectors;
        int rows = local.length;
        if(index < 0 || index >= rows){
            throw new IllegalArgumentException("the index is out of range");
        }
            return local[index];
    }

    public int length() {
        // TODO: return number of stored vectors
        SharedVector[] local = vectors;
        return local.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
        SharedVector[] local = vectors;
        this.acquireAllVectorReadLocks(local);
        try{
            if(local.length == 0){
                throw new IllegalArgumentException("vectors is empty");
            }
            VectorOrientation first = local[0].getOrientation();
            return first;
        }
        finally{
            releaseAllVectorReadLocks(local);
        }
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        for(int i = 0; i < vecs.length; i++){
            vecs[i].readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        for(int i = vecs.length-1; i >= 0; i--){
            vecs[i].readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        for(int i = 0; i < vecs.length; i++){
            vecs[i].writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
        for(int i = vecs.length-1; i >= 0; i--){
            vecs[i].writeUnlock();
        }
    }

}
