package memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SharedMatrixTest {

    @Test
    void epmty_Constructor(){
        SharedMatrix sharedMatrix = new SharedMatrix();
        assertEquals(0,sharedMatrix.length());
    }

    @Test
    void regular_Constructor_null(){
        assertThrows(IllegalArgumentException.class, () -> new SharedMatrix(null));
    }

    @Test
    void regular_Constructor_null_index(){
        double[][] a = {{1,2,3},null,{4,5,6}};
        assertThrows(IllegalArgumentException.class, () -> new SharedMatrix(a));
    }

    @Test
    void regular_Constructor_Matirx_not_good(){
        double[][] a = {{1,2,3},{4,5}};
        assertThrows(IllegalArgumentException.class, () -> new SharedMatrix(a));
    }

    @Test
    void regular_Constructor_Matirx_good(){
        double[][] a = {{1,2,3},{4,5,6}};
        SharedMatrix sharedMatrix = new SharedMatrix(a);
        for (int i = 0; i < sharedMatrix.length(); i++) {
            for (int j = 0; j < sharedMatrix.get(i).length(); j++) {
                assertEquals(a[i][j],sharedMatrix.get(i).get(j));
            }
        }
    }

    @Test
    void loadRowMajor_Matrix_null(){
        SharedMatrix sharedMatrix = new SharedMatrix();
        assertThrows(IllegalArgumentException.class, () -> sharedMatrix.loadRowMajor(null));
    }

    @Test
    void loadRowMajor_Matrix_empty(){
        SharedMatrix sharedMatrix = new SharedMatrix(new double[][]{{1,2,3},{4,5,6}});
        sharedMatrix.loadRowMajor(new double[][] {} );
        assertEquals(0,sharedMatrix.length());
    }

    @Test
    void loadRowMajor_Matrix_not_good(){
        SharedMatrix sharedMatrix = new SharedMatrix();
        double[][] a = {{1,2,3},{4,5}};
        assertThrows(IllegalArgumentException.class, () -> sharedMatrix.loadRowMajor(a));
    }

    @Test
    void loadRowMajor_Matrix_good(){
        SharedMatrix sharedMatrix = new SharedMatrix();
        double[][] a = {{1,2,3},{4,5,6}};
        sharedMatrix.loadRowMajor(a);
        for (int i = 0; i < sharedMatrix.length(); i++) {
            assertEquals(VectorOrientation.ROW_MAJOR, sharedMatrix.get(i).getOrientation());
            for (int j = 0; j < sharedMatrix.get(i).length(); j++) {
                assertEquals(a[i][j],sharedMatrix.get(i).get(j));
            }
        }
    }

    @Test
    void loadColumnMajor_Matrix_null(){
        SharedMatrix sharedMatrix = new SharedMatrix();
        assertThrows(IllegalArgumentException.class, () -> sharedMatrix.loadColumnMajor(null));
    }

    @Test
    void loadColumnMajor_Matrix_empty(){
        SharedMatrix sharedMatrix = new SharedMatrix(new double[][]{{1,2,3},{4,5,6}});
        sharedMatrix.loadColumnMajor(new double[][] {} );
        assertEquals(0,sharedMatrix.length());
    }

    @Test
    void loadColumnMajor_Matrix_not_good(){
        SharedMatrix sharedMatrix = new SharedMatrix();
        double[][] a = {{1,2,3},{4,5}};
        assertThrows(IllegalArgumentException.class, () -> sharedMatrix.loadColumnMajor(a));
    }

    @Test
    void loadColumnMajor_Matrix_good(){
        SharedMatrix sharedMatrix = new SharedMatrix();
        double[][] a = {{1,2,3},{4,5,6}};
        sharedMatrix.loadColumnMajor(a);
        for (int i = 0; i < sharedMatrix.length(); i++) {
            assertEquals(VectorOrientation.COLUMN_MAJOR, sharedMatrix.get(i).getOrientation());
            for (int j = 0; j < sharedMatrix.get(i).length(); j++) {
                assertEquals(a[j][i],sharedMatrix.get(i).get(j));
            }
        }
    }

    @Test
    void readRowMajor_Vectors_Diff_Orientation(){
        double[][] a = {{1,2,3},{4,5,6}};
        SharedMatrix sharedMatrix = new SharedMatrix(a);
        sharedMatrix.get(1).transpose();

        assertThrows(IllegalArgumentException.class,() -> sharedMatrix.readRowMajor());
    }

    @Test
    void readRowMajor_Matrix_Empty(){
        SharedMatrix sharedMatrix = new SharedMatrix();
        assertEquals(0,sharedMatrix.length());
    }

    @Test
    void readRowMajor_Matrix_Row_Major(){
        double[][] a = {{1,2,3},{4,5,6}};
        SharedMatrix sharedMatrix = new SharedMatrix(a);
        double[][] result = sharedMatrix.readRowMajor();
        for (int i = 0; i < sharedMatrix.length(); i++) {
            for (int j = 0; j < sharedMatrix.get(i).length(); j++) {
                assertEquals(a[i][j],result[i][j]);
            }
        }
    }

    @Test
    void readRowMajor_Matrix_Column_Major(){
        double[][] a = {{1,2,3},{4,5,6}};
        SharedMatrix sharedMatrix = new SharedMatrix();
        sharedMatrix.loadColumnMajor(a);
        double[][] result = sharedMatrix.readRowMajor();
        for(int i = 0;i<result.length;i++){
            for(int j = 0;j<result[i].length;j++){
                assertEquals(a[i][j],result[i][j]);
            }
        }
    }

    @Test
    void get_Index_OutOfBounds(){
        double[][] a = {{1,2,3},{4,5,6}};
        SharedMatrix sharedMatrix = new SharedMatrix(a);
        assertThrows(IllegalArgumentException.class, () -> sharedMatrix.get(-1));
    }

    @Test
    void get_Index_Valid(){
        double[][] a = {{1,2,3},{4,5,6}};
        SharedMatrix sharedMatrix = new SharedMatrix(a);
        SharedVector expected = new SharedVector(new double[]{4,5,6},VectorOrientation.ROW_MAJOR);
        SharedVector actual = sharedMatrix.get(1);
        assertEquals(expected.getOrientation(),actual.getOrientation());
        for(int i = 0;i<actual.length();i++){
            assertEquals(expected.get(i),actual.get(i));
        }
    }

    @Test
    void length_Empty(){
        SharedMatrix sharedMatrix = new SharedMatrix();
        assertEquals(0,sharedMatrix.length());
    }

    @Test
    void length_Not_Empty(){
        SharedMatrix sharedMatrix = new SharedMatrix(new double[][]{{1,2,3},{4,5,6}});
        assertEquals(2,sharedMatrix.length());
    }

    @Test
    void get_Orientation_Empty(){
        SharedMatrix sharedMatrix = new SharedMatrix();
        assertThrows(IllegalArgumentException.class,() -> sharedMatrix.getOrientation());
    }

    @Test
    void get_Orientation_Valid_Rows(){
        double[][] a = {{1,2,3},{4,5,6}};
        SharedMatrix sharedMatrix = new SharedMatrix(a);
        assertEquals(VectorOrientation.ROW_MAJOR,sharedMatrix.getOrientation());
    }

    @Test
    void get_Orientation_Valid_Columns(){
        double[][] a = {{1,2,3},{4,5,6}};
        SharedMatrix sharedMatrix = new SharedMatrix();
        sharedMatrix.loadColumnMajor(a);
        assertEquals(VectorOrientation.COLUMN_MAJOR,sharedMatrix.getOrientation());
    }
}
