package com.game;

import com.game.Shape.Tetrominoes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Board extends JPanel implements ActionListener {

    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 22;
    private static final int WIDTH = 200;
    private static final int HEIGHT = 400;
    private static final Color[] COLORS = { new Color(0,0,0), new Color(255, 50, 19), new Color(114, 203, 59),
        new Color(3, 65, 174), new Color(255, 213, 0), new Color(128, 0, 128), new Color(0, 255, 255),
        new Color(255, 127, 0)};
    private Timer timer;
    private boolean isFallingFinished = false;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int linesCompleted = 0;
    public int level = 0;
    public int score = 0;
    private int curX = 0;
    private int curY = 0;
    private JLabel statusBar;
    private Shape currentPiece;
    private Shape.Tetrominoes[] board;

    private Rectangle[] buttons = {new Rectangle(WIDTH/3+135, 77, 100, 35),
            new Rectangle(WIDTH/3+135, 125, 100, 35)};

    public Board(Tetris parent) {
        setFocusable(true);
        currentPiece = new Shape();
        timer = new Timer(400, this);
        statusBar = parent.getStatusBar();
        board = new Shape.Tetrominoes[BOARD_WIDTH * BOARD_HEIGHT];
        clearBoard();
        addKeyListener(new TetrisAdapter());
    }

    public int squareWidth() {
        return (int) WIDTH / BOARD_WIDTH;
    }

    public int squareHeight() {
        return (int) HEIGHT / BOARD_HEIGHT;
    }

    public Shape.Tetrominoes shapeAt(int x, int y) {
        return board[y * BOARD_WIDTH + x];
    }

    public void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {
            board[i] = Shape.Tetrominoes.NoShape;
        }
    }
    public void newPiece() {
        currentPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + currentPiece.minY();
        if(!tryMove(currentPiece, curX, curY - 1)) {
            currentPiece.setShape(Shape.Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            statusBar.setText("Game Over");
        }
    }

    private void pieceDropped() {
        for(int i = 0; i < 4; i++) {
            int x = curX + currentPiece.x(i);
            int y = curY - currentPiece.y(i);
            board[y * BOARD_WIDTH + x] = currentPiece.getShape();
        }
        removeCompletedLines();

        if (!isFallingFinished) {
            newPiece();
        }
    }

    private void oneLineDown() {
        if (!tryMove(currentPiece, curX, curY -1)) {
            pieceDropped();
            addToScore(6);
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    private void drawSquare(Graphics g, int x, int y, Shape.Tetrominoes shape) {
        Color color = COLORS[shape.ordinal()];
        g.setColor(color);
        g.fillRect(x+1, y+1, squareWidth() -2, squareHeight() -2);
        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() -1, x, y);
        g.drawLine(x, y, x+ squareWidth() -1, y);
        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() -1, x + squareWidth() -1, y + squareHeight() -1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() -1, x + squareWidth() -1, y +1);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT + 100);
        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for(int j = 0; j < BOARD_WIDTH; ++j) {
                Shape.Tetrominoes shape = shapeAt(j, BOARD_HEIGHT -i - 1);
                if (shape != Shape.Tetrominoes.NoShape) {
                    drawSquare(g, j* squareWidth(), boardTop + i * squareHeight(), shape);
                }
            }
        }

        if (currentPiece.getShape() != Shape.Tetrominoes.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x = curX + currentPiece.x(i);
                int y = curY - currentPiece.y(i);
                drawSquare(g, x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), currentPiece.getShape());
            }
        }

        g.setColor(Color.red);
        for(int i = 0; i < buttons.length; i++) {
            g.drawRoundRect(buttons[i].x + 1, buttons[i].y + 1, buttons[i].width + 1, buttons[i].height + 1, 25, 25);
        }

        g.setColor(Color.white);
        for(int i = 0; i < buttons.length; i++) {
            g.fillRoundRect(buttons[i].x, buttons[i].y, buttons[i].width, buttons[i].height, 25, 25);
        }

        g.setColor(Color.BLACK);
        g.drawString("New Game", WIDTH/3+150, 100);
        g.drawString("Pause", WIDTH/3+150, 150);
        g.drawString("Level: " + level, WIDTH/3+150, 225);
        g.drawString("Score: " + score, WIDTH/3+150, 265);
    }

    public void start() {
        if (isPaused) {
            return;
        }

        isStarted = true;
        isFallingFinished = false;
        clearBoard();
        newPiece();
        timer.start();
    }

    public void pause() {
        if (!isStarted) {
            return;
        }

        isPaused = !isPaused;

        if (isPaused) {
            timer.stop();
            statusBar.setText("Paused");
        } else {
            timer.start();
            statusBar.setText(String.valueOf("Playing.."));
        }
        repaint();
    }

    private boolean tryMove(Shape newPiece,  int newX, int newY) {
        for(int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);

            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
                return false;
            }

            if(shapeAt(x, y) != Shape.Tetrominoes.NoShape) {
                return false;
            }
        }

        currentPiece = newPiece;
        curX = newX;
        curY = newY;

        repaint();

        return true;
    }

    private void removeCompletedLines() {
        int numComplLines = 0;

        for (int i = BOARD_HEIGHT - 1; i >= 0; --i) {
            boolean lineIsComplete = true;

            for(int j = 0; j < BOARD_WIDTH; ++j) {
                if (shapeAt(j, i) == Shape.Tetrominoes.NoShape) {
                    lineIsComplete = false;
                    break;
                }
            }

            if (lineIsComplete) {
                numComplLines++;

                for (int k = i; k < BOARD_HEIGHT - 1; ++k) {
                    for(int j = 0; j < BOARD_WIDTH; ++j) {
                        board[k * BOARD_WIDTH + j] = shapeAt(j, k +1);
                    }
                }
                lineIsCleared();
            }

        }

        if (numComplLines > 0) {
            linesCompleted += numComplLines;
            calculateScore(numComplLines);
            isFallingFinished = true;
            currentPiece.setShape(Shape.Tetrominoes.NoShape);
            repaint();
        }
    }

    private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(currentPiece, curX, newY -1)) {
                break;
            }

            newY--;
        }
        addToScore(12);
        repaint();
        pieceDropped();
    }

    private void calculateScore(int numFullLines) {
        switch(numFullLines) {
            case 1:
                score += 40*(level+1);
                break;
            case 2:
                score += 100*(level+1);
                break;
            case 3:
                score += 300*(level+1);
                break;
            case 4:
                score += 1200*(level+1);
                break;
            default:
                break;
        }
    }

    private void lineIsCleared() {
        linesCompleted++;
        if(linesCompleted % 10 == 0) {
            if (level < 9) {
                level++;
            }
        }
    }

    private void addToScore(int add) {
        if (add != 0) {
            score+=add;
        } else {
            score+=1;
        }
    }

    class TetrisAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if(!isStarted || currentPiece.getShape() == Shape.Tetrominoes.NoShape) {
                return;
            }

            int keyCode = e.getKeyCode();
            if ( keyCode == 'p' || keyCode == 'P') {
                pause();
            }

            if (isPaused) {
                return;
            }

            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                case 'a':
                case 'A':
                    tryMove(currentPiece, curX - 1, curY);
                    break;
                case KeyEvent.VK_RIGHT:
                case 'd':
                case 'D':
                    tryMove(currentPiece, curX + 1, curY);
                    break;
                case KeyEvent.VK_DOWN:
                case 's':
                case 'S':
                    oneLineDown();
                    break;
                case KeyEvent.VK_UP:
                case 'w':
                case 'W':
                    tryMove(currentPiece.rotateLeft(), curX, curY);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
            }
        }
    }

}
