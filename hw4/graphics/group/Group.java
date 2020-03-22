package graphics.group;

import java.awt.Point;
import java.util.List;

import graphics.object.GraphicalObject;
import graphics.object.AlreadyHasGroupRunTimeException;

public interface Group extends GraphicalObject {
    public void addChild(GraphicalObject child) throws AlreadyHasGroupRunTimeException;
    public void removeChild(GraphicalObject child);
    public void bringChildToFront(GraphicalObject child);
    public void resizeToChildren();
    public List<GraphicalObject> getChildren();
    public Point parentToChild(Point pt);
    public Point childToParent(Point pt);
}
