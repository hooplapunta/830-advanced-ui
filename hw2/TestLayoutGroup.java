import java.awt.*;

import graphics.object.*;
import graphics.group.*;

public class TestLayoutGroup extends TestFrame {

    private static final long serialVersionUID = 1L;

    public static void main(String[] args) throws Exception {
        new TestLayoutGroup(args);
    }

    public TestLayoutGroup(String[] args) throws Exception {
        super("TestLayoutGroup", 500, 200);

        println("topGroup");
        Group topGroup = new SimpleGroup(0, 0, 500, 200);
        addChild(topGroup);
        
        int nObjects = 4;
        try {
            nObjects = Integer.parseInt(args[0]);
            println("nObjects = " + nObjects);
        } catch (Exception e) {
            println("usage:  TestLayoutGroup [# of graphical objects]\n"
                    + "using " + nObjects + " objects by default");
        }

        println("creating black frame");
        topGroup.addChild(new OutlineRect(9, 9, 481, 181, Color.black, 1));

        println("creating LayoutGroup inside black frame");
        Group group = new LayoutGroup(10, 10, 480, 180, Group.HORIZONTAL, 5);
        topGroup.addChild(group);

        println("creating random OutlineRects");
        GraphicalObject[] objects = new GraphicalObject[nObjects];
        Color[] colors = { Color.black, Color.red, Color.blue };
        for (int i = 0; i < nObjects; ++i) {
            objects[i] = new OutlineRect(random(200), random(200), 30 + random(20),
                    30 + random(20), (Color) random(colors), 1 + random(5));
            group.addChild(objects[i]);
        }
        redraw(topGroup);
        pause();

        println("shuffling rectangles 10 times");
        GraphicalObject front = objects[objects.length - 1];
        for (int i = 0; i < 10; ++i) {
            GraphicalObject gobj;
            while ((gobj = (GraphicalObject) random(objects)) == front)
                ;
            group.bringChildToFront(gobj);
            front = gobj;
            redraw(topGroup);
            sleep(1000);
        }
        pause();

        println("doubling rectangle widths");
        for (int i = 0; i < objects.length; ++i) {
            OutlineRect r = (OutlineRect) objects[i];
            r.setWidth(r.getWidth() * 2);
            redraw(topGroup);
            sleep(1000);
        }
        println("close the window to exit");
    }
}