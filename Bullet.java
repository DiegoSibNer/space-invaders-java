import java.awt.*;

public class Bullet {
    private int x, y, width, height;
    private int speed = 10;
    private boolean alive = true;

    public Bullet(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public void update() {
        y -= speed;
        if (y + height < 0) alive = false;
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, width, height);
    }

    public boolean isAlive() { return alive; }
    public Rectangle getRect() { return new Rectangle(x, y, width, height); }
}
