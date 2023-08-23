package classes;

public class Ballon {

    int x;
    int y;
    boolean isActive = true;
    int width;
    int height;

    public Ballon(int x, int y){
        this.x = x;
        this.y = y;
        this.width = 30;
        this.height = 50;
    }

    public void move(){
        this.y += 5; 
        if (this.y >= 700 - 100) {
            this.isActive = false;
        }
    }

}
