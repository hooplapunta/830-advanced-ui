package ui.toolkit.widget;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import ui.toolkit.constraint.Constraint;
import ui.toolkit.constraint.SetupConstraint;
import ui.toolkit.graphics.object.BoundaryRectangle;
import ui.toolkit.graphics.object.FilledEllipse;
import ui.toolkit.graphics.object.GraphicalObject;
import ui.toolkit.graphics.object.Line;
import ui.toolkit.graphics.object.Text;
import ui.toolkit.graphics.object.selectable.SelectableEllipse;

public class RadioButton extends SelectableEllipse {
    private FilledEllipse indicator;
    private GraphicalObject label;

    public RadioButton(int x, int y, GraphicalObject label) {
        super(x, y, 0, 0, Color.BLACK, 1);
        this.label = label;

        int buttonSize = 14;
        int indicatorSize = 10;
        this.setWidth(buttonSize);
        this.setHeight(buttonSize);
        indicator = new FilledEllipse(0, 0, indicatorSize, indicatorSize, Color.WHITE);
        setupAlignment(this, indicator, label);
    }

    public RadioButton(GraphicalObject label) {
        this(0, 0, label);
    }

    public RadioButton(String label) {
        this(new Text(label));
    }

    public RadioButton() {
        this("Label");
    }

    private void setupAlignment(SelectableEllipse option, FilledEllipse indicator, GraphicalObject label) {
        indicator.setX(new Constraint<Integer>(option.useX()) {
            public Integer getValue() {
                return option.getX() + (option.getWidth() - indicator.getWidth()) / 2;
            }
        });

        indicator.setY(new Constraint<Integer>(option.useY()) {
            public Integer getValue() {
                return option.getY() + (option.getHeight() - indicator.getHeight()) / 2;
            }
        });

        label.setX(new Constraint<Integer>(option.useX()) {
            public Integer getValue() {
                return option.getX() + option.getWidth() * 2;
            }
        });

        String labelType = label.getClass().getSimpleName();

        switch (labelType) {
            case "Text": {
                Text textLabel = (Text) label;
                textLabel.setY(new Constraint<Integer>(option.useY()) {
                    public Integer getValue() {
                        return option.getY() + textLabel.getAscent();
                    }
                });
                break;
            }
            case "Line": {
                Line lineLabel = (Line) label;
                lineLabel.setInvariant(true);
                lineLabel.setX2(new Constraint<Integer>(lineLabel.useX1()) {
                    public Integer getValue() {
                        return lineLabel.getX1() + lineLabel.getDx();
                    }
                });
                lineLabel.setY2(new Constraint<Integer>(lineLabel.useY1()) {
                    public Integer getValue() {
                        return lineLabel.getY1() + lineLabel.getDy();
                    }
                });
                lineLabel.setY(new Constraint<Integer>(option.useY()) {
                    public Integer getValue() {
                        return option.getY() + lineLabel.getLineThickness();
                    }
                });
            }
            default: {
                label.setY(new Constraint<Integer>(option.useY()) {
                    public Integer getValue() {
                        return option.getY();
                    }
                });
            }
        }
    }

    public RadioButton setAlignment(SetupConstraint constraint, GraphicalObject... objects) {
        constraint.setup(objects);
        return this;
    }

    public SelectableEllipse getOption() {
        return this;
    }

    public FilledEllipse getIndicator() {
        return this.indicator;
    }

    public GraphicalObject getLabel() {
        return this.label;
    }

    @Override
    public void draw(Graphics2D graphics, Shape clipShape) {
        super.draw(graphics, clipShape);
        indicator.draw(graphics, clipShape);
        label.draw(graphics, clipShape);
    }

    @Override
    public BoundaryRectangle getBoundingBox() {
        BoundaryRectangle r = label.getBoundingBox();
        return new BoundaryRectangle(getX(), getY(), getWidth() * 2 + r.getWidth(),
                Math.max(getHeight(), r.getHeight()));
    }

    @Override
    public boolean contains(int x, int y) {
        return getBoundingBox().contains(x, y);
    }

    public Color getColor() {
        try {
            return ((Line) label).getColor();
        } catch (Exception e) {
            return null;
        }
    }

    public void setColor(Color color) {
        try {
            // Only capable to deal with Line
            ((Line) label).setColor(color);
        } catch (Exception e) {
        }
    }
}