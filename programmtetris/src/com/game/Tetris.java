package com.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Tetris extends JFrame {

    private JLabel statusBar;

    public Tetris() {
        statusBar = new JLabel("Playing..");
        add(statusBar, BorderLayout.SOUTH);
        Board board = new Board(this);
        add(board);

        addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent me) { }
            public void mouseReleased(MouseEvent me) { }
            public void mouseEntered(MouseEvent me) { }
            public void mouseExited(MouseEvent me) { }
            public void mouseClicked(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON1 && (me.getX() >= 205 && me.getY() >= 106) && (me.getX() <= 297 && me.getY() <= 138)) {
                    System.out.println("new Game");
                    board.start();
                    board.score = 0;
                    board.level = 0;
                }
                if (me.getButton() == MouseEvent.BUTTON1 && (me.getX() >= 205 && me.getY() >= 160) && (me.getX() <= 297 && me.getY() <= 183)) {

                    board.pause();
                }
            }
        });
        board.start();

        setSize(325,500);
        setTitle("Tetris");
        setResizable(false);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public JLabel getStatusBar() {
        return statusBar;
    }
}
