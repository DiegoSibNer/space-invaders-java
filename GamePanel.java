import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    // Tamaño ventana
    public static final int WIDTH = 600;
    public static final int HEIGHT = 800;

    // Hilo del juego
    private Thread gameThread;
    private final int FPS = 60;
    private boolean running = false;

    // Jugador
    private Player player;

    // Balas
    private ArrayList<Bullet> bullets;

    // Enemigos
    private ArrayList<Enemy> enemies;
    private int enemyRows = 4;
    private int enemyCols = 8;
    private int enemySpeed = 1;
    private int enemyDirection = 1; // 1 -> derecha, -1 -> izquierda

    // Estado
    private int score = 0;
    private boolean gameOver = false;

    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);

        initGame();
    }

    private void initGame() {
        player = new Player(WIDTH / 2 - 25, HEIGHT - 100, 50, 20);
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        spawnEnemies();
        score = 0;
        gameOver = false;
    }

    private void spawnEnemies() {
        enemies.clear();
        int paddingX = 60;
        int startY = 80;
        int spacingX = (WIDTH - paddingX*2) / (enemyCols-1);
        for (int r = 0; r < enemyRows; r++) {
            for (int c = 0; c < enemyCols; c++) {
                int x = paddingX + c * spacingX;
                int y = startY + r * 60;
                enemies.add(new Enemy(x - 20, y, 40, 30));
            }
        }
    }

    public void startGameThread() {
        if (gameThread == null) {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (running) {
            update();
            repaint();

            try {
                double remaining = nextDrawTime - System.nanoTime();
                if (remaining < 0) remaining = 0;
                Thread.sleep((long)(remaining / 1000000));
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        if (gameOver) return;

        // Actualiza jugador
        player.update();

        // Mover balas
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update();
            if (!b.isAlive()) {
                it.remove();
            }
        }

        // Mover enemigos en bloque: detecta borde para invertir
        boolean reachedEdge = false;
        for (Enemy e : enemies) {
            if (enemyDirection == 1 && e.getX() + e.getWidth() >= WIDTH - 10) reachedEdge = true;
            if (enemyDirection == -1 && e.getX() <= 10) reachedEdge = true;
        }
        if (reachedEdge) {
            enemyDirection *= -1;
            for (Enemy e : enemies) e.setY(e.getY() + 20); // bajan
        } else {
            for (Enemy e : enemies) e.setX(e.getX() + enemyDirection * enemySpeed);
        }

        // Colisiones bala-enemigo
        for (Iterator<Bullet> ib = bullets.iterator(); ib.hasNext();) {
            Bullet b = ib.next();
            boolean hit = false;
            for (Iterator<Enemy> ie = enemies.iterator(); ie.hasNext();) {
                Enemy en = ie.next();
                if (b.getRect().intersects(en.getRect())) {
                    // eliminar enemigo y bala
                    ie.remove();
                    ib.remove();
                    score += 10;
                    hit = true;
                    break;
                }
            }
            if (hit) break;
        }

        // Verificar si enemigoso llegan al jugador o al fondo
        for (Enemy e : enemies) {
            if (e.getY() + e.getHeight() >= player.getY()) {
                gameOver = true;
                running = false;
            }
        }

        // Si no quedan enemigos, respawnea uno más rápido
        if (enemies.isEmpty()) {
            enemyRows = Math.min(6, enemyRows + 1);
            enemySpeed += 1;
            spawnEnemies();
        }
    }

    // Dibujado
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Antialias off para pixel look (opcional)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // Dibujar jugador
        player.draw(g2);

        // Dibujar balas
        for (Bullet b : bullets) b.draw(g2);

        // Dibujar enemigos
        for (Enemy e : enemies) e.draw(g2);

        // HUD
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Consolas", Font.PLAIN, 18));
        g2.drawString("Score: " + score, 10, 20);

        if (gameOver) {
            g2.setFont(new Font("Consolas", Font.BOLD, 48));
            String s = "GAME OVER";
            int sw = g2.getFontMetrics().stringWidth(s);
            g2.drawString(s, (WIDTH - sw) / 2, HEIGHT / 2 - 30);

            g2.setFont(new Font("Consolas", Font.PLAIN, 24));
            String s2 = "Presiona R para reiniciar";
            int sw2 = g2.getFontMetrics().stringWidth(s2);
            g2.drawString(s2, (WIDTH - sw2) / 2, HEIGHT / 2 + 10);
        }
    }

    // Input
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) player.setLeft(false);
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) player.setRight(false);
    }
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) player.setLeft(true);
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) player.setRight(true);
        if (code == KeyEvent.VK_SPACE) {
            // Disparo
            if (bullets.size() < 4) { // limitar balas concurrentes
                bullets.add(new Bullet(player.getX() + player.getWidth()/2 - 3, player.getY() - 10, 6, 12));
            }
        }
        if (code == KeyEvent.VK_R && gameOver) {
            // Reiniciar
            initGame();
            startGameThread();
        }
    }
}
