package com.huflit.applegamehihi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientPanel extends JPanel implements KeyListener {
    int tile = 64;
    int mapSize = 10;
    int playerX = 5, playerY = 5, score = 0;
    java.util.List<Point> apples = new ArrayList<>();
    PrintWriter out;
    boolean gameOver = false;

    Image playerImage;
    Image appleImage;

    public ClientPanel() {
        setPreferredSize(new Dimension(tile * mapSize, tile * mapSize + 40)); // tăng khung để chứa score
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        loadImages();
        connect();
    }

    void loadImages() {
        try {
            playerImage = new ImageIcon(getClass().getResource("/rong.jpg")).getImage();
            appleImage = new ImageIcon(getClass().getResource("/quabo.jpg")).getImage();
        } catch (Exception e) {
            System.err.println("❌ Không thể load ảnh!");
            e.printStackTrace();
        }
    }

    void connect() {
        try {
            Socket socket = new Socket("localhost", 12345);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("✅ Kết nối server thành công!");

            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.startsWith("SCORE")) {
                            score = Integer.parseInt(line.split(" ")[1]);
                        } else if (line.startsWith("APPLE")) {
                            var p = line.split(" ");
                            synchronized (apples) {
                                apples.add(new Point(Integer.parseInt(p[1]), Integer.parseInt(p[2])));
                            }
                        } else if (line.equals("WIN")) {
                            gameOver = true;
                            SwingUtilities.invokeLater(() -> {
                                int choice = JOptionPane.showConfirmDialog(this, "🎉 Bạn đã thắng!\nBạn có muốn chơi lại không?", "Chiến thắng", JOptionPane.YES_NO_OPTION);
                                if (choice == JOptionPane.YES_OPTION) {
                                    resetGame();
                                } else {
                                    System.exit(0);
                                }
                            });
                        }
                        repaint();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối server.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Vẽ người chơi
        if (playerImage != null) {
            g.drawImage(playerImage, playerX * tile, playerY * tile, tile, tile, this);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(playerX * tile, playerY * tile, tile, tile);
        }

        // Vẽ táo
        synchronized (apples) {
            for (Point p : apples) {
                if (appleImage != null) {
                    g.drawImage(appleImage, p.x * tile, p.y * tile, tile, tile, this);
                } else {
                    g.setColor(Color.RED);
                    g.fillOval(p.x * tile, p.y * tile, tile, tile);
                }
            }
        }

        // Vẽ điểm
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, tile * mapSize + 25); // hiển thị bên dưới bản đồ
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> playerX = Math.max(0, playerX - 1);
            case KeyEvent.VK_RIGHT -> playerX = Math.min(mapSize - 1, playerX + 1);
            case KeyEvent.VK_UP -> playerY = Math.max(0, playerY - 1);
            case KeyEvent.VK_DOWN -> playerY = Math.min(mapSize - 1, playerY + 1);
        }

        if (out != null) {
            out.println("POS " + playerX + " " + playerY);
        }

        synchronized (apples) {
            apples.removeIf(p -> p.x == playerX && p.y == playerY);
        }

        repaint();
    }

    void resetGame() {
        playerX = 5;
        playerY = 5;
        score = 0;
        apples.clear();
        gameOver = false;

        if (out != null) {
            out.println("RESTART");
        }

        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    static class Point {
        int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }

        public boolean equals(Object o) {
            if (!(o instanceof Point)) return false;
            Point p = (Point) o;
            return p.x == x && p.y == y;
        }

        public int hashCode() { return Objects.hash(x, y); }
    }
}
