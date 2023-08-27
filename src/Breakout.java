/*
 * Author: Oleksiy Zhytnetsky
 * Project: Lab2-Zhytnetskyi
 * File: Breakout.java
 * -------------------
 * This file implements the game of Breakout and contains all the necessary methods to run it.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Implements the game of Breakout and contains all the necessary methods to run it.
 */
public class Breakout extends GraphicsProgram {
    /** The RandomGenerator instance. */
    private static final RandomGenerator RAND_GEN = RandomGenerator.getInstance();

    /* Dimensions of the application window in px */

    /** The width of the application window in px. */
    private static final int APPLICATION_WIDTH = 800;

    /** The height of the application window in px. */
    private static final int APPLICATION_HEIGHT = 600;

    /* Dimensions of the game board in px */

    /** The width of the game board in px. */
    private static final int WIDTH = APPLICATION_WIDTH - 30;

    /** The height of the game board in px. */
    private static final int HEIGHT = APPLICATION_HEIGHT - 84;

    /* Dimensions of the paddle in px */

    /** The width of the paddle in px. */
    private static final double PADDLE_WIDTH = 75.0;

    /** The height of the paddle in px. */
    private static final double PADDLE_HEIGHT = 10.0;

    /** The offset of the bottom of the paddle from the bottom of the game board in px. */
    private static final double PADDLE_Y_OFFSET = 30.0;

    /** The speed of movement of the paddle along the X-Axis. */
    private static final double PADDLE_DX = 3.5;

    /** The blind zone of paddle dx change for use in asynchronous methods. */
    private static final double PADDLE_DX_BLIND_ZONE = PADDLE_WIDTH / 2;

    /** The value in px up to which the paddle can go off-screen on the X-Axis. */
    private static final double PADDLE_OFFSCREEN_X_TOLERANCE = PADDLE_WIDTH / 4;

    /** The number of bricks per row. */
    private static final int NBRICKS_PER_ROW = 3;

    /** The number of rows. */
    private static final int NBRICK_ROWS = 2;

    /** The separation interval between columns and rows of bricks in px. */
    private static final double BRICK_SEP = 6.0;

    /** The width of a brick in px. */
    private static final double BRICK_WIDTH =
            (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

    /** The height of a brick in px. */
    private static final double BRICK_HEIGHT = 8.0;

    /** The offset of the top brick row from the top of the game board. */
    private static final double BRICK_Y_OFFSET = 60.0;

    /**
     * The percentage by which a brick's width is shrunk at the beginning of each round,
     * except for the first one. This effect is halved for the brick's height.
     */
    private static final double BRICK_SIZE_SHRINKING_PER_ROUND = 0.25;

    /**
     * The percentage by which the movement speed of the ball is increased at the beginning
     * of each round, except for the first one. This effect is halved for the ball's dx speed
     */
    private static final double BALL_SPEED_INCREASE_PER_ROUND = 0.2;

    /** The radius of the ball in px. */
    private static final double BALL_RADIUS = 7.5;

    /** The speed of the ball object along the X-Axis. */
    private static final double BALL_DX = RAND_GEN.nextDouble(1.0, 2.5);

    /** The speed of the ball object along the Y-Axis. */
    private static final double BALL_DY = 2.5;

    /** The number of lives a player has. */
    private static final int NLIVES = 3;

    /** The number of rounds the game has. */
    private static final int NROUNDS = 3;

    /** The period of time for which the game loop gets paused after every iteration. */
    private static final double PAUSE_INTERVAL = 1000 / 75.0;

    /** The number of targets the player has to destroy to win. */
    private static final int TARGETS_TOTAL = NBRICKS_PER_ROW * NBRICK_ROWS;

    /** The extra space added to the app window height to accommodate game info labels. */
    private static final int LABEL_RESERVED_SPACE = (int)(APPLICATION_HEIGHT * 0.1);

    /** The font size of game info (score, nLives) labels in px. */
    private static final int INFO_LABEL_FONT_SIZE = LABEL_RESERVED_SPACE / 2;

    /** The font size of the round over (victory, defeat) labels in px. */
    private static final int ROUND_OVER_LABEL_FONT_SIZE = 40;

    /** The offset of the game info labels from the window borders in px. */
    private static final double LABEL_X_OFFSET = 20.0;

    /** The colour used for score and lives display. */
    private static final Color GAMEINFO_LABEL_COLOUR = Color.WHITE;

    /** The colour used for victory, defeat and round over text display */
    private static final Color TEXT_LABEL_COLOUR = new Color(173, 33, 238);

    /** The double offset of the background image along the Y-Axis */
    private static final double BACKGROUND_IMG_Y_OFFSET = 85;

    /* Method: run() */
    /** Runs the Breakout program */
    public void run() {
        /* Setting the size of the application window */
        this.setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT + LABEL_RESERVED_SPACE);

        /* Adding mouse event listeners */
        addMouseListeners();

        /* Instantiating the ball */
        Ball ball = new Ball(WIDTH / 2.0, HEIGHT / 2.0, BALL_RADIUS,
                BALL_DX, BALL_DY, Color.BLACK);
        ballRandomiseDirectionX(ball);
        add(ball);

        /* Instantiating the paddle */
        Platform paddle = new Platform(WIDTH / 2.0, HEIGHT - PADDLE_Y_OFFSET -
                PADDLE_HEIGHT / 2, PADDLE_WIDTH, PADDLE_HEIGHT, 0, 0, Color.BLACK);
        add(paddle);
        this.paddleInstance = paddle;

        /* Instantiating the labels */
        GLabel nLives = new GLabel("");
        GLabel score = new GLabel("");

        /* Initialising the game */
        initGame(nLives, score);

        /* Initialising the sound */
        SoundClip ballCollisionSound = new SoundClip("./assets/soundCollision.wav");
        ballCollisionSound.setVolume(0.85);

        SoundClip ballDeathSound = new SoundClip("./assets/soundBallDeath.wav");
        ballDeathSound.setVolume(0.85);

        SoundClip gameVictorySound = new SoundClip("./assets/soundGameVictory.wav");
        gameVictorySound.setVolume(0.85);

        SoundClip roundVictorySound = new SoundClip("./assets/soundRoundVictory.wav");
        roundVictorySound.setVolume(0.85);

        SoundClip gameDefeatSound = new SoundClip("./assets/soundGameDefeat.wav");
        gameDefeatSound.setVolume(0.85);

        /* Invoking the main game loop */
        gameLoop(paddle, ball, nLives, score, ballCollisionSound,
                ballDeathSound, gameVictorySound, roundVictorySound, gameDefeatSound);
    }

    /* Asynchronous Method: mouseMoved() */
    /**
     * Determines whether the paddle should be moved and, if so, in which direction
     *
     * @param ev Mouse Event
     */
    public void mouseMoved(MouseEvent ev) {
        if (this.currLives > 0) {
            Point currPos = ev.getPoint();

            determinePaddleSpeed(this.paddleInstance, currPos);
        }
    }

    /* Method: gameLoop() */
    /**
     * Runs the main game loop, which itself runs the current level and handles game-over
     * (won or lost) reload scenarios.
     *
     * @param paddle             A reference to the paddle object.
     * @param ball               A reference to the ball object.
     * @param nLives             A reference to the nLives object.
     * @param score              A reference to the score object.
     * @param ballCollisionSound A reference to the ballCollisionSound SoundClip object.
     * @param ballDeathSound     A reference to the ballDeathSound SoundClip object.
     * @param gameVictorySound   A reference to the gameVictorySound SoundClip object.
     * @param roundVictorySound  A reference to the roundVictorySound SoundClip object.
     * @param gameDefeatSound    A reference to the gameDefeatSound SoundClip object.
     */
    private void gameLoop(Platform paddle, Ball ball, GLabel nLives, GLabel score,
                          SoundClip ballCollisionSound, SoundClip ballDeathSound,
                          SoundClip gameVictorySound, SoundClip roundVictorySound,
                          SoundClip gameDefeatSound) {
        for (int i = 1; i <= NROUNDS; i++) {
            /* Running the current game level */
            playLevel(paddle, ball, nLives, score, i, ballCollisionSound,
                    ballDeathSound, gameVictorySound, roundVictorySound);

            /* Reloading the game if the player has won */
            if (this.gameScore >= TARGETS_TOTAL) {
                i = 0;
                this.gameScore = 0;
            }

            /* Reloading the game if the player has lost */
            if (this.currLives <= 0) {
                displayRoundOver(true, ROUND_OVER_LABEL_FONT_SIZE, i, TEXT_LABEL_COLOUR);
                gameDefeatSound.play();
                pause(gameDefeatSound.getDuration() * 1000);
                waitForClick();
                i = 0;
                reloadGame(paddle, ball, nLives, i);
            }
        }
    }

