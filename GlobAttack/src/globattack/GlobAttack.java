package globattack;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont; //custom font

public class GlobAttack extends PApplet{
    boolean left, right, up, down;
    float enemySpeed = 1f;
    float bulletSpeed = 5;
    ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    float spawnRate = 300;
    PImage backgroundImg;
    float playerX = 256; //player starting positionX
    float playerY = 352; //player starting positionY
    PImage[] playerAnim =  new PImage[6]; //holds images of 6 frames of animation
    int animationFrame = 1;
    PImage[][] enemyAnimations = new PImage[3][6];
    PImage[] explosionAnimation = new PImage[6];
    int score = 0;
    int highScore = 0;
    PFont scoreFont;
    PImage gameOverImg ;
    PImage restartButton;
    PImage menuImg;

    enum GameState {
        OVER, RUNNING, START
    }
    static GameState currentState;

    public static void main(String[] args) {
        PApplet.main("globattack.GlobAttack");
    }

    public void setup(){
        //enemies.add(new Enemy(random(0, width), random(0, width)));
        backgroundImg = loadImage("Images/Background.png");
        menuImg = loadImage("Images/MenuBackground.png");
        menuImg.resize(width, height);
        for (int i = 1; i <= 6; i++) { //six frames of animations from Bat_Brains_1 to Bat_Brains_6
            playerAnim[i-1]=loadImage("Images/Bat_Brains_" + i + ".png");
            playerAnim[i-1].resize(60, 0);
        }
        for (int j = 1; j <= 6; j++) {
            enemyAnimations[0][j - 1] = loadImage("Images/Bat_Purple" + j + ".png"); //purple bat sprites
            enemyAnimations[1][j - 1] = loadImage("Images/Bat_Square" + j + ".png"); //square bat animations
            enemyAnimations[2][j - 1] = loadImage("Images/Bat_Booger" + j + ".png"); //bat booger animations
            enemyAnimations[0][j - 1].resize(60, 0);
            enemyAnimations[1][j - 1].resize(60, 0);
            enemyAnimations[2][j - 1].resize(60, 0);
        }
        for (int i = 1; i <= 6; i++) {
            explosionAnimation[i - 1] = loadImage("Images/Explosion_FX" + i + ".png");
            explosionAnimation[i - 1].resize(60, 0);
        }
        currentState = GameState.START;
        //currentState = GameState.RUNNING;
        gameOverImg = loadImage("Images/GameOverImg.png");
        gameOverImg.resize(300, 0);
        restartButton = loadImage("Images/WoodButton.png");
        restartButton.resize(240, 50);

    }
    public void settings() {
        size(514, 704);
    }

    public void drawBackground() {
        background(250);
        imageMode(CORNER);
        image(backgroundImg, 0, 0);
    }
    public void drawScore() {
        scoreFont = createFont("Leelawadee UI Bold", 26, true);
        textFont(scoreFont);
        fill(255, 255, 255);
        textAlign(CENTER);
        text("Score: " + score, width - 90, 40);
    }
    public void drawMainMenu(){
        background(250);
        imageMode(CORNER);
        image(menuImg, 0, 0);
        imageMode(CENTER);
        image(restartButton, width / 2, height / 2 + 100);
        textAlign(CENTER);
        text("Start ", width / 2, height / 2 + 105);

    }
    public void drawGameOver(){
        imageMode(CENTER);
        image(gameOverImg, width / 2, height / 2);
        fill(122, 64, 51);
        textAlign(CENTER);
        text("Game Over ", width / 2, height / 2 - 100);
        text("Score: " + score, width / 2, height / 2 - 40);
        text("High Score: " + highScore, width / 2, height / 2 + 10);
        image(restartButton, width / 2, height / 2 + 100);
        fill(255, 255, 255);
        text("Restart ", width / 2, height / 2 + 105);
    }

    public void draw(){ //called every frame
        drawBackground();
        switch(currentState){
            case START:
                drawMainMenu();
                break;
            case OVER:
                drawGameOver();
                break;

            case RUNNING:
                drawScore();
                noStroke();
                if (frameCount % 5 == 0) {
                    animationFrame++;
                    animationFrame = animationFrame % 6;
                    for(int i = 0; i < enemies.size(); i++) {
                        Enemy en = enemies.get(i);
                        if(en.isDead == true) {
                            en.explosionFrame ++;
                            if (en.explosionFrame == 5) {
                                enemies.remove(i);
                            }
                        }
                    }
                }
                drawPlayer();
                increaseDifficulty();
                for(int i = 0; i < enemies.size(); i++){
                    Enemy en = enemies.get(i);
                    en.move(playerX, playerY);
                    en.drawEnemy();
                    for (int j = 0; j < bullets.size(); j++) {
                        Bullet b = bullets.get(j);
                        if (abs(b.x - en.x) < 15 && abs(b.y - en.y) < 15 && en.isDead == false ) { //bullet collision check
                            en.isDead = true;
                            bullets.remove(j);
                            break;
                        }
                    }
                    if (abs(playerX - en.x) < 15 && abs(playerY - en.y) < 15) {
                        if (score > highScore) {
                            highScore = score;
                        }
                        currentState = GameState.OVER;
                    }
                }
                for(int j = 0; j < bullets.size(); j++){
                    Bullet bull = bullets.get(j);
                    bull.move();
                    bull.drawBullet();
                    if (bull.x < 0 || bull.x > width || bull.y < 0 || bull.y > height) { //enemy collides w/ bullets
                        score += 1;
                        bullets.remove(bull);
                    }
                }

        }
    }
    public void keyPressed() {
        switch(currentState) {
            case RUNNING:
                if (key == 'w') {
                    up = true;
                    System.out.println("Hi");
                }
                if (key == 'a') {
                    left = true;
                    System.out.println("Hi1");
                }
                if (key == 's') {
                    down = true;
                    System.out.println("Hi2");
                }
                if (key == 'd') {
                    right = true;
                    System.out.println("Hi3");
                }
        }
    }
    public void keyReleased() {
        switch(currentState) {
            case RUNNING:
                if (key == 'w') {
                    up = false;
                    System.out.println("Hiiiii");
                }
                if (key == 'a') {
                    left = false;
                    System.out.println("H1");
                }
                if (key == 's') {
                    down = false;
                    System.out.println("H2");
                }
                if (key == 'd') {
                    right = false;
                    System.out.println("H3");
                }
        }
    }
    public void drawPlayer(){
        if (up) {
            playerY -= 5;
        }
        else if (left) {
            playerX -= 5;
        }
        else if (right) {
            playerX += 5;
        }
        else if (down) {
            playerY += 5;
        }
        fill(0, 230, 172);
        //rectMode(CENTER);
        //playerX = constrain(playerX, 0, width);
        //playerY = constrain(playerY, 0, height);
        //rect(playerX, playerY, 30, 30);
        imageMode(CENTER);
        image(playerAnim[0], playerX, playerY);
        image(playerAnim[animationFrame], playerX, playerY);
        playerX = constrain(playerX, 70, width-70);
        playerY = constrain(playerY, 70, height-70);


    }
    public void increaseDifficulty(){ //spawn enemies and change their speed
        if (frameCount % spawnRate == 0) { //add new enemy after every 5 sec (since SpawnRate = 60 framecount * 5 sec)
            //enemies.add(new Enemy(random(0, width), random(0, width)));
            generateEnemy();
            if (enemySpeed < 3) {
                enemySpeed += 0.1f;
            }
            if (spawnRate > 50) {
                spawnRate -= 10;
            }
        }

    }
    public void generateEnemy(){
        int side = (int) random(0, 2);
        int side2 = (int) random(0, 2);
        if (side % 2 == 0) { // top and bottom
            enemies.add(new Enemy(random(0, width), height * (side2 % 2), (int) random(0, 3)));
        } else { // sides
            enemies.add(new Enemy(width * (side2 % 2), random(0, height), (int) random(0, 3)));
        }
    }
    class Enemy {
        float x, y, vx, vy; //direction of enemy
        int enemyType = 0;
        boolean isDead = false;
        int explosionFrame = 0;

        Enemy(float x, float y, int enemyType) {
            this.x = x;
            this.y = y;
            this.enemyType = enemyType;
        }

        public void drawEnemy() {
            if(isDead == false){
                imageMode(CENTER);
                image(enemyAnimations[enemyType][animationFrame], x, y);
            }

        }

        public void move(float px, float py) {
            if(isDead == false) {
                float angle = atan2(py - y, px - x);
                vx = cos(angle);
                vy = sin(angle);
                x += vx * enemySpeed;
                y += vy * enemySpeed;
            }
            else {
                image(explosionAnimation[explosionFrame], x, y);
            }
        }
    }
    public void mousePressed() {
        switch(currentState){
            case START:
                if (mouseX > (width / 2 - 120) && mouseX < (width / 2 + 120) && mouseY > height / 2 + 100 - 25 && mouseY < (height / 2 + 100 + 25)) {
                    currentState = GameState.RUNNING;
                }
                break;
            case RUNNING:
                float dx = mouseX - playerX;
                float dy = mouseY - playerY;
                float angle = atan2(dy, dx);
                float vx = bulletSpeed * cos(angle);
                float vy = bulletSpeed * sin(angle);
                bullets.add(new Bullet(playerX, playerY, vx, vy));
                break;
            case OVER:
                //checks location of mouse
                if (mouseX > (width / 2 - 120) && mouseX < (width / 2 + 120) && mouseY > height / 2 + 100 - 25 && mouseY < (height / 2 + 100 + 25)) {
                    for (int i = 0; i < enemies.size(); i++) {
                        enemies.remove(i);
                        score = 0;
                        enemySpeed = 1f;
                        spawnRate = 300;
                    }
                    currentState = GameState.RUNNING;
                }
                break;
        }

    }
    public class Bullet {
        float x, y, vx, vy;
        Bullet(float x, float y, float vx, float vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }

        void drawBullet() {
            fill(0, 255, 0);
            ellipse(x, y, 10, 10);
        }
        void move() {
            x += vx;
            y += vy;
        }
    }
}
