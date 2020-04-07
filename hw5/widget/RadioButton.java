package widget;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import constraint.Constraint;
import constraint.SetupConstraint;
import graphics.object.BoundaryRectangle;
import graphics.object.FilledEllipse;
import graphics.object.GraphicalObject;
import graphics.object.Text;
import graphics.object.selectable.SelectableEllipse;

public class RadioButton extends SelectableEllipse {
    private FilledEllipse indicator;
    private GraphicalObject label;

    public RadioButton(GraphicalObject label) {
        super();
        this.label = label;

        int buttonSize = 14;
        int indicatorSize = 10;
        this.setWidth(buttonSize);
        this.setHeight(buttonSize);
        indicator = new FilledEllipse(0, 0, indicatorSize, indicatorSize, Color.WHITE);
        setupAlignment(this, indicator, label);
    }

    public RadioButton(String label) {
        this(new Text(label));
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

        if (!labelType.equals("Text")) {
            label.setY(new Constraint<Integer>(option.useY()) {
                public Integer getValue() {
                    return option.getY();
                }
            });
        } else {
            Text textLabel = (Text) label;
            textLabel.setY(new Constraint<Integer>(option.useY()) {
                public Integer getValue() {
                    return option.getY() + textLabel.getAscent();
                }
            });
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
        return new BoundaryRectangle(
            getX(), getY(),
            getWidth() * 2 + r.getWidth(),
            Math.max(getHeight(), r.getHeight())
        );
    }
}