package ui.toolkit.graphics.group;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import ui.toolkit.behavior.Behavior;
import ui.toolkit.behavior.TopGroup;
import ui.toolkit.constraint.Constraint;
import ui.toolkit.constraint.NoConstraint;
import ui.toolkit.graphics.object.AlreadyHasGroupRunTimeException;
import ui.toolkit.graphics.object.BoundaryRectangle;
import ui.toolkit.graphics.object.GraphicalObject;

public class SimpleGroup implements Group {
    /**
     * SimpleGroup class: a group of objects at their fixed positions
     */
    private int x, y, width, height;
    private Group group = null;
    private List<GraphicalObject> children = new ArrayList<>();

    protected List<Behavior> behaviors = new ArrayList<>();
    private List<Behavior> behaviorsToAdd = new ArrayList<>();
    private List<Behavior> behaviorsToRemove = new ArrayList<>();

    private Constraint<Integer> xConstraint = new NoConstraint<>();
    private Constraint<Integer> yConstraint = new NoConstraint<>();
    private Constraint<Integer> widthConstraint = new NoConstraint<>();
    private Constraint<Integer> heightConstraint = new NoConstraint<>();

    /**
     * Constructors
     */
    public SimpleGroup(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public SimpleGroup() {
        this(0, 0, 200, 200);
    }

    /**
     * Getters, setters and "users"
     * 
     * Note: user (e.g. useX) returns the constraint on the variable (X)
     */
    public int getX() {
        if (xConstraint.isConstrained()) {
            this.x = xConstraint.evaluate();
        }
        return this.x;
    }

    public void setX(int x) {
        if (this.x != x) {
            if (!xConstraint.isConstrained()) {
                this.x = x;
                xConstraint.notifyValueChange(false);
            } else if (xConstraint.hasCycle()) {
                // if no cycle, set a constrained x is no-op
                // if cycle, set local value and do multi-way constraint
                xConstraint.setValue(x);
                xConstraint.notifyValueChange(false);
            }
        }
    }

    public void setX(Constraint<Integer> constraint) {
        // update dependency graph for the new constraint
        xConstraint.replaceWithConstraint(constraint);
        xConstraint = constraint;
        xConstraint.setValue(this.x);
        xConstraint.notifyValueChange(true);
    }

    public Constraint<Integer> useX() {
        return this.xConstraint;
    }

    public int getY() {
        if (yConstraint.isConstrained()) {
            this.y = yConstraint.evaluate();
        }
        return this.y;
    }

    public void setY(int y) {
        if (this.y != y) {
            if (!yConstraint.isConstrained()) {
                this.y = y;
                yConstraint.notifyValueChange(false);
            } else if (yConstraint.hasCycle()) {
                yConstraint.setValue(y);
                yConstraint.notifyValueChange(false);
            }
        }
    }

    public void setY(Constraint<Integer> constraint) {
        yConstraint.replaceWithConstraint(constraint);
        yConstraint = constraint;
        yConstraint.setValue(this.y);
        yConstraint.notifyValueChange(true);
    }

    public Constraint<Integer> useY() {
        return this.yConstraint;
    }

    public int getWidth() {
        if (widthConstraint.isConstrained()) {
            this.width = widthConstraint.evaluate();
        }
        return this.width;
    }

    public void setWidth(int width) {
        if (this.width != width) {
            if (!widthConstraint.isConstrained()) {
                this.width = width;
                widthConstraint.notifyValueChange(false);
            } else if (widthConstraint.hasCycle()) {
                widthConstraint.setValue(width);
                widthConstraint.notifyValueChange(false);
            }
        }
    }

    public void setWidth(Constraint<Integer> constraint) {
        widthConstraint.replaceWithConstraint(constraint);
        widthConstraint = constraint;
        widthConstraint.setValue(this.width);
        widthConstraint.notifyValueChange(true);
    }

    public Constraint<Integer> useWidth() {
        return this.widthConstraint;
    }

    public int getHeight() {
        if (heightConstraint.isConstrained()) {
            this.height = heightConstraint.evaluate();
        }
        return this.height;
    }

    public void setHeight(int height) {
        if (this.height != height) {
            if (!heightConstraint.isConstrained()) {
                this.height = height;
                heightConstraint.notifyValueChange(false);
            } else if (heightConstraint.hasCycle()) {
                heightConstraint.setValue(height);
                heightConstraint.notifyValueChange(false);
            }
        }
    }

    public void setHeight(Constraint<Integer> constraint) {
        heightConstraint.replaceWithConstraint(constraint);
        heightConstraint = constraint;
        heightConstraint.setValue(this.height);
        heightConstraint.notifyValueChange(true);
    }

    public Constraint<Integer> useHeight() {
        return this.heightConstraint;
    }

    /**
     * Methods defined in the GraphicalObject interface
     */
    public void draw(Graphics2D graphics, Shape clipShape) {
        // Intersect the clip shape with the group bounding box
        Shape commonClipArea = getBoundingBox().intersection(clipShape.getBounds());

        // Translate the new clip shape to pass to children
        int x = getX(), y = getY();
        AffineTransform transform = new AffineTransform();
        transform.translate(-x, -y);
        Shape childClipShape = transform.createTransformedShape(commonClipArea);

        // Translate the origin to draw children
        graphics.translate(x, y);
        for (GraphicalObject child : children) {
            child.draw(graphics, childClipShape);
        }
        graphics.translate(-x, -y);
    }

    public BoundaryRectangle getBoundingBox() {
        int x = getX(), y = getY(), width = getWidth(), height = getHeight();
        return new BoundaryRectangle(x, y, width, height);
    }

    public void moveTo(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        if (this.group != null && group != null) {
            throw new AlreadyHasGroupRunTimeException();
        }
        this.group = group;
    }

    public boolean contains(int x, int y) {
        return getBoundingBox().contains(x, y);
    }

    public boolean contains(Point pt) {
        return contains(pt.x, pt.y);
    }

    /**
     * Methods defined in the Group interface
     */
    public Group addChild(GraphicalObject child) {
        Group childGroup = child.getGroup();
        if (childGroup != null) {
            throw new AlreadyHasGroupRunTimeException();
        } else {
            children.add(child);
            child.setGroup(this);
            if (child instanceof Group) {
                Group groupChild = (Group) child;
                addBehaviors(groupChild.getBehaviorsToAdd());
                removeBehaviors(groupChild.getBehaviorsToRemove());
                groupChild.clearBehaviorsToAdd().clearBehaviorsToRemove();
            }
        }
        return this;
    }

    public Group addChildren(GraphicalObject... children) {
        for (GraphicalObject child : children) {
            addChild(child);
        }
        return this;
    }

    public Group removeChild(GraphicalObject child) {
        children.remove(child);
        child.setGroup(null);
        if (child instanceof Group) {
            for (Behavior behavior : ((Group) child).getBehaviors()) {
                removeBehavior(behavior);
            }
        }
        return this;
    }

    public Group removeChildren(GraphicalObject... children) {
        for (GraphicalObject child : children) {
            removeChild(child);
        }
        return this;
    }

    public Group addBehavior(Behavior behavior) {
        if (behavior.getGroup() == null) {
            behavior.setGroup(this);
            behaviors.add(behavior);
        }
        if (group != null) {
            System.out.println(group);
            group.addBehavior(behavior);
        } else {
            behaviorsToAdd.add(behavior);
        }
        return this;
    }

    public Group addBehaviors(Behavior... behaviors) {
        for (Behavior behavior : behaviors) {
            addBehavior(behavior);
        }
        return this;
    }

    public Group removeBehavior(Behavior behavior) {
        if (group != null) {
            group.removeBehavior(behavior);
        } else {
            behavior.setGroup(null);
            behaviorsToRemove.add(behavior);
        }
        return this;
    }

    public Group removeBehaviors(Behavior... behaviors) {
        for (Behavior behavior : behaviors) {
            removeBehavior(behavior);
        }
        return this;
    }

    public List<Behavior> getBehaviors() {
        return new ArrayList<Behavior>(behaviors);
    }

    public Behavior[] getBehaviorsToAdd() {
        return behaviorsToAdd.stream().toArray(Behavior[]::new);
    }

    public Behavior[] getBehaviorsToRemove() {
        return behaviorsToRemove.stream().toArray(Behavior[]::new);
    }

    public Group clearBehaviorsToAdd() {
        behaviorsToAdd.clear();
        return this;
    }

    public Group clearBehaviorsToRemove() {
        behaviorsToRemove.clear();
        return this;
    }

    public Group bringChildToFront(GraphicalObject child) {
        if (children.remove(child)) {
            children.add(child);
        } else {
            throw new RuntimeException("Object is not in the group");
        }
        return this;
    }

    public Group resizeToChildren() {
        int newWidth = 0, newHeight = 0;
        for (GraphicalObject child : children) {
            BoundaryRectangle box = child.getBoundingBox();
            newWidth = Math.max(newWidth, (int) box.getMaxX());
            newHeight = Math.max(newHeight, (int) box.getMaxY());
        }
        this.setWidth(newWidth);
        this.setHeight(newHeight);
        return this;
    }

    public List<GraphicalObject> getChildren() {
        return new ArrayList<GraphicalObject>(children);
    }

    public Point parentToChild(Point pt) {
        int x = getX(), y = getY();
        int childX = pt.x - x;
        int childY = pt.y - y;
        return new Point(childX, childY);
    }

    public Point childToParent(Point pt) {
        int x = getX(), y = getY();
        int parentX = pt.x + x;
        int parentY = pt.y + y;
        return new Point(parentX, parentY);
    }
}