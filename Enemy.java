import java.awt.*;

public class Enemy {
    private int x, y, width, height;
    private Color color;

    public Enemy(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        // color aleatorio simple
        this.color = new Color((int)(Math.random()*156)+100, (int)(Math.random()*156)+100, (int)(Math.random()*156)+100);
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        // detalles "ojos"
        g.fillRect(x + 8, y + 8, 6, 6);
        g.fillRect(x + width - 14, y + 8, 6, 6);
    }

    public Rectangle getRect() { return new Rectangle(x, y, width, height); }

    // getters / setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setX(int nx) { x = nx; }
    public void setY(int ny) { y = ny; }
}
