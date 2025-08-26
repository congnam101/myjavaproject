/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huflit.applegamehihi;

/**
 *
 * @author congnammm
 */
import javax.swing.*;

public class GameClient {
    public static void main(String[] args) {
        System.out.println("⚡ Đang tạo cửa sổ game...");
        JFrame frame = new JFrame("Game Ăn Táo");
        ClientPanel panel = new ClientPanel();
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // giữa màn hình
        SwingUtilities.invokeLater(() -> panel.requestFocusInWindow());
        frame.setVisible(true);
        System.out.println("✅ Cửa sổ game đã hiển thị.");
    }
}



