package com.comp2042.domain.bricks;

import com.comp2042.logic.MatrixOperations;
import com.comp2042.domain.Brick;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the T-shaped Tetromino (3 blocks in a row with one block on top).
 * Has four rotation states. The T-brick is unique for enabling T-Spin moves.
 */
public final class TBrick implements Brick {

        /** List of rotation matrices for the T-brick. */
        private final List<int[][]> brickMatrix = new ArrayList<>();

        /**
         * Constructs a T-brick with all four rotation states.
         */
        public TBrick() {
                brickMatrix.add(new int[][] {
                                { 0, 0, 0, 0 },
                                { 3, 3, 3, 0 },
                                { 0, 3, 0, 0 },
                                { 0, 0, 0, 0 }
                });
                brickMatrix.add(new int[][] {
                                { 0, 3, 0, 0 },
                                { 0, 3, 3, 0 },
                                { 0, 3, 0, 0 },
                                { 0, 0, 0, 0 }
                });
                brickMatrix.add(new int[][] {
                                { 0, 3, 0, 0 },
                                { 3, 3, 3, 0 },
                                { 0, 0, 0, 0 },
                                { 0, 0, 0, 0 }
                });
                brickMatrix.add(new int[][] {
                                { 0, 3, 0, 0 },
                                { 3, 3, 0, 0 },
                                { 0, 3, 0, 0 },
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
