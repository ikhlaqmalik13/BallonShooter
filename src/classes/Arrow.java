package classes;

public class Arrow {

    int x;
    int y;
    int width;
    int height;
    boolean isActive = true;

    public Arrow(int x, int y ){
        this.x = x;
        this.y = y;
        this.width = 5;
        this.height = 20;
    }

    public void move(){
        this.y -= 5; 
        if (this.y <= 0) {
            this.isActive = false;
        }
    }
    
}
