package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;
    private final Deque<Brick> nextBricks = new ArrayDeque<>();
    private final List<Brick> currentBag = new ArrayList<>();

    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());

        // Fill initial bag and queue
        refillBag();
        fillQueue();
    }

    // 7-Bag Randomizer: Creates a shuffled bag of all 7 pieces.
    private void refillBag() {
        currentBag.clear();
        currentBag.addAll(brickList);
        Collections.shuffle(currentBag);
    }

    // Ensures we always have at least 2 pieces in the queue
    private void fillQueue() {
        while (nextBricks.size() < 2) {
            if (currentBag.isEmpty()) {
                refillBag();
            }
            nextBricks.add(currentBag.remove(0));
        }
    }

    @Override
    public Brick getBrick() {
        fillQueue();
        return nextBricks.poll();
    }

    @Override
    public Brick getNextBrick() {
        fillQueue();
        return nextBricks.peek();
    }
}