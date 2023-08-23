package home;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import classes.Arrow;
import classes.Ballon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BallonShooter extends JFrame implements KeyListener  {

    public static final int GAME_WIDTH = 500;
    public static final int GAME_HEIGHT = 700;

    int shooterX = GAME_WIDTH/2;
    int shooterY = GAME_HEIGHT - 49;

    List<Ballon> ballons = new ArrayList<Ballon>();
    List<Arrow> arrows = new ArrayList<Arrow>();
    Timer gameTimer;
    int updatedBallonsCounter = 0;

    int totalBallons =  0;
    int firedBallons = 0;
    int toalArrowsShooted = 0;

    JLabel totalBalloonsLabel;
    JLabel firedBalloonsLabel;
    JLabel totalArrowsLabel;
    BufferedImage bufferedImage = null;


    public BallonShooter(){
        super("Ballon Shooter");

        try{
            bufferedImage = ImageIO.read(new File("src\\res\\images\\sky.png"));
        }catch (IOException e) {
            e.printStackTrace();
        }

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bufferedImage != null) {
                    g.drawImage(bufferedImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        setContentPane(backgroundPanel);

        setBounds(0, 0, GAME_WIDTH, GAME_HEIGHT);
        setFocusable(true);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        addKeyListener(this);


        gameTimer = new Timer();
        gameTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                if(updatedBallonsCounter % 20 == 0  ){
                    createRandomBalloons();
                }
                if(updatedBallonsCounter % 95 == 0){
                    playAudio("balloon_fall_scene");
                }
                updateBalloons();
                updateArrows();
                updatedBallonsCounter ++;
                repaint();
            }
            
        }, 0, 100);

        totalBalloonsLabel = new JLabel("Total Balloons: " + totalBallons);
        totalBalloonsLabel.setBounds(10, 10, 150, 20);
        add(totalBalloonsLabel);

        firedBalloonsLabel = new JLabel("Balloons Hit: " + firedBallons);
        firedBalloonsLabel.setBounds(10, 30, 150, 20);
        add(firedBalloonsLabel);

        totalArrowsLabel = new JLabel("Total Arrows Shot: " + toalArrowsShooted);
        totalArrowsLabel.setBounds(10, 50, 150, 20);
        add(totalArrowsLabel);
        
    }

    public void paint(Graphics g){
        super.paint(g);
        g.setColor(Color.GREEN);

        for (Ballon balloon : ballons) {
            if (balloon.isActive) {
                g.fillOval(balloon.x, balloon.y, balloon.width, balloon.height);
            }
        }

        g.setColor(Color.BLACK);
        g.fillRect(shooterX, shooterY , 40, 40);

        for(Arrow arrow : arrows){
            if(arrow.isActive){
                g.fillRect(arrow.x,arrow.y, 5, 20);
            }
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        switch(e.getKeyChar()){

            case 'w' : {
                shootArrow();
                break;
            }
            case 'a' : {
                if(shooterX <= 0){
                    break;
                }
                shooterX -= 10;
                break;
            }
            case 'd' : {
                if(shooterX >= GAME_WIDTH - 40){
                    break;
                }
                shooterX += 10;
                break;
            }
            default : {
                break;
            }

        }

        repaint();
        
    }

    private void updateBalloons() {
        Iterator<Ballon> balloonIterator = ballons.iterator();
        while (balloonIterator.hasNext()) {
            Ballon balloon = balloonIterator.next();
            if (balloon.isActive) {
                balloon.move();
                Iterator<Arrow> arrowIterator = arrows.iterator();
                while (arrowIterator.hasNext()) {
                    Arrow arrow = arrowIterator.next();
                    if (arrow.isActive && checkCollision(balloon, arrow)) {
                        playAudio("hit_arrow");
                        firedBallons++;
                        firedBalloonsLabel.setText("Total Balloons Hit: " + String.valueOf(firedBallons));
                        balloon.isActive = false;
                        arrow.isActive = false;
                        break; 
                    }
                }
            } else {
                balloonIterator.remove();
                playAudio("water_drop_splash");
            }
        }
    }
        

    private void updateArrows() {
        Iterator<Arrow> iterator = arrows.iterator();
        while (iterator.hasNext()) {
            Arrow arrow = iterator.next();
            if (arrow.isActive) {
                arrow.move();
            } else {
                iterator.remove();
            }
        }
    }

    private void createRandomBalloons() {
        int randomX = (int) (Math.random() * (GAME_WIDTH - 30));
        ballons.add(new Ballon(randomX, 0));
        totalBallons++;
        totalBalloonsLabel.setText("Total Balloons: " + String.valueOf(totalBallons));
    }

    private boolean checkCollision(Ballon b, Arrow a){
        Rectangle balloonRect = new Rectangle(b.x, b.y, b.width, b.height);
        Rectangle arrowRect = new Rectangle(a.x, a.y, a.width, a.height);
        return balloonRect.intersects(arrowRect);
    }

    private void shootArrow(){
       playAudio("shoot_arrow");
       arrows.add(new Arrow(shooterX+20, GAME_HEIGHT-70));
       toalArrowsShooted++;
       totalArrowsLabel.setText("Total Arrows Shot: " + String.valueOf(toalArrowsShooted));
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private static void playAudio(String noteFileName) {
        File file = new File("src/res/wavs/" + noteFileName + ".wav");
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

}
