package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        if(vector == null){
            throw new IllegalArgumentException("vector is null");
        }
        if(orientation == null){
            throw new IllegalArgumentException("orientation is null");
        }
        this.vector = vector;
        this.orientation = orientation;
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        if(index<0 || index >= vector.length){
            throw new IllegalArgumentException("index out of bounds");
        }
        readLock();
        try{
            return vector[index];
        } finally {
            readUnlock();
        }
    }

    public int length() {
        // TODO: return vector length
        return vector.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        readLock();
        try{
            return orientation;    
        } finally {
            readUnlock();
        }
    }

    public void writeLock() {
        // TODO: acquire write lock
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        // TODO: release write lock
        lock.writeLock().unlock();
    }

    public void readLock() {
        // TODO: acquire read lock
        lock.readLock().lock();
    }

    public void readUnlock() {
        // TODO: release read lock
        lock.readLock().unlock();
    }

    public void transpose() {
        // TODO: transpose vector
        writeLock();
        try{
            if(orientation == VectorOrientation.ROW_MAJOR){
                orientation = VectorOrientation.COLUMN_MAJOR;
            }
            else{
                orientation = VectorOrientation.ROW_MAJOR;
            }
        } finally {
            writeUnlock();
        }
    }

    public void add(SharedVector other) {
        // TODO: add two vectors
        if(other == null){
            throw new IllegalArgumentException("other is null");
        }
        int thisid = System.identityHashCode(this);
        int otherid = System.identityHashCode(other);
        if(thisid<otherid){
            this.writeLock();
            other.readLock();
        }
        else{
            other.readLock();
            this.writeLock();
        }

        try{
            if(this.orientation != other.orientation ){
                throw new IllegalArgumentException("orientation mismatch");
            }
            if(this.vector.length != other.vector.length){
                throw new IllegalArgumentException("vector length mismatch");
            }
            for(int i = 0; i < this.vector.length; i++){
                this.vector[i] += other.vector[i];
            }
        } finally {
            if(thisid<otherid){
                other.readUnlock();
                this.writeUnlock();
            }
            else{
                this.writeUnlock();
                other.readUnlock();
            }

        }
    }

    public void negate() {
        // TODO: negate vector
        writeLock();
        try{
            for(int i = 0; i < vector.length; i++){
                vector[i] = -vector[i];
            }
        } finally {
            writeUnlock();
        }
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        if(other == null){
            throw new IllegalArgumentException("other is null");
        }
        int thisid = System.identityHashCode(this);
        int otherid = System.identityHashCode(other);
        if(thisid<otherid){
            this.writeLock();
            other.readLock();
        }
        else{
            other.readLock();
            this.writeLock();
        }
        double dotProduct = 0;
        try {
            if(this.orientation != VectorOrientation.ROW_MAJOR || other.orientation != VectorOrientation.COLUMN_MAJOR ){
                throw new IllegalArgumentException("orientation mismatch");
            }
            if(this.vector.length != other.vector.length){
                throw new IllegalArgumentException("vector length mismatch");
            }
            for(int i = 0; i < this.vector.length; i++){
                dotProduct += this.vector[i] * other.vector[i];
            }
        } finally {
            if(thisid<otherid){
                other.readUnlock();
                this.writeUnlock();
            }
            else{
                this.writeUnlock();
                other.readUnlock();
            }
        }
        return dotProduct;
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        if(matrix == null){
            throw new IllegalArgumentException("matrix is null");
        }
        this.writeLock();
        try{
            double[][] local = matrix.readRowMajor();
            matrix.loadColumnMajor(local);
            double[] replace = new double[local[0].length];
            if(this.orientation == VectorOrientation.COLUMN_MAJOR){
                throw new IllegalArgumentException("column major mismatch");
            }
            if(this.vector.length != local.length){
                throw new IllegalArgumentException("vector length mismatch");
            }
            for(int i = 0; i < replace.length; i++){
                replace[i] = this.dot(matrix.get(i));
            }
            this.vector = replace;
        } finally {
            this.writeUnlock();
        }
    }


    /*public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        if(matrix == null){
            throw new IllegalArgumentException("matrix is null");
        }
        this.writeLock();
        try{
            double[][] local = matrix.readRowMajor();
            double[] replace = new double[local[0].length];
            for(int i = 0; i < replace.length; i++){
                replace[i] = 0;
            }
            if(this.orientation == VectorOrientation.COLUMN_MAJOR){
                throw new IllegalArgumentException("column major mismatch");
            }
            if(this.vector.length != local.length){
                throw new IllegalArgumentException("vector length mismatch");
            }
            for(int i = 0; i < local.length; i++){
                for(int j = 0; j < local[i].length; j++){
                    replace[j] += this.vector[i] * local[i][j];
                }
            }
            this.vector = replace;
        } finally {
            this.writeUnlock();
        }
    } */
}
