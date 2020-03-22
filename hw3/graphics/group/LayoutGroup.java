package graphics.group;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.ArrayList;

import graphics.object.GraphicalObject;
import graphics.object.BoundaryRectangle;
import graphics.object.AlreadyHasGroupRunTimeException;
import constraint.Constraint;

public class LayoutGroup implements Group {
    /**
     * LayoutGroup class
     * 
     * Automatically places its children in a certain layout
     * Options include horizontal, vertical and grid layouts
     */
    private int x, y, width, height;
    private int layout, offset;
    private int nRows, nColumns;
    private Group group = null;
    private List<GraphicalObject> children = new ArrayList<>();

    private Constraint<Integer> xConstraint = new Constraint<>();
    private Constraint<Integer> yConstraint = new Constraint<>();
    private Constraint<Integer> widthConstraint = new Constraint<>();
    private Constraint<Integer> heightConstraint = new Constraint<>();
    private Constraint<Integer> layoutConstraint = new Constraint<>();
    private Constraint<Integer> offsetConstraint = new Constraint<>();
    private Constraint<Integer> nRowsConstraint = new Constraint<>();
    private Constraint<Integer> nColumnsConstraint = new Constraint<>();

    /**
     * Constructors
     */
    public LayoutGroup(int x, int y, int width, int height,
            int layout, int offset) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.layout = layout;
        this.offset = offset;
        checkIfSupportedLayout(layout);
    }

    public LayoutGroup(int x, int y, int width, int height,
            int layout, int offset, int nRows, int nColumns) {
        this(x, y, width, height, layout, offset);
        this.nRows = nRows;
        this.nColumns = nColumns;
    }

    public LayoutGroup() {
        this(0, 0, 200, 200, HORIZONTAL, 10);
    }

    private void checkIfSupportedLayout(int layout) {
        if ((layout != HORIZONTAL) && (layout != VERTICAL) && (layout != GRID)) {
            throw new RuntimeException("Not supported layout type");
        }
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
        xConstraint.updateConstraint(constraint);
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
        yConstraint.updateConstraint(constraint);
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
        widthConstraint.updateConstraint(constraint);
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
        heightConstraint.updateConstraint(constraint);
        heightConstraint = constraint;
        heightConstraint.setValue(this.height);
        heightConstraint.notifyValueChange(true);
    }

    public Constraint<Integer> useHeight() {
        return this.heightConstraint;
    }

    public int getLayout() {
        if (layoutConstraint.isConstrained()) {
            this.layout = layoutConstraint.evaluate();
        }
        return this.layout;
    }

    public void setLayout(int layout) {
        checkIfSupportedLayout(layout);
        if (this.layout != layout) {
            if (!layoutConstraint.isConstrained()) {
                this.layout = layout;
                layoutConstraint.notifyValueChange(false);
            } else if (layoutConstraint.hasCycle()) {
                layoutConstraint.setValue(layout);
                layoutConstraint.notifyValueChange(false);
            }
        }
    }

    public void setLayout(Constraint<Integer> constraint) {
        layoutConstraint.updateConstraint(constraint);
        layoutConstraint = constraint;
        layoutConstraint.setValue(this.layout);
        layoutConstraint.notifyValueChange(true);
    }

    public Constraint<Integer> useLayout() {
        return this.layoutConstraint;
    }

    public int getOffset() {
        if (offsetConstraint.isConstrained()) {
            this.offset = offsetConstraint.evaluate();
        }
        return this.offset;
    }

    public void setOffset(int offset) {
        if (this.offset != offset) {
            if (!offsetConstraint.isConstrained()) {
                this.offset = offset;
                offsetConstraint.notifyValueChange(false);
            } else if (offsetConstraint.hasCycle()) {
                offsetConstraint.setValue(offset);
                offsetConstraint.notifyValueChange(false);
            }
        }
    }

    public void setOffset(Constraint<Integer> constraint) {
        offsetConstraint.updateConstraint(constraint);
        offsetConstraint = constraint;
        offsetConstraint.setValue(this.offset);
        offsetConstraint.notifyValueChange(true);
    }

    public Constraint<Integer> useOffset() {
        return this.offsetConstraint;
    }

    public int getNRows() {
        if (nRowsConstraint.isConstrained()) {
            this.nRows = nRowsConstraint.evaluate();
        }
        return this.nRows;
    }

    public void setNRows(int nRows) {
        if (this.nRows != nRows) {
            if (!nRowsConstraint.isConstrained()) {
                this.nRows = nRows;
                nRowsConstraint.notifyValueChange(false);
            } else if (nRowsConstraint.hasCycle()) {
                nRowsConstraint.setValue(nRows);
                nRowsConstraint.notifyValueChange(false);
            }
        }
    }

    public void setNRows(Constraint<Integer> constraint) {
        nRowsConstraint.updateConstraint(constraint);
        nRowsConstraint = constraint;
        nRowsConstraint.setValue(this.nRows);
        nRowsConstraint.notifyValueChange(true);
    }

    public Constraint<Integer> useNRows() {
        return this.nRowsConstraint;
    }

    public int getNColumns() {
        if (nColumnsConstraint.isConstrained()) {
            this.nColumns = nColumnsConstraint.evaluate();
        }
        return this.nColumns;
    }

    public void setNColumns(int nColumns) {
        if (this.nColumns != nColumns) {
            if (!nColumnsConstraint.isConstrained()) {
                this.nColumns = nColumns;
                nColumnsConstraint.notifyValueChange(false);
            } else if (nColumnsConstraint.hasCycle()) {
                nColumnsConstraint.setValue(nColumns);
                nColumnsConstraint.notifyValueChange(false);
            }
        }
    }

    public void setNColumns(Constraint<Integer> constraint) {
        nColumnsConstraint.updateConstraint(constraint);
        nColumnsConstraint = constraint;
        nColumnsConstraint.setValue(this.nColumns);
        nColumnsConstraint.notifyValueChange(true);
    }

    public Constraint<Integer> useNColumns() {
        return this.nColumnsConstraint;
    }

    /**
     * Methods defined in the GraphicalObject interface
     */
    private Object[] getGridSizes(List<GraphicalObject> children) {
        int[] rowHeight = new int[nRows];
        int[] columnWidth = new int[nColumns];

        for (int idx = 0; idx < children.size(); ++idx) {
            if (idx >= nRows * nColumns) {
                break;
            }
            GraphicalObject child = children.get(idx);
            BoundaryRectangle box = child.getBoundingBox();

            rowHeight[idx / nColumns] = Math.max(
                rowHeight[idx / nColumns], box.height);
            columnWidth[idx % nColumns] = Math.max(
                columnWidth[idx % nColumns], box.width);
        }
        return new Object[] {rowHeight, columnWidth};
    }

    public void draw(Graphics2D graphics, Shape clipShape) {
        // Intersect the clip shape with the group bounding box
        Shape commonClipArea = getBoundingBox().intersection(clipShape.getBounds());

        // Translate the new clip shape to pass to children
        AffineTransform transform = new AffineTransform();
        int x = getX(), y = getY();
        transform.translate(-x, -y);
        Shape childClipShape = transform.createTransformedShape(commonClipArea);

        // Translate the origin to draw children
        graphics.translate(x, y);
        int layout = getLayout(), offset = getOffset();
        int nRows = getNRows(), nColumns = getNColumns();

        int currentXPosition = 0, currentYPosition = 0;
        int[] rowHeight = {0}, columnWidth ={0};

        if (layout == GRID) {
            Object[] gridSizes = getGridSizes(children);
            rowHeight = (int[]) gridSizes[0];
            columnWidth = (int[]) gridSizes[1];
        }

        for (int idx = 0; idx < children.size(); ++idx) {
            if ((layout == GRID) && (idx >= nRows * nColumns)) {
                break;
            }
            GraphicalObject child = children.get(idx);
            BoundaryRectangle oldBoundingBox = child.getBoundingBox();

            switch (layout) {
                case HORIZONTAL:
                    child.moveTo(currentXPosition, 0);
                    currentXPosition += oldBoundingBox.width + offset;
                    break;
                case VERTICAL:
                    child.moveTo(0, currentYPosition);
                    currentYPosition += oldBoundingBox.height + offset;
                    break;
                case GRID:
                    child.moveTo(currentXPosition, currentYPosition);
                    if ((idx + 1) % nColumns != 0) {
                        currentXPosition += columnWidth[idx % nColumns] + offset;
                    } else {
                        currentXPosition = 0;
                        currentYPosition += rowHeight[idx / nColumns] + offset;
                    }
                    break;
                default:
                    throw new RuntimeException("Not supported layout type");
            }
            child.draw(graphics, childClipShape);
            child.moveTo(oldBoundingBox.x, oldBoundingBox.y);
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

    /**
     * Methods defined in the Group interface
     */
    public void addChild(GraphicalObject child) {
        Group childGroup = child.getGroup();
        if (childGroup != null) {
            throw new AlreadyHasGroupRunTimeException();
        } else {
            children.add(child);
            child.setGroup(this);
        }
    }

    public void removeChild(GraphicalObject child) {
        children.remove(child);
        child.setGroup(null);
    }

    public void bringChildToFront(GraphicalObject child) {
        if (children.remove(child)) {
            children.add(child);
        } else {
            throw new RuntimeException("Object is not in the group");
        }
    }

    public void resizeToChildren() {
        int newWidth = 0, newHeight = 0;
        int layout = getLayout(), offset = getOffset();

        for (GraphicalObject child: children) {
            BoundaryRectangle boundingBox = child.getBoundingBox();
            if (layout == HORIZONTAL) {
                newWidth += boundingBox.width + offset;
                newHeight = Math.max(newHeight, boundingBox.height);
            } else if (layout == VERTICAL) {
                newHeight += boundingBox.height + offset;
                newWidth = Math.max(newWidth, boundingBox.width);
            }
        }

        if (layout == GRID) {
            int nColumns = getNColumns();
            Object[] gridSizes = getGridSizes(children);
            int[] rowHeight = (int[]) gridSizes[0];
            int[] columnWidth = (int[]) gridSizes[1];

            int countColumn = Math.min(nColumns, children.size());
            for (int i = 0; i < countColumn; ++i) {
                newWidth += columnWidth[i] + offset;
            }
            int countRow = (children.size() - 1) / nColumns + 1;
            for (int i = 0; i < countRow; ++i) {
                newHeight += rowHeight[i] + offset;
            }
        }

        if (children.size() > 0) {
            if (layout != VERTICAL) {
                newWidth -= offset;
            }
            if (layout != HORIZONTAL) {
                newHeight -= offset;
            }
        }
        this.setWidth(newWidth);
        this.setHeight(newHeight);
    }

    public List<GraphicalObject> getChildren() {
        List<GraphicalObject> childrenCopy
            = new ArrayList<GraphicalObject>(children);
        return childrenCopy;
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