package com.comp2042.logic.bricks;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BrickTest {

    @Test
    void testIBrickShapes() {
        Brick brick = new IBrick();
        List<int[][]> shapes = brick.getShapeMatrix();
        assertEquals(2, shapes.size(), "IBrick should have 2 rotation states");

        // Check horizontal state (4x4 matrix usually)
        int[][] flat = shapes.get(0);
        // I-piece usually defined as:
        // 0 0 0 0
        // 1 1 1 1
        // 0 0 0 0
        // 0 0 0 0
        int blockCount = countBlocks(flat);
        assertEquals(4, blockCount, "IBrick should have 4 blocks");
    }

    @Test
    void testOBrickShapes() {
        Brick brick = new OBrick();
        List<int[][]> shapes = brick.getShapeMatrix();
        assertEquals(1, shapes.size(), "OBrick should have 1 rotation state (identical)");

        int[][] shape = shapes.get(0);
        // O-Piece:
        // 0 0 0 0
        // 0 1 1 0
        // 0 1 1 0
        // 0 0 0 0
        assertEquals(4, countBlocks(shape), "OBrick should have 4 blocks");
    }

    @Test
    void testTBrickShapes() {
        Brick brick = new TBrick();
        List<int[][]> shapes = brick.getShapeMatrix();
        assertEquals(4, shapes.size(), "TBrick should have 4 rotation states");
        assertEquals(4, countBlocks(shapes.get(0)), "TBrick should have 4 blocks");
    }

    @Test
    void testJBrickShapes() {
        Brick brick = new JBrick();
        List<int[][]> shapes = brick.getShapeMatrix();
        assertEquals(4, shapes.size(), "JBrick should have 4 rotation states");
        assertEquals(4, countBlocks(shapes.get(0)), "JBrick should have 4 blocks");
    }

    @Test
    void testLBrickShapes() {
        Brick brick = new LBrick();
        List<int[][]> shapes = brick.getShapeMatrix();
        assertEquals(4, shapes.size(), "LBrick should have 4 rotation states");
        assertEquals(4, countBlocks(shapes.get(0)), "LBrick should have 4 blocks");
    }

    @Test
    void testSBrickShapes() {
        Brick brick = new SBrick();
        List<int[][]> shapes = brick.getShapeMatrix();
        assertEquals(2, shapes.size(), "SBrick should have 2 rotation states");
        assertEquals(4, countBlocks(shapes.get(0)), "SBrick should have 4 blocks");
    }

    @Test
    void testZBrickShapes() {
        Brick brick = new ZBrick();
        List<int[][]> shapes = brick.getShapeMatrix();
        assertEquals(2, shapes.size(), "ZBrick should have 2 rotation states");
        assertEquals(4, countBlocks(shapes.get(0)), "ZBrick should have 4 blocks");
    }

    @Test
    void testCustomBrick() {
        // Create a custom 3x3 design
        boolean[][] design = new boolean[3][3];
        design[1][1] = true; // Center
        design[0][1] = true; // Top
        design[2][1] = true; // Bottom
        // Vertical line of 3

        Brick custom = new CustomBrick(design, 10);
        List<int[][]> shapes = custom.getShapeMatrix();

        assertEquals(4, shapes.size(), "CustomBrick should generate 4 rotation states");

        // Verify the shape was translated correctly to int[][]
        int[][] shape0 = shapes.get(0);
        assertEquals(10, shape0[1][1]);
        assertEquals(10, shape0[0][1]);
        assertEquals(10, shape0[2][1]);
        assertEquals(3, countBlocks(shape0), "CustomBrick should have 3 blocks as defined");
    }

    private int countBlocks(int[][] shape) {
        int count = 0;
        for (int[] row : shape) {
            for (int val : row) {
                if (val != 0)
                    count++;
            }
        }
        return count;
    }
}
