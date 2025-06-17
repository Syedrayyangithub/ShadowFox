package com.example.chat;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageGenerator {
    public static void main(String[] args) throws IOException {
        // Adjust the path as needed for your project structure
        String basePath = "src/main/resources/images/";
        new File(basePath).mkdirs();
        createLogo(basePath + "logo.png");
        createInstructionsIcon(basePath + "instructions.png");
        createChatIcon(basePath + "chat.png");
        System.out.println("Images generated in " + basePath);
    }

    private static void createLogo(String path) throws IOException {
        int w = 220, h = 60;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(44, 62, 80));
        g.fillRect(0, 0, w, h);
        g.setFont(new Font("SansSerif", Font.BOLD, 32));
        g.setColor(new Color(236, 240, 241));
        g.drawString("ShadowFox", 20, 40);
        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }

    private static void createInstructionsIcon(String path) throws IOException {
        int size = 48;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(52, 152, 219));
        g.fillOval(0, 0, size, size);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 32));
        g.drawString("i", size / 2 - 7, size / 2 + 12);
        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }

    private static void createChatIcon(String path) throws IOException {
        int w = 48, h = 48;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(39, 174, 96));
        g.fill(new RoundRectangle2D.Double(4, 8, 40, 28, 16, 16));
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("💬", 10, 32);
        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }
}