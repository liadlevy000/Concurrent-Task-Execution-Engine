package spl.lae;

import memory.SharedMatrix;
import memory.SharedVector;
import memory.VectorOrientation;
import org.junit.jupiter.api.Test;
import parser.ComputationNode;
import parser.ComputationNodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class LinearAlgebraEngineTest {
    @Test
    void test_CreateAddTasks() {
        double[][] left = new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        double[][] right = new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        ComputationNode leftN = new ComputationNode(left);
        ComputationNode rightN = new ComputationNode(right);
        List<ComputationNode> children = new ArrayList<>();
        children.add(leftN);
        children.add(rightN);

        ComputationNode computationNode = new ComputationNode("+", children);
        LinearAlgebraEngine engine = new LinearAlgebraEngine(3);
        engine.run(computationNode);
        double[][] expected = {{2, 4, 6}, {8, 10, 12}, {14, 16, 18}};
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[i].length; j++) {
                assertEquals(expected[i][j], computationNode.getMatrix()[i][j]);
            }
        }
    }

    @Test
    void test_CreateMulTasks() {
        double[][] left = new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        double[][] right = new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 10}};
        ComputationNode leftN = new ComputationNode(left);
        ComputationNode rightN = new ComputationNode(right);
        List<ComputationNode> children = new ArrayList<>();
        children.add(leftN);
        children.add(rightN);

        ComputationNode computationNode = new ComputationNode("*", children);
        LinearAlgebraEngine engine = new LinearAlgebraEngine(3);
        engine.run(computationNode);
        double[][] expected = {{30, 36, 45}, {66, 81, 102}, {102, 126, 159}};
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[i].length; j++) {
                assertEquals(expected[i][j], computationNode.getMatrix()[i][j]);
            }
        }
    }

    @Test
    void test_CreateNegate() {
        double[][] left = new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        ComputationNode leftN = new ComputationNode(left);
        List<ComputationNode> children = new ArrayList<>();
        children.add(leftN);

        ComputationNode computationNode = new ComputationNode("-", children);
        LinearAlgebraEngine engine = new LinearAlgebraEngine(3);
        engine.run(computationNode);
        double[][] expected = {{-1, -2, -3}, {-4, -5, -6}, {-7, -8, -9}};
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[i].length; j++) {
                assertEquals(expected[i][j], computationNode.getMatrix()[i][j]);
            }
        }
    }

}
