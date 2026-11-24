package memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SharedVectorTest {
    @Test
    void add_sameSizeVectors_addsCorrectly() {
        SharedVector a = new SharedVector(new double[]{1, 2},VectorOrientation.ROW_MAJOR);
        SharedVector b = new SharedVector(new double[]{3, 4},VectorOrientation.ROW_MAJOR);
        double[] expected = {4,6};
        a.add(b);
        for(int i = 0; i<expected.length ; i++){
            assertEquals(expected[i],a.get(i));
        }

    }

    @Test
    void add_diff_Orientation(){
        SharedVector a = new SharedVector(new double[]{1, 2},VectorOrientation.ROW_MAJOR);
        SharedVector b = new SharedVector(new double[]{3, 4},VectorOrientation.COLUMN_MAJOR);

        assertThrows(IllegalArgumentException.class,()->a.add(b));
    }

    @Test
    void add_diff_length(){
        SharedVector a = new SharedVector(new double[]{1, 2, 3},VectorOrientation.ROW_MAJOR);
        SharedVector b = new SharedVector(new double[]{3, 4},VectorOrientation.ROW_MAJOR);

        assertThrows(IllegalArgumentException.class,()->a.add(b));
    }

    @Test
    void add_null_vector(){
        SharedVector a = new SharedVector(new double[]{1, 2},VectorOrientation.ROW_MAJOR);
        SharedVector b = null;

        assertThrows(IllegalArgumentException.class,()->a.add(b));
    }

    @Test
    void add_both_column(){
        SharedVector a = new SharedVector(new double[]{1, 2, 3},VectorOrientation.COLUMN_MAJOR);
        SharedVector b = new SharedVector(new double[]{3, 4, 5},VectorOrientation.COLUMN_MAJOR);
        double[] expected = {4,6,8};
        a.add(b);
        for(int i = 0; i<expected.length ; i++){
            assertEquals(expected[i],a.get(i));
        }
    }

    @Test
    void negate(){
        SharedVector a = new SharedVector(new double[]{1, 2, 3},VectorOrientation.ROW_MAJOR);
        double[] expected = {-1,-2,-3};
        a.negate();
        for(int i = 0; i<expected.length ; i++){
            assertEquals(expected[i],a.get(i));
        }
    }

    @Test
    void vecMatCal_correctly_1(){
        SharedVector a = new SharedVector(new double[]{1, 2, 3},VectorOrientation.ROW_MAJOR);
        double[][] curr = {{1,2,3},{4,5,6},{7,8,9}};
        SharedMatrix b = new SharedMatrix(curr);
        double[] expected = {30,36,42};

        a.vecMatMul(b);
        for(int i = 0; i<expected.length ; i++){
            assertEquals(expected[i],a.get(i));
        }
    }

    @Test
    void vecMatCal_correctly_2(){
        SharedVector a = new SharedVector(new double[]{1, 2, 3},VectorOrientation.ROW_MAJOR);
        double[][] curr = {{1,2},{4,5},{7,8}};
        SharedMatrix b = new SharedMatrix(curr);
        double[] expected = {30,36};

        a.vecMatMul(b);
        for(int i = 0; i<expected.length ; i++){
            assertEquals(expected[i],a.get(i));
        }
    }

    @Test
    void vecMatCal_ColumnVector(){
        SharedVector a = new SharedVector(new double[]{1, 2, 3},VectorOrientation.COLUMN_MAJOR);
        double[][] curr = {{1,2},{4,5},{7,8}};
        SharedMatrix b = new SharedMatrix(curr);

        assertThrows(IllegalArgumentException.class,()->a.vecMatMul(b));
    }

    @Test
    void vecMatCal_Mismatch_Size(){
        SharedVector a = new SharedVector(new double[]{1, 2, 3},VectorOrientation.ROW_MAJOR);
        double[][] curr = {{1,2},{4,5}};
        SharedMatrix b = new SharedMatrix(curr);

        assertThrows(IllegalArgumentException.class,()->a.vecMatMul(b));
    }

    @Test
    void vecMatCal_Matrix_null(){
        SharedVector a = new SharedVector(new double[]{1, 2, 3},VectorOrientation.ROW_MAJOR);
        SharedMatrix b = null;

        assertThrows(IllegalArgumentException.class,()->a.vecMatMul(b));
    }

    @Test
    void transpose_Check(){
        SharedVector a = new SharedVector(new double[]{1, 2, 3},VectorOrientation.COLUMN_MAJOR);
        a.transpose();

        assertEquals(VectorOrientation.ROW_MAJOR,a.getOrientation());
    }

    @Test
    void get_Correct_index(){
        SharedVector a = new SharedVector(new double[]{1, 2, 3},VectorOrientation.COLUMN_MAJOR);
        double expected = 2;
        assertEquals(expected,a.get(1));
    }

    @Test
    void get_Incorrect_index(){
        SharedVector a = new SharedVector(new double[]{1, 2, 3},VectorOrientation.COLUMN_MAJOR);
        assertThrows(IllegalArgumentException.class,()->a.get(-1));
    }

    @Test
    void get_Length(){
        SharedVector a = new SharedVector(new double[]{1, 2, 3},VectorOrientation.COLUMN_MAJOR);
        assertEquals(3,a.length());
    }
}
