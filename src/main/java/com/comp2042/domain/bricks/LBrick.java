package com.comp2042.domain.bricks;

import com.comp2042.logic.MatrixOperations;
import com.comp2042.domain.Brick;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the L-shaped Tetromino (3 blocks in a row with one block on the
 * left end).
 * Has four rotation states.
 */
public final class LBrick implements Brick {

        /** List of rotation matrices for the L-brick. */
        private final List<int[][]> brickMatrix = new ArrayList<>();

        /**
         * Constructs an L-brick with all four rotation states.
         */
        public LBrick() {
                brickMatrix.add(new int[][] {
                                { 0, 0, 0, 0 },
                                { 0, 7, 7, 7 },
                                { 0, 7, 0, 0 },
                                { 0, 0, 0, 0 }
                });
                brickMatrix.add(new int[][] {
                                { 0, 0, 0, 0 },
                                { 0, 7, 7, 0 },
                                { 0, 0, 7, 0 },
                                { 0, 0, 7, 0 }
                });
                brickMatrix.add(new int[][] {
                                { 0, 0, 0, 0 },
                                { 0, 0, 7, 0 },
                                { 7, 7, 7, 0 },
                                { 0, 0, 0, 0 }
                });
                brickMatrix.add(new int[][] {
                                { 0, 7, 0, 0 },
                                { 0, 7, 0, 0 },
                                { 0, 7, 7, 0 },
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