    /* Method: playLevel() */
    /**
     * Runs all the methods necessary to simulate a specified game level.
     *
     * @param paddle             A reference to the paddle object.
     * @param ball               A reference to the ball object.
     * @param nLives             A reference to the nLives object.
     * @param score              A reference to the score object.
     * @param level              The specified game level.
     * @param ballCollisionSound A reference to the ballCollisionSound SoundClip object.
     * @param ballDeathSound     A reference to the ballDeathSound SoundClip object.
     * @param gameVictorySound   A reference to the gameVictorySound SoundClip object.
     * @param roundVictorySound  A reference to the roundVictorySound SoundClip object.
     */
    private void playLevel(Platform paddle, Ball ball, GLabel nLives, GLabel score, int level,
                           SoundClip ballCollisionSound, SoundClip ballDeathSound,
                           SoundClip gameVictorySound, SoundClip roundVictorySound) {
        while (this.currLives > 0) {
            /* Paddle logic */
            movePaddle(paddle);

            /* Ball logic */
            moveBall(ball);
            ballHandleCollision(ball, paddle, ballCollisionSound);
            ballHandleDeath(ball, ballDeathSound);

            /* Labels logic */
            updateScore(score);
            updateLives(nLives);

            /* Victory logic */
            if (this.gameScore >= TARGETS_TOTAL) {
                displayRoundOver(false, ROUND_OVER_LABEL_FONT_SIZE, level,
                        TEXT_LABEL_COLOUR);

                if (level == NROUNDS) {
                    gameVictorySound.play();
                    pause(gameVictorySound.getDuration() * 1000);
                }
                else roundVictorySound.play();

                waitForClick();
                reloadGame(paddle, ball, nLives, level);
                break;
            }
            pause(PAUSE_INTERVAL);
        }
    }

    /* Method: reloadGame() */
    /**
     * Runs all the methods necessary to reload the game when transitioning between levels.
     *
     * @param paddle A reference to the paddle object.
     * @param ball   A reference to the ball object.
     * @param nLives A reference to the nLives object.
     * @param level  The specified game level.
     */
    public void reloadGame(Platform paddle, Ball ball, GLabel nLives, int level) {
        /* Removing previous victory labels */
        for (int i = 0; i < 2; i++) {
            GObject targetObj = getElementAt(WIDTH / 2.0, HEIGHT / 2.0 +
                    ROUND_OVER_LABEL_FONT_SIZE * 1.5 * i);

            if (targetObj != null && targetObj.getClass() == GLabel.class) {
                remove(targetObj);
            }
        }

        /* Increasing game difficulty || Regressing to initial difficulty */
        if (level != 0 && level != NROUNDS) {
            this.currBrickWidth *= (1 - BRICK_SIZE_SHRINKING_PER_ROUND);
            this.currBrickHeight *= (1 - (BRICK_SIZE_SHRINKING_PER_ROUND / 2.0));
            ball.setDx( ball.getDx() * (1 + (BALL_SPEED_INCREASE_PER_ROUND / 2.0)) );
            if (ball.getDy() < 0) ball.setDy(-ball.getDy());
            ball.setDy( ball.getDy() * (1 + BALL_SPEED_INCREASE_PER_ROUND) );
        }
        else {
            this.currBrickWidth = BRICK_WIDTH;
            this.currBrickHeight = BRICK_HEIGHT;
            ball.setDx(BALL_DX);
            ball.setDy(BALL_DY);
        }

        /* Redrawing the world and repositioning the main objects */
        drawBricks();
        drawLives(nLives);
        reloadBall(ball);
        reloadPaddle(paddle);

        /* Regressing instance variables to default values */
        this.currLives = NLIVES;
        if (level != NROUNDS) this.gameScore = 0;
    }

    /* Method: reloadPaddle() */
    /**
     * Moves the paddle to its initial location
     *
     * @param paddle A reference to the paddle object
     */
    private void reloadPaddle(Platform paddle) {
        double initX = WIDTH / 2.0;

        paddle.move(-1 * (paddle.getCentreX() - initX), 0);
        paddle.setCentreX(initX);
    }

    /* Method: ballHandleDeath() */
    /**
     * If the ball falls below a set Y-coordinate threshold, decrements the current number of
     * lives by 1 and invokes reloadBall()
     *
     * @param ball           A reference to the ball object
     * @param ballDeathSound A reference to the ballDeathSound SoundClip object.
     */
    private void ballHandleDeath(Ball ball, SoundClip ballDeathSound) {
        if (ball.getCentreY() + ball.getRadius() > HEIGHT + LABEL_RESERVED_SPACE) {
            ballDeathSound.play();
            this.currLives--;
            reloadBall(ball);
        }
    }

    /* Method: reloadBall() */
    /**
     * Moves the ball to its initial location
     *
     * @param ball A reference to the ball object
     */
    private void reloadBall(Ball ball) {
        double initX = WIDTH / 2.0, initY = HEIGHT / 2.0;

        ball.move( -1 * (ball.getCentreX() - initX), -1 * (ball.getCentreY() - initY) );
        ball.setCentreX(initX);
        ball.setCentreY(initY);
        ballRandomiseDirectionX(ball);
    }

    /* Method: ballRandomiseDirectionX() */
    /**
     * Randomises the dx value of the ball to be either BALL_DX or -BALL_DX
     *
     * @param ball A reference to the ball object
     */
    private void ballRandomiseDirectionX(Ball ball) {
        boolean val = RAND_GEN.nextBoolean();
        if (val) ball.setDx(BALL_DX);
        else ball.setDx(-BALL_DX);
    }

    /* Method: ballHandleCollision() */
    /**
     * Handles collision and interaction of the ball with other game objects
     *
     * @param ball               A reference to the ball object
     * @param paddle             A reference to the paddle object
     * @param ballCollisionSound A reference to the ballCollisionSound SoundClip object.
     */
    private void ballHandleCollision(Ball ball, Platform paddle, SoundClip ballCollisionSound) {
        ballHandleBottomCollision(ball, paddle, ballCollisionSound);
        ballHandleTopCollision(ball, paddle, ballCollisionSound);

        ballHandleRightCollision(ball, ballCollisionSound);
        ballHandleLeftCollision(ball, ballCollisionSound);
    }

    /* Method: ballHandleLeftCollision() */
    /**
     * Checks for collision with the right window border
     *
     * @param ball     A reference to the ball object
     * @param colSound A reference to the ballCollisionSound SoundClip object.
     */
    private void ballHandleLeftCollision(Ball ball, SoundClip colSound) {
        GPoint collisionPoint = new GPoint(ball.getCentreX() - ball.getRadius(),
                ball.getCentreY());

        if (collisionPoint.getX() <= 0) {
            ball.setDx(-ball.getDx());
            colSound.play();
        }
    }

    /* Method: ballHandleRightCollision() */
    /**
     * Checks for collision with the right window border
     *
     * @param ball     A reference to the ball object
     * @param colSound A reference to the ballCollisionSound SoundClip object.
     */
    private void ballHandleRightCollision(Ball ball, SoundClip colSound) {
        GPoint collisionPoint = new GPoint(ball.getCentreX() + ball.getRadius(),
                ball.getCentreY());

        if (collisionPoint.getX() >= WIDTH) {
            ball.setDx(-ball.getDx());
            colSound.play();
        }
    }

    /* Method: ballHandleTopCollision() */
    /**
     * Checks for collision with bricks above the ball
     *
     * @param ball     A reference to the ball object
     * @param paddle   A reference to the paddle object
     * @param colSound A reference to the ballCollisionSound SoundClip object.
     */
    private void ballHandleTopCollision(Ball ball, Platform paddle, SoundClip colSound) {
        GPoint collisionPoint = new GPoint(ball.getCentreX(),
                ball.getCentreY() - ball.getRadius());

        GObject obj = getElementAt(collisionPoint);
        if (obj != null && obj != paddle && obj.getClass() != GLabel.class &&
                obj.getClass() != GOval.class && obj.getClass() != GImage.class) {
            ball.setDy(-ball.getDy());
            remove(obj);
            this.gameScore++;
            colSound.play();
        }

        if (collisionPoint.getY() <= 0) {
            ball.setDy(-ball.getDy());
            colSound.play();
        }
    }

    /* Method: ballHandleBottomCollision() */
    /**
     * Checks for collision with the paddle / brick below the ball
     *
     * @param ball     A reference to the ball object
     * @param paddle   A reference to the paddle object
     * @param colSound A reference to the ballCollisionSound SoundClip object.
     */
    private void ballHandleBottomCollision(Ball ball, Platform paddle, SoundClip colSound) {
        GPoint collisionPoint = new GPoint(ball.getCentreX(),
                ball.getCentreY() + ball.getRadius());

        GObject obj = getElementAt(collisionPoint);
        if (obj != null && obj != ball && obj.getClass() != GLabel.class &&
                obj.getClass() != GOval.class && obj.getClass() != GImage.class) {
            ball.move(0, -ball.getRadius());
            ball.setCentreY(ball.getCentreY() - ball.getRadius());
            ball.setDy(-ball.getDy());
            if (obj != paddle) {
                remove(obj);
                this.gameScore++;
            }
            colSound.play();
        }
    }

    /* Method: moveBall() */
    /**
     * Moves the ball object on set ball.dx and ball.dy values
     *
     * @param ball A reference to the ball object
     */
    private void moveBall(Ball ball) {
        ball.move(ball.getDx(), ball.getDy());
        ball.updateLocation();
    }

    /* Method: movePaddle() */
    /**
     * Moves the paddle object on set paddle.dx and paddle.dy values
     *
     * @param paddle A reference to the paddle object
     */
    private void movePaddle(Platform paddle) {
        paddlePreventBorderOverflow(paddle);

        paddle.move(paddle.getDx(), paddle.getDy());
        paddle.updateLocation();
    }

    /* Method: paddlePreventBorderOverflow() */
    /**
     * Prevents the paddle from going too far off-screen on the X-Axis
     *
     * @param paddle A reference to the paddle object
     */
    public void paddlePreventBorderOverflow(Platform paddle) {
        // Right window border overflow
        if (paddle.getCentreX() + paddle.getWidth() / 2 > WIDTH +
                PADDLE_OFFSCREEN_X_TOLERANCE) {
            paddle.setDx(-0.25);
            paddle.move(paddle.getDx(), paddle.getDy());
            paddle.updateLocation();
        }

        // Left window border overflow
        if (paddle.getCentreX() - paddle.getWidth() / 2 < -PADDLE_OFFSCREEN_X_TOLERANCE) {
            paddle.setDx(0.25);
            paddle.move(paddle.getDx(), paddle.getDy());
            paddle.updateLocation();
        }
    }

    /* Method: determinePaddleSpeed() */
    /**
     * Determines and sets the dx of the paddle object
     *
     * @param paddle  A reference to the paddle object
     * @param pos     The coordinate point received from mouseMoved()
     */
    private void determinePaddleSpeed(Platform paddle, Point pos) {
        if (Math.abs(paddle.getCentreY() - pos.getY()) <= HEIGHT * 0.35) {
            if (pos.getX() - PADDLE_DX_BLIND_ZONE > paddle.getCentreX()) {
                paddle.setDx(PADDLE_DX);
            }
            else if (pos.getX() + PADDLE_DX_BLIND_ZONE < paddle.getCentreX()) {
                paddle.setDx(-PADDLE_DX);
            }
            else paddle.setDx(0.0);
        }
        else paddle.setDx(0.0);
    }

    /* Method: initGame() */
    /**
     * Runs all the methods necessary to initialise the game at the beginning of its execution.
     *
     * @param nLives A reference to the nLives object.
     * @param score  A reference to the score object.
     */
    private void initGame(GLabel nLives, GLabel score) {
        setBackgroundImage();
        drawBricks();
        prepareLabels(nLives, score);
        drawLives(nLives);
    }

    /* Method: setBackgroundImage() */
    /** Sets the background image of the game */
    public void setBackgroundImage() {
        GImage img = new GImage("./assets/background.jpg", 0, 0);
        img.setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT + BACKGROUND_IMG_Y_OFFSET);
        img.setLocation(0, -BACKGROUND_IMG_Y_OFFSET / 2.0);
        img.move(0, BACKGROUND_IMG_Y_OFFSET / 2.0);

        add(img);
        img.sendToBack();
    }

    /* Method: updateLives() */
    /**
     * Updates the visual representation of the number of lives (balls) the player has left
     *
     * @param nLives A reference to the nLives object
     */
    private void updateLives(GLabel nLives) {
        for (int i = this.currLives; i < NLIVES; i++) {
            GObject obj = getElementAt((1 + i) * LABEL_X_OFFSET / 1.25 +
                            nLives.getWidth() + BALL_RADIUS * i,
                    HEIGHT + (LABEL_RESERVED_SPACE + BALL_RADIUS) / 2.0);

            if (obj != null && obj.getClass() != GLabel.class &&
                    obj.getClass() != GImage.class) {
                remove(obj);
            }
        }
    }

    /* Method: drawLives() */
    /**
     * Draws the balls next to the nLives label, which serve as a visual representation of the
     * number of lives the player has left.
     *
     * @param nLives A reference to the nLives object
     */
    private void drawLives(GLabel nLives) {
        for (int i = 0; i < NLIVES; i++) {
            GOval life = Shapes.createCircle((1 + i) * LABEL_X_OFFSET / 1.25 +
                            nLives.getWidth() + BALL_RADIUS * i,
                    HEIGHT + (LABEL_RESERVED_SPACE + BALL_RADIUS) / 2.0,
                    BALL_RADIUS, GAMEINFO_LABEL_COLOUR);

            if (getElementAt(life.getX(), life.getY()) != null &&
                    getElementAt(life.getX(), life.getY()).getClass() != GLabel.class &&
                    getElementAt(life.getX(), life.getY()).getClass() != GImage.class) {
                remove( getElementAt(life.getX(), life.getY()) );
            }

            add(life);
        }
    }

    /* Method: drawBricks() */
    /** Draws the columns and rows of bricks */
    private void drawBricks() {
        double currX = BRICK_WIDTH / 2 + 5, offsetX = BRICK_WIDTH + BRICK_SEP;
        double currY = BRICK_Y_OFFSET + BRICK_HEIGHT / 2, offsetY = BRICK_HEIGHT + BRICK_SEP;
        Color currColour = new Color(254, 0, 0);
        int colourDelta = 127;

        for (int i = 0; i < NBRICK_ROWS; i++) {
            /* Determining brick row colour */
            if (i % 2 == 0 && i != 0) {
                if (currColour.getRed() == 254 && currColour.getBlue() == 0) {
                    currColour = new Color(254, currColour.getGreen() + colourDelta, 0);
                }

                if (currColour.getBlue() == 0 && currColour.getGreen() == 254) {
                    currColour = new Color(currColour.getRed() - colourDelta, 254, 0);
                }

                if (currColour.getRed() == 0 && currColour.getGreen() == 254) {
                    currColour = new Color(0, 254, currColour.getBlue() + colourDelta);
                }

                if (currColour.getRed() == 0 && currColour.getBlue() == 254) {
                    currColour = new Color(0, currColour.getGreen() - colourDelta, 254);
                }

                if (currColour.getGreen() == 0 && currColour.getBlue() == 254) {
                    currColour = new Color(currColour.getRed() + colourDelta, 0, 254);
                }

                if (currColour.getRed() == 254 && currColour.getGreen() == 0) {
                    currColour = new Color(254, 0, currColour.getBlue() - colourDelta);
                }
            }

            for (int j = 0; j < NBRICKS_PER_ROW; j++) {
                /* Making sure to remove any potentially present previous bricks */
                GObject leftover = getElementAt(currX + j * offsetX, currY + i * offsetY);
                if (leftover != null && leftover.getClass() == Platform.class) {
                    remove(leftover);
                }

                /* Creating a brick */
                Platform brick = new Platform(currX + j * offsetX,
                        currY + i * offsetY, this.currBrickWidth, this.currBrickHeight,
                        0, 0, currColour);
                add(brick);
            }
        }
    }

    /* Method: updateScore() */
    /**
     * Updates the visual representation of the current score on the screen
     *
     * @param score A reference to the score object
     */
    private void updateScore(GLabel score) {
        score.setLabel("Score: " + this.gameScore);
    }

    /* Method: displayRoundOver() */
    /**
     * Initialises and displays game over (victory / defeat) labels at the end of each round
     *
     * @param hasLost    Determines whether the round was won or lost
     * @param fontSize   The label font size in px
     * @param nRound     The number of the current round (starting from 1)
     * @param colour     The colour of the text in the label
     */
    public void displayRoundOver(boolean hasLost, int fontSize, int nRound, Color colour) {
        GLabel roundOverLabel = new GLabel("");
        GLabel playAgainLabel = new GLabel("");
        int yInterval = fontSize / 2;

        roundOverLabel.setFont("Consolas-" + fontSize);
        playAgainLabel.setFont("Consolas-" + fontSize / 2);

        if (!hasLost) {
            if (nRound != NROUNDS) {
                roundOverLabel.setLabel("Victory! " + (NROUNDS - nRound) + " levels remain.");
                playAgainLabel.setLabel("Click on the screen to continue...");
            }
            else {
                roundOverLabel.setFont("Consolas-" + (fontSize - 5) );
                roundOverLabel.setLabel("Congratulations! You win the Breakout!");
                playAgainLabel.setLabel("Click on the screen to play again...");
            }
        }
        else {
            roundOverLabel.setLabel("Defeat! Better luck next time!");
            playAgainLabel.setLabel("Click on the screen to play again...");
        }

        roundOverLabel.setLocation(WIDTH / 2.0 - roundOverLabel.getWidth() / 2,
                HEIGHT / 2.0 + roundOverLabel.getHeight() / 2);
        roundOverLabel.setColor(colour);
        add(roundOverLabel);

        playAgainLabel.setLocation(WIDTH / 2.0 - playAgainLabel.getWidth() / 2,
                HEIGHT / 2.0 + roundOverLabel.getHeight() / 2 + yInterval +
                        playAgainLabel.getHeight());
        playAgainLabel.setColor(colour);
        add(playAgainLabel);
    }

    /* Method: prepareLabels() */
    /**
     * Prepares (initialises) the game info labels, such as "nLives" and "score"
     *
     * @param nLives A reference to the nLives object
     * @param score  A reference to the score object
     */
    private void prepareLabels(GLabel nLives, GLabel score) {
        nLives.setFont("Consolas-" + INFO_LABEL_FONT_SIZE);
        nLives.setLabel("Lives: ");
        nLives.setLocation(LABEL_X_OFFSET, HEIGHT + LABEL_RESERVED_SPACE / 2.0 +
                nLives.getHeight() / 2.0);
        nLives.setColor(GAMEINFO_LABEL_COLOUR);
        add(nLives);

        score.setFont("Consolas-" + INFO_LABEL_FONT_SIZE);
        score.setLabel("Score: 99");
        score.setLocation(WIDTH - LABEL_X_OFFSET - score.getWidth(),
                HEIGHT + LABEL_RESERVED_SPACE / 2.0 + score.getHeight() / 2.0);
        score.setColor(GAMEINFO_LABEL_COLOUR);
        add(score);
    }

    /* Private Instance Variables */

    /** An Instance version of the paddle for use in asynchronous methods */
    private Platform paddleInstance;

    /** The number of bricks destroyed by the player */
    private int gameScore = 0;

    /** The current number of lives the player has left */
    private int currLives = NLIVES;

    /** The current width of a brick */
    private double currBrickWidth = BRICK_WIDTH;

    /** The current height of a brick */
    private double currBrickHeight = BRICK_HEIGHT;
}