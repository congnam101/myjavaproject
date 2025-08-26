/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huflit.applegamehihi;

/**
 *
 * @author congnammm
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
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

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("✅ Server đang chạy tại cổng 12345...");

        while (true) {
            Socket client = serverSocket.accept();
            System.out.println("🔗 Client kết nối: " + client.getInetAddress());
            new Thread(() -> handleClient(client)).start();
        }
    }

   static void handleClient(Socket socket) {
    try {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        while (true) {
            List<Point> apples = generateApples(4);
            for (Point p : apples) {
                out.println("APPLE " + p.x + " " + p.y);
            }
            int score = 0;
            boolean playing = true;

            while (playing && (socket != null && !socket.isClosed())) {
                String line = in.readLine();
                if (line == null) break;

                if (line.startsWith("POS")) {
                    String[] parts = line.split(" ");
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);

                    Iterator<Point> it = apples.iterator();
                    while (it.hasNext()) {
                        Point p = it.next();
                        if (p.x == x && p.y == y) {
                            it.remove();
                            score++;
                            break;
                        }
                    }

                    out.println("SCORE " + score);

                    if (score >= 4) {
                        out.println("WIN");
                        playing = false;
                        break;
                    }
                } else if (line.equals("RESTART")) {
                    // Bắt đầu lại vòng lặp ngoài để gửi táo mới
                    break;
                }
            }

            if (socket.isClosed()) break;
        }

        socket.close();
        System.out.println("🔌 Kết thúc kết nối với client.");

    } catch (IOException e) {
        System.err.println("❌ Lỗi xử lý client: " + e.getMessage());
    }
}


    static List<Point> generateApples(int n) {
        List<Point> list = new ArrayList<>();
        Random rand = new Random();
        while (list.size() < n) {
            Point p = new Point(rand.nextInt(10), rand.nextInt(10));
            if (!list.contains(p)) list.add(p);
        }
        return list;
    }
}