package graphics.object;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import graphics.group.Group;
import constraint.Constraint;

public class Text implements GraphicalObject {
    /**
     * Text class: texts
     */
    private Graphics2D internalGraphics = null; // only for calculating bounding box
    private String text;
    private int x, y;
    private Font font;
    private Color color;
    private Group group = null;

    public static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 10);

    private Constraint<String> textConstraint = new Constraint<>();
    private Constraint<Integer> xConstraint = new Constraint<>();
    private Constraint<Integer> yConstraint = new Constraint<>();
    private Constraint<Font> fontConstraint = new Constraint<>();
    private Constraint<Color> colorConstraint = new Constraint<>();

    /**
     * Constructors
     */
    public Text(Graphics2D graphics, String text, int x, int y, Font font, Color color) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.font = font;
        this.color = color;

        if (graphics != null) {
            this.internalGraphics = (Graphics2D) graphics.create();
            this.internalGraphics.setFont(font);
            this.internalGraphics.setColor(color);
        }
    }

    public Text(String text, int x, int y) {
        this(null, text, x, y, DEFAULT_FONT, Color.BLUE);
    }

    public Text() {
        this(null, "Text", 10, 10, DEFAULT_FONT, Color.BLUE);
    }

    /**
     * Getters, setters and "users"
     * 
     * Note: user (e.g. useX) returns the constraint on the variable (X)
     */
    public Graphics2D getGraphics() {
        return this.internalGraphics;
    }

    public void setGraphics(Graphics2D graphics) {
        this.internalGraphics = graphics;
    }
    
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

    public String getText() {
        if (textConstraint.isConstrained()) {
            this.text = textConstraint.evaluate();
        }
        return this.text;
    }

    public void setText(String text) {
        if (this.text != text) {
            if (!textConstraint.isConstrained()) {
                this.text = text;
                textConstraint.notifyValueChange(false);
            } else if (textConstraint.hasCycle()) {
                textConstraint.setValue(text);
                textConstraint.notifyValueChange(false);
            }
        }
    }

    public void setText(Constraint<String> constraint) {
        textConstraint.replaceWithConstraint(constraint);
        textConstraint = constraint;
        textConstraint.setValue(this.text);
        textConstraint.notifyValueChange(true);
    }

    public Constraint<String> useText() {
        return this.textConstraint;
    }

    public Font getFont() {
        if (fontConstraint.isConstrained()) {
            this.font = fontConstraint.evaluate();
            internalGraphics.setFont(this.font);
        }
        return this.font;
    }

    public void setFont(Font font) {
        if (this.font != font) {
            if (!fontConstraint.isConstrained()) {
                this.font = font;
                internalGraphics.setFont(this.font);
                fontConstraint.notifyValueChange(false);
            } else if (fontConstraint.hasCycle()) {
                fontConstraint.setValue(font);
                fontConstraint.notifyValueChange(false);
            }
        }
    }

    public void setFont(Constraint<Font> constraint) {
        fontConstraint.replaceWithConstraint(constraint);
        fontConstraint = constraint;
        fontConstraint.setValue(this.font);
        fontConstraint.notifyValueChange(true);
    }

    public Constraint<Font> useFont() {
        return this.fontConstraint;
    }
    
    public Color getColor() {
        if (colorConstraint.isConstrained()) {
            this.color = colorConstraint.evaluate();
            internalGraphics.setColor(this.color);
        }
        return this.color;
    }

    public void setColor(Color color) {
        if (this.color != color) {
            if (!colorConstraint.isConstrained()) {
                this.color = color;
                internalGraphics.setColor(this.color);
                colorConstraint.notifyValueChange(false);
            } else if (colorConstraint.hasCycle()) {
                colorConstraint.setValue(color);
                colorConstraint.notifyValueChange(false);
            }
        }
    }

    public void setColor(Constraint<Color> constraint) {
        colorConstraint.replaceWithConstraint(constraint);
        colorConstraint = constraint;
        colorConstraint.setValue(this.color);
        colorConstraint.notifyValueChange(true);
    }

    public Constraint<Color> useColor() {
        return this.colorConstraint;
    }

    /**
     * Methods defined in the GraphicalObject interface
     */
    public void draw(Graphics2D graphics, Shape clipShape) {
        Shape oldClip = graphics.getClip();
        graphics.setClip(clipShape);

        RenderingHints oldRenderingHints = graphics.getRenderingHints();
        graphics.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        int x = getX(), y = getY();
        Font font = getFont();
        Color color = getColor();
        String text = getText();

        graphics.setFont(font);
        graphics.setColor(color);

        if (internalGraphics == null) { // steal your graphics
            internalGraphics = graphics;
        }
        int textHeight = internalGraphics.getFontMetrics().getHeight();
        for (String line : text.split("\n")) {  // deal with newlines
            graphics.drawString(line, x, y += textHeight);
        }
        graphics.setClip(oldClip);
        graphics.setRenderingHints(oldRenderingHints);
    }

    public BoundaryRectangle getBoundingBox() {
        if (internalGraphics == null) {
            return new BoundaryRectangle(x, y, -1, -1);
        }
        // The bounding box includes leading
        String text = getText();
        FontMetrics metrics = internalGraphics.getFontMetrics();
        Rectangle2D box = metrics.getStringBounds(text, internalGraphics);

        // Coordinates were relative to the reference point
        int x = getX(), y = getY();
        box.setRect(x + box.getX(), y + box.getY(), box.getWidth(), box.getHeight());
        return new BoundaryRectangle(box);
    }

    public void moveTo(int x, int y) {
        BoundaryRectangle boundingBox = getBoundingBox();
        int topLeftX = boundingBox.x;
        int topLeftY = boundingBox.y;

        int prevX = getX(), prevY = getY();
        this.setX(prevX + x - topLeftX);
        this.setY(prevY + y - topLeftY);
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
}