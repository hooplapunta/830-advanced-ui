package ui.toolkit.behavior;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ui.toolkit.constraint.Constraint;
import ui.toolkit.graphics.group.Group;
import ui.toolkit.graphics.object.AlreadyHasGroupRunTimeException;
import ui.toolkit.graphics.object.BoundaryRectangle;
import ui.toolkit.graphics.object.GraphicalObject;

public class InteractiveWindowGroup extends JFrame implements Group {
    private static final long serialVersionUID = 1L;

    protected BufferedImage buffer;
    private JComponent canvas;
    private Insets insets;

    private List<GraphicalObject> children = new ArrayList<>();
    private List<Behavior> behaviors = new ArrayList<>();
    private boolean behaviorsSorted = false;

    /**
     * InteractiveWindowGroup constructor Make a top-level window with specified
     * title, width and height
     * 
     * @param title  the title of the window
     * @param width  the width of the window
     * @param height the height of the window
     */
    public InteractiveWindowGroup(String title, int width, int height) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        WindowMouseListener mouseListener = new WindowMouseListener();
        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);
        this.addMouseWheelListener(mouseListener);
        this.addKeyListener(new WindowKeyListener());

        canvas = new JPanel() {
            private static final long serialVersionUID = 1L;

            public void paintComponent(Graphics graphics) {
                if (buffer != null) {
                    graphics.drawImage(buffer, 0, 0, null);
                }
            }
        };
        canvas.setLayout(null);
        canvas.setBackground(Color.white);
        canvas.setPreferredSize(new Dimension(width, height));

        this.add(canvas);
        this.pack();
        this.setVisible(true);
        this.makeBuffer(width, height);
        this.redraw();
        this.insets = getInsets();
    }

    private void handleBehaviorEvent(BehaviorEvent behaviorEvent) {
        if (behaviors.isEmpty()) {
            return;
        }
        if (!behaviorsSorted) {
            Collections.sort(behaviors);
            behaviorsSorted = true;
        }

        Behavior lastBehavior = behaviors.get(0);
        boolean eventConsumed = false;
        for (Behavior behavior : behaviors) {
            if (eventConsumed && behavior.compareTo(lastBehavior) > 0) {
                break;
            }
            eventConsumed = behavior.check(behaviorEvent) || eventConsumed;
        }
        this.redraw();
    }

    // Mouse listener
    private class WindowMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent event) {
            int id = BehaviorEvent.MOUSE_DOWN_ID;
            handleBehaviorEvent(getBehaviorEvent(event, id));
        }

        public void mouseReleased(MouseEvent event) {
            int id = BehaviorEvent.MOUSE_UP_ID;
            handleBehaviorEvent(getBehaviorEvent(event, id));
        }

        public void mouseMoved(MouseEvent event) {
            int id = BehaviorEvent.MOUSE_MOVE_ID;
            handleBehaviorEvent(getBehaviorEvent(event, id));
        }

        public void mouseDragged(MouseEvent event) {
            int id = BehaviorEvent.MOUSE_DRAG_ID;
            handleBehaviorEvent(getBehaviorEvent(event, id));
        }

        public void mouseClicked(MouseEvent event) {
            int id = BehaviorEvent.MOUSE_CLICK_ID;
            handleBehaviorEvent(getBehaviorEvent(event, id));
        }

        public void mouseWheelMoved(MouseWheelEvent event) {
            int id = BehaviorEvent.SCROLLWHEEL_ID;
            handleBehaviorEvent(getBehaviorEvent(event, id));
        }

        // Convert an awt MouseEvent to our BehaviorEvent
        private BehaviorEvent getBehaviorEvent(MouseEvent event, int id) {
            return new BehaviorEvent(getModifiers(event), getKey(event, id), id, event.getX() - insets.left,
                    event.getY() - insets.top - 1 // at least this works on MacOS
            );
        }

        // Get mouse event keys
        private int getKey(MouseEvent event, int id) {
            if (BehaviorEvent.isMouseEvent(id)) {
                int button = event.getButton();
                if (button == MouseEvent.BUTTON1) {
                    return BehaviorEvent.LEFT_MOUSE_KEY;
                } else if (button == MouseEvent.BUTTON2) {
                    return BehaviorEvent.MIDDLE_MOUSE_KEY;
                } else if (button == MouseEvent.BUTTON3) {
                    return BehaviorEvent.RIGHT_MOUSE_KEY;
                }
            }
            if (BehaviorEvent.isMouseWheelEvent(id)) {
                int scroll = ((MouseWheelEvent) event).getWheelRotation();
                if (scroll < 0) {
                    return BehaviorEvent.SCROLLWHEEL_UP_KEY;
                } else if (scroll > 0) {
                    return BehaviorEvent.SCROLLWHEEL_DOWN_KEY;
                }
            }
            return BehaviorEvent.NO_KEY;
        }
    }

    // Get modifiers: applicable to all input events
    private int getModifiers(InputEvent event) {
        int modifiers = BehaviorEvent.NO_MODIFIER;
        modifiers |= event.isAltDown() ? BehaviorEvent.ALT_MODIFIER : 0;
        modifiers |= event.isControlDown() ? BehaviorEvent.CONTROL_MODIFIER : 0;
        modifiers |= event.isShiftDown() ? BehaviorEvent.SHIFT_MODIFIER : 0;
        modifiers |= event.isMetaDown() ? BehaviorEvent.COMMAND_MODIFIER : 0;
        return modifiers;
    }

    // Key listener
    private class WindowKeyListener extends KeyAdapter {
        private Point getCursor() {
            Point cursor = getMousePosition();
            return (cursor != null) ? cursor : new Point(0, 0);
        }

        public void keyPressed(KeyEvent event) {
            Point cursor = getCursor();
            handleBehaviorEvent(new BehaviorEvent(getModifiers(event), event.getKeyCode(), BehaviorEvent.KEY_DOWN_ID,
                    cursor.x - insets.left, cursor.y - insets.top));
        }

        public void keyReleased(KeyEvent event) {
            Point cursor = getCursor();
            handleBehaviorEvent(new BehaviorEvent(getModifiers(event), event.getKeyCode(), BehaviorEvent.KEY_UP_ID,
                    cursor.x - insets.left, cursor.y - insets.top));
        }
    }

    private void makeBuffer(int width, int height) {
        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = buffer.createGraphics();
        graphics.setColor(canvas.getBackground());
        graphics.fillRect(0, 0, width, height);
        graphics.dispose();
    }

    public InteractiveWindowGroup() {
        this("Interactive Window", 400, 400);
    }

    /**
     * Redraw: automatically called after adding children
     */
    public void redraw() {
        Graphics2D graphics = buffer.createGraphics();
        BoundaryRectangle r = new BoundaryRectangle(0, 0, getWidth(), getHeight());
        graphics.setColor(canvas.getBackground());
        graphics.fill(r);
        for (GraphicalObject child : children) {
            child.draw(graphics, r);
        }
        graphics.dispose();
        canvas.repaint();
    }

    public BufferedImage getBufferedImage() {
        return buffer;
    }

    /**
     * Methods defined in the Group and GraphicalObject interfaces
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
            this.redraw();
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

    public List<GraphicalObject> getChildren() {
        return new ArrayList<GraphicalObject>(children);
    }

    public InteractiveWindowGroup addBehavior(Behavior behavior) {
        behaviors.add(behavior);
        behaviorsSorted = false;
        return this;
    }

    public InteractiveWindowGroup addBehaviors(Behavior... behaviors) {
        for (Behavior behavior : behaviors) {
            addBehavior(behavior);
        }
        return this;
    }

    public InteractiveWindowGroup removeBehavior(Behavior behavior) {
        behaviors.remove(behavior);
        return this;
    }

    public InteractiveWindowGroup removeBehaviors(Behavior... behaviors) {
        for (Behavior behavior : behaviors) {
            removeBehavior(behavior);
        }
        return this;
    }

    public List<Behavior> getBehaviors() {
        return new ArrayList<Behavior>(behaviors);
    }

    /* The following are useless methods */
    public Behavior[] getBehaviorsToAdd() {
        return null;
    }

    public Behavior[] getBehaviorsToRemove() {
        return null;
    }

    public Group clearBehaviorsToAdd() {
        return null;
    }

    public Group clearBehaviorsToRemove() {
        return null;
    }

    public Group bringChildToFront(GraphicalObject child) {
        return this;
    }

    public Group resizeToChildren() {
        return this;
    }

    public Point parentToChild(Point pt) {
        return pt;
    }

    public Point childToParent(Point pt) {
        return pt;
    }

    public void draw(Graphics2D graphics, Shape clipRect) {
        redraw();
    }

    public BoundaryRectangle getBoundingBox() {
        return new BoundaryRectangle(canvas.getBounds());
    }

    public void moveTo(int x, int y) {
    }

    public Group getGroup() {
        return null;
    }

    public void setGroup(Group group) {
    }

    public boolean contains(int x, int y) {
        return getBoundingBox().contains(x, y);
    }

    public void setX(Constraint<Integer> constraint) {
    }

    public void setY(Constraint<Integer> constraint) {
    }

    /**
     * @return the canvas
     */
    public JComponent getCanvas() {
        return canvas;
    }
}