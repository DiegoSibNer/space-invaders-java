import java.awt.*;

public class Player {
    private int x, y, width, height;
    private int speed = 6;
    private boolean left = false, right = false;

    public Player(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public void update() {
        if (left) x -= speed;
        if (right) x += speed;
        if (x < 0) x = 0;
        if (x + width > GamePanel.WIDTH) x = GamePanel.WIDTH - width;
    }

    public void draw(Graphics2D g) {
        // cuerpo
        g.setColor(Color.GREEN);
        g.fillRect(x, y, width, height);
        // detalle
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x + 10, y - 8, width - 20, 8); // "canon"
    }

    // getters / setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setLeft(boolean v) { left = v; }
    public void setRight(boolean v) { right = v; }
}
