package ui.talk;

import static com.example.dialogflow.DetectIntentAudio.detectIntentAudio;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.speech.microphone.MicrophoneAnalyzer;
import com.speech.TextToSpeech;

import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import ui.toolkit.behavior.Behavior;
import ui.toolkit.behavior.BehaviorEvent;
import ui.toolkit.behavior.ChoiceBehavior;
import ui.toolkit.behavior.InteractiveWindowGroup;
import ui.toolkit.graphics.group.Group;
import ui.toolkit.graphics.group.LayoutGroup;
import ui.toolkit.graphics.group.SimpleGroup;
import ui.toolkit.graphics.object.BoundaryRectangle;
import ui.toolkit.graphics.object.GraphicalObject;
import ui.toolkit.graphics.object.Line;
import ui.toolkit.graphics.object.Text;
import ui.toolkit.graphics.object.selectable.SelectableFilledRect;
import ui.toolkit.graphics.object.selectable.SelectableGraphicalObject;
import ui.toolkit.widget.*;
import ui.toolkit.widget.Button;

public class TalkUI extends InteractiveWindowGroup {
    private static final long serialVersionUID = 1L;

    private static final int BORDER_GAP = 10;
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int SEPARATION_LEFT = 200;
    private static final int SEPARATION_RIGHT = WINDOW_WIDTH - SEPARATION_LEFT;
    private static final int CONTROL_PLANE_WIDTH = SEPARATION_LEFT - BORDER_GAP * 2;;
    private static final int CONTROL_PLANE_HEIGHT = WINDOW_HEIGHT - BORDER_GAP * 2;
    private static final int VOICE_PLANE_HEIGHT = (CONTROL_PLANE_HEIGHT) / 2 - BORDER_GAP;
    private static final int PROPERTY_PLANE_HEIGHT = CONTROL_PLANE_HEIGHT - VOICE_PLANE_HEIGHT - BORDER_GAP;

    private Group controlPlane, drawingPanel, voiceControlPlane;
    private MicrophoneAnalyzer mic;
    private TextToSpeech tts;
    private ResponseHandler handler;

    private String sessionId;
    private String projectId;

    Integer placeX = null;
    Integer placeY = null;

    public PropertySheet propertySheet;

    public static TalkUI Instance;
    public boolean needsSelection;
    public GraphicalObject interactionTarget;
    public InteractionOutcome interactionOutcome;
    public Map<GraphicalObject, InteractionOutcome> outcomes = new HashMap<>();

    public static void main(String[] args) {
        // parse command line arguments
        String sessionId = null;
        String projectId = null;
        String command = args[0];
        if (command.equals("--sessionId")) {
            sessionId = args[1];
        }
        command = args[2];
        if (command.equals("--projectId")) {
            projectId = args[3];
        }

        // start Talk UI interface
        new TalkUI(sessionId, projectId);
    }

    public TalkUI(String sessionId, String projectId) {
        super("TalkUI Editor", WINDOW_WIDTH, WINDOW_HEIGHT);

        Instance = this;

        this.sessionId = sessionId;
        this.projectId = projectId;

        makeGUI();
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                placeX = event.getX();
                placeY = event.getY();
            }
        });

        this.mic = new MicrophoneAnalyzer();
        this.tts = new TextToSpeech();
        this.handler = new ResponseHandler();
        listenForVoiceInput(mic, tts);
    }

    private void listenForVoiceInput(MicrophoneAnalyzer mic, TextToSpeech tts) {
        while (true) {
            mic.open();
            System.out.println("Waiting for voice input...");

            final int THRESHOLD = 30;
            int volume = mic.getAudioVolume();
            boolean isSpeaking = (volume > THRESHOLD);
            // System.out.println("\tCurrent audio volumes: " + volume);

            int audioLength = 0; // in seconds
            if (isSpeaking) {
                try {
                    System.out.println("RECORDING...");
                    mic.captureAudio();
                    while (mic.getAudioVolume() > THRESHOLD) {
                        //System.out.println("\tCurrent audio volume: " + mic.getAudioVolume());
                        Thread.sleep(2000);
                        audioLength += 2;
                    }
                    System.out.println("Recording Complete!");
                    // System.out.println("Looping back");
                    if (audioLength > -1) { // TODO: to avoid abrupt noise or not?
                        makeResponse(mic, tts);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error Occurred");
                } finally {
                    mic.close();
                }
            }
        }
    }

    /**
     * Start event listeners and send the behavior events to the factory object
     * 
     * returns the target object and the trigger events (start and stop, default is
     * mouse down and up)
     */
    private Pair<SelectableGraphicalObject, Pair<BehaviorEvent, BehaviorEvent>> listenForBehaviorInput() {
        // use an array to get around assignment in the enclosing scope
        SelectableGraphicalObject[] target = new SelectableGraphicalObject[1];
        target[0] = null;
        BehaviorEvent startEvent = BehaviorEvent.DEFAULT_START_EVENT;
        BehaviorEvent stopEvent = BehaviorEvent.DEFAULT_STOP_EVENT;

        // create a temporary choice behavior
        ChoiceBehavior cBehavior = new ChoiceBehavior(ChoiceBehavior.SINGLE, false) {
            @Override
            public boolean stop(BehaviorEvent event) {
                boolean eventConsumed = super.stop(event);
                // get the selected graphical object
                try {
                    target[0] = getSelection().get(0);
                    System.out.println("found target: " + target[0]);
                    // unregister the behavior from the drawing canvas
                    // drawingPanel.removeBehavior(this);
                } catch (Exception e) {
                    target[0] = null;
                    System.out.println("no target found");
                }

                return eventConsumed;
            }
        };

        // add a choice behavior to the drawing canvas to locate the target object
        drawingPanel.addBehavior(cBehavior);

        return Pair.of(target[0], Pair.of(startEvent, stopEvent));
    }

    private void makeResponse(MicrophoneAnalyzer mic, TextToSpeech tts) {
        String audioFilePath = mic.getAudioFilePath();
        String languageCode = "en-US";
        QueryResult queryResult = null;
        try {
            queryResult = detectIntentAudio(projectId, audioFilePath, sessionId, languageCode,
                    mic.getAudioFormat().getSampleRate());

            // TODO: should start listening for behavior events if in the interaction's
            // follow up?
            // Pair<SelectableGraphicalObject, Pair<BehaviorEvent, BehaviorEvent>>
            // targetAndEvents = listenForBehaviorInput();
            // SelectableGraphicalObject target = targetAndEvents.getLeft();
            // BehaviorEvent startEvent = targetAndEvents.getRight().getLeft();
            // BehaviorEvent stopEvent = targetAndEvents.getRight().getRight();

        } catch (Exception e) {
            System.err.println("Query failed: " + e);
        }

        if (queryResult != null) {
            GraphicalObject object = handler.handle(queryResult, drawingPanel);

            Text detectedText = new Text(queryResult.getQueryText());
            Text responseText = new Text(queryResult.getFulfillmentText());
            detectedText.setColor(Color.BLUE);
            responseText.setColor(new Color(192, 0, 255)); // purple?
            voiceControlPlane.addChildToTop(detectedText);
            voiceControlPlane.addChildToTop(responseText);

            placeX = placeY = null;

            if (object != null) {
                drawingPanel.addChild(object);
                followCursor(object);
            }
            redraw();

            tts.speak(responseText.getText());
            System.out.println(placeX + " " + placeY);

            if (object != null) {
                while (placeX == null && placeY == null) {
                    followCursor(object);
                }
            }

            // handle behavior
            // needs specification
            if (needsSelection) {

                System.out.println("Needs selection to continue...");
                while (ChoiceBehavior.lastSelectedGlobalObject == null) {

                    // take the specified interaction outcome
                    // set it to the global map of object to outcome
                    System.out.println("Waiting for selection...");

                }
                System.out.println();
                System.out.println("Selection made: " + ChoiceBehavior.lastSelectedGlobalObject);

                List<Behavior> behaviors = ChoiceBehavior.lastSelectedGlobalObject.getGroup().getBehaviors();
                Widget root = null;
                for (Behavior b: behaviors) {
                    if (b instanceof ChoiceBehavior) {
                        root = ((ChoiceBehavior) b).getRoot();
                    }
                }

                // reset the parent group's callback to trigger the outcome lookup
                if (root != null) {
                    root.setCallback(v -> {
                        System.out.println(v + " was selected, looking for outcome.");

                        InteractionOutcome outcome = outcomes.get(v);
                        if (outcome != null) {
                            outcome.apply();
                        }
                    });
                }

                // add the interaction outcome to the lookup list
                if (interactionTarget == null) {
                    interactionTarget = ChoiceBehavior.lastSelectedGlobalObject;
                }
                interactionOutcome.target = interactionTarget;

                outcomes.put(ChoiceBehavior.lastSelectedGlobalObject, interactionOutcome);

                needsSelection = false;
                interactionTarget = null;
                interactionOutcome = null;
            }

            placeX = placeY = null;
        } else {
            System.out.println("Didn't hear anything or get a response.");
        }
    }

    private void followCursor(GraphicalObject object) {
        Point cursor = getMousePosition();

        if (cursor != null) {
            cursor = drawingPanel.parentToChild(cursor);
            object.moveTo((int) cursor.getX(), (int) cursor.getY());
        }

    }

    private void makeGUI() {
        // create example widget
        RadioButtonPanel radioPanel = new RadioButtonPanel(600, 600)
                .addChildren(new RadioButton(new Line(0, 10, 40, 10, Color.BLACK, 3)),
                        new RadioButton(new Line(0, 10, 40, 10, Color.BLUE, 3)),
                        new RadioButton(new Line(0, 10, 40, 10, Color.MAGENTA, 3)),
                        new RadioButton(new Line(0, 10, 40, 10, Color.CYAN, 3)));

        // SelectableFilledRect sFilledRect = new SelectableFilledRect(200, 200, 40, 40, Color.PINK);

        // setup groups and separation line
        Line separationLine = new Line(SEPARATION_LEFT, 0, SEPARATION_LEFT, WINDOW_HEIGHT, Color.BLACK, 2);

        LayoutGroup voiceControlPrePlane = new LayoutGroup(0, 0, CONTROL_PLANE_WIDTH, 50, LayoutGroup.VERTICAL, 20);
        voiceControlPlane = new LayoutGroup(0, 70, CONTROL_PLANE_WIDTH, VOICE_PLANE_HEIGHT, LayoutGroup.VERTICAL, 20);
        // add plane texts
        Text voicePlaneText = new Text("Voice Control Plane");
        Widget<?> exportButton = new ButtonPanel(0, 0, false, ButtonPanel.MULTIPLE).addChild(new Button("Save"))
                .setCallback(o -> {
                    try {
                        if (drawingPanel == null)
                            return;
                        BoundaryRectangle drawingBox = drawingPanel.getBoundingBox();
                        // retrieve image
                        BufferedImage bi = getBufferedImage().getSubimage(drawingBox.x, drawingBox.y, drawingBox.width,
                                drawingBox.height);

                        File outputFile = new File("saved.png");
                        ImageIO.write(bi, "png", outputFile);
                        System.out.println("Canvas saved!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        voiceControlPrePlane.addChildren(voicePlaneText, exportButton);

        // the (x, y) does not matter since the control plane is a LayoutGroup
        // TODO: should use radioPanel.getValue() to get active value, but somehow the
        // active value after init is null, though in the UI first radio button selected
        propertySheet = new PropertySheet(radioPanel.getChildren().get(0), this);
//        radioPanel.setCallback(o -> {
//            for (GraphicalObject child : radioPanel.getChildren()) {
//                if (((RadioButton) child).isSelected()) {
//                    System.out.println("update selection...");
//                    propertySheet.updatePropertySheet(child);
//                }
//            }
//        });

        JComponent propertyControlPlane = new JScrollPane(propertySheet);
        propertyControlPlane.setBounds(BORDER_GAP, (CONTROL_PLANE_HEIGHT) / 2 + BORDER_GAP, CONTROL_PLANE_WIDTH,
                PROPERTY_PLANE_HEIGHT);
        getCanvas().add(propertyControlPlane);

        // set the offset to BORDER_GAP
        controlPlane = new LayoutGroup(BORDER_GAP, BORDER_GAP, CONTROL_PLANE_WIDTH, CONTROL_PLANE_HEIGHT,
                LayoutGroup.VERTICAL, BORDER_GAP).addChildren(voiceControlPrePlane, voiceControlPlane);

        drawingPanel = new SimpleGroup(SEPARATION_LEFT, 0, SEPARATION_RIGHT, WINDOW_HEIGHT);

        drawingPanel.addChildren(radioPanel);

        addChildren(controlPlane, separationLine, drawingPanel);

        radioPanel.setSelection("one");
    }
}