package com.comp2042.domain.bricks;

import com.comp2042.logic.MatrixOperations;
import com.comp2042.domain.Brick;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the J-shaped Tetromino (3 blocks in a row with one block on the
 * right end).
 * Has four rotation states.
 */
public final class JBrick implements Brick {

        /** List of rotation matrices for the J-brick. */
        private final List<int[][]> brickMatrix = new ArrayList<>();

        /**
         * Constructs a J-brick with all four rotation states.
         */
        public JBrick() {
                brickMatrix.add(new int[][] {
                                { 0, 0, 0, 0 },
                                { 6, 6, 6, 0 },
                                { 0, 0, 6, 0 },
                                { 0, 0, 0, 0 }
                });
                brickMatrix.add(new int[][] {
                                { 0, 0, 0, 0 },
                                { 0, 6, 6, 0 },
                                { 0, 6, 0, 0 },
                                { 0, 6, 0, 0 }
                });
                brickMatrix.add(new int[][] {
                                { 0, 0, 0, 0 },
                                { 0, 6, 0, 0 },
                                { 0, 6, 6, 6 },
                                { 0, 0, 0, 0 }
                });
                brickMatrix.add(new int[][] {
                                { 0, 0, 6, 0 },
                                { 0, 0, 6, 0 },
                                { 0, 6, 6, 0 },
                                { 0, 0, 0, 0 }
                });
        }

        /**
         * Returns a deep copy of the shape matrices.
         *
         * @return list of rotation matrices for this brick
         */
        @Override
        public List<int[][]> getShapeMatrix() {
                return MatrixOperations.deepCopyList(brickMatrix);
        }
}
