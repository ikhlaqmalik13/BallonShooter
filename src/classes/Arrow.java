package classes;

public class Arrow {

    public int x;
    public int y;
    public int width;
    public int height;
    public boolean isActive = true;

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
