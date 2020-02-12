import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.BasicStroke;


public class TestTestFrame extends TestFrame {
    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
        new TestTestFrame(args);
    }

    public TestTestFrame(String[] args) {
        super("TestTestFrame", 200, 200);
        println("First Pause");
        pause();
        println("drawing a rectangle");
        drawsomething();
        println("20 x's with sleep(250) in between");
        for (int x = 0; x < 20; x ++) {
            print("x");
            sleep(250);
        }
        println("");
        println("done");
        pause();
        println("close the window to exit");
    }

    private class pretendrect implements GraphicalObject {
        public void draw(Graphics2D graphics, Shape clipRect) {
            graphics.setStroke(new BasicStroke(3));
            graphics.setColor(Color.red);
            graphics.drawRect(3, 3, 10, 10);
        };
        public BoundaryRectangle getBoundingBox() {return null;};
        public void moveTo(int x, int y) {};
        public Group getGroup() {return null;};
        public void setGroup(Group group) {};
        public boolean contains(int x, int y) {return false;};
        public pretendrect(){}
    }

    private void drawsomething() {
        redraw(new pretendrect());
    }
}

