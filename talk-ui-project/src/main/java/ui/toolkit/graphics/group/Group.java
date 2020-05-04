package ui.toolkit.graphics.group;

import java.awt.Point;
import java.util.List;

import ui.toolkit.behavior.Behavior;
import ui.toolkit.graphics.object.AlreadyHasGroupRunTimeException;
import ui.toolkit.graphics.object.GraphicalObject;

public interface Group extends GraphicalObject {
    public Group addChild(GraphicalObject child) throws AlreadyHasGroupRunTimeException;
    public Group addChildren(GraphicalObject... children) throws AlreadyHasGroupRunTimeException;
    public Group removeChild(GraphicalObject child);
    public Group removeChildren(GraphicalObject... children);

    public Group addChildToTop(GraphicalObject child) throws AlreadyHasGroupRunTimeException;

    public Group bringChildToFront(GraphicalObject child);
    public Group resizeToChildren();
    public List<GraphicalObject> getChildren();

    public Group addBehavior(Behavior behavior);
    public Group addBehaviors(Behavior... behaviors);
    public Group removeBehavior(Behavior behavior);
    public Group removeBehaviors(Behavior... behaviors);
    public List<Behavior> getBehaviors();

    public Behavior[] getBehaviorsToAdd();
    public Behavior[] getBehaviorsToRemove();
    public Group clearBehaviorsToAdd();
    public Group clearBehaviorsToRemove();

    public Point parentToChild(Point pt);
    public Point childToParent(Point pt);
}
