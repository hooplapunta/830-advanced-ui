package ui.talk;

import static com.example.dialogflow.DetectIntentAudio.detectIntentAudio;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.speech.microphone.MicrophoneAnalyzer;

import org.apache.commons.lang3.tuple.Pair;

import com.speech.TextToSpeech;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

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
import ui.toolkit.widget.Button;
import ui.toolkit.widget.ButtonPanel;
import ui.toolkit.widget.PropertySheet;
import ui.toolkit.widget.RadioButton;
import ui.toolkit.widget.RadioButtonPanel;
import ui.toolkit.widget.Widget;

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

    private String sessionId;
    private String projectId;

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
        this.sessionId = sessionId;
        this.projectId = projectId;

        // TODO: drawingPanel is already a class attribute, seems no need to recreate it
        Group drawingPanel = makeGUI();
        MicrophoneAnalyzer mic = new MicrophoneAnalyzer();
        TextToSpeech tts = new TextToSpeech();

        listenForVoiceInput(mic, drawingPanel, tts);
    }

    private void listenForVoiceInput(MicrophoneAnalyzer mic, Group panel, TextToSpeech tts) {
        while (true) {
            mic.open();
            final int THRESHOLD = 30;
            int volume = mic.getAudioVolume();
            boolean isSpeaking = (volume > THRESHOLD);
            System.out.println("\tCurrent audio volumes: " + volume);

            int audioLength = 0; // in seconds
            if (isSpeaking) {
                try {
                    System.out.println("RECORDING...");
                    mic.captureAudio();
                    while (mic.getAudioVolume() > THRESHOLD) {
                        System.out.println("\tCurrent audio volume: " + mic.getAudioVolume());
                        Thread.sleep(1000);
                        audioLength += 1;
                    }
                    System.out.println("Recording Complete!");
                    System.out.println("Looping back");
                    if (audioLength > 1) { // to avoid abrupt noise
                        makeResponse(mic, panel, tts);
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
                } catch (Exception e) {
                    target[0] = null;
                }

                return eventConsumed;
            }
        };

        // add a choice behavior to the drawing canvas to locate the target object
        drawingPanel.addBehavior(cBehavior);

        // wait till the target found or timeout happened
        int failureCount = 0;
        while (target[0] == null && failureCount < 6) {
            try {
                Thread.sleep(1000);
                failureCount += 1;
                System.out.println("no target found for: " + failureCount + " seconds...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // unregister the behavior from the drawing canvas
        drawingPanel.removeBehavior(cBehavior);

        if (target[0] != null) {
            System.out.println("found target: " + target[0]);
        } else if (failureCount >= 6) {
            System.out.println("timeout happened");
        }

        System.out.println("choice behavior unregistered.");

        return Pair.of(target[0], Pair.of(startEvent, stopEvent));
    }

    private void makeResponse(MicrophoneAnalyzer mic, Group panel, TextToSpeech tts) {
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

        HandleResponse.handle(queryResult, panel);
        redraw();

        tts.speak(queryResult.getFulfillmentText());
    }

    private Group makeGUI() {
        // create example widget
        RadioButtonPanel radioPanel = new RadioButtonPanel(50, 50)
                .addChildren(new RadioButton(new Line(0, 10, 40, 10, Color.BLACK, 3)),
                        new RadioButton(new Line(0, 10, 40, 10, Color.BLUE, 3)),
                        new RadioButton(new Line(0, 10, 40, 10, Color.MAGENTA, 3)),
                        new RadioButton(new Line(0, 10, 40, 10, Color.CYAN, 3)))
                .setSelection("one");

        SelectableFilledRect sFilledRect = new SelectableFilledRect(200, 200, 40, 40, Color.PINK);

        // setup groups and separation line
        Line separationLine = new Line(SEPARATION_LEFT, 0, SEPARATION_LEFT, WINDOW_HEIGHT, Color.BLACK, 2);

        voiceControlPlane = new LayoutGroup(0, 0, CONTROL_PLANE_WIDTH, VOICE_PLANE_HEIGHT, LayoutGroup.VERTICAL, 20);
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

        voiceControlPlane.addChildren(voicePlaneText, exportButton);

        // the (x, y) does not matter since the control plane is a LayoutGroup
        // TODO: should use radioPanel.getValue() to get active value, but somehow the
        // active value after init is null, though in the UI first radio button selected
        PropertySheet propertySheet = new PropertySheet(radioPanel.getChildren().get(0), this);
        radioPanel.setCallback(o -> {
            for (GraphicalObject child : radioPanel.getChildren()) {
                if (((RadioButton) child).isSelected()) {
                    System.out.println("update selection...");
                    propertySheet.updatePropertySheet(child);
                }
            }
        });

        JComponent propertyControlPlane = new JScrollPane(propertySheet);
        propertyControlPlane.setBounds(BORDER_GAP, (CONTROL_PLANE_HEIGHT) / 2 + BORDER_GAP, CONTROL_PLANE_WIDTH,
                PROPERTY_PLANE_HEIGHT);
        getCanvas().add(propertyControlPlane);

        // set the offset to BORDER_GAP
        controlPlane = new LayoutGroup(BORDER_GAP, BORDER_GAP, CONTROL_PLANE_WIDTH, CONTROL_PLANE_HEIGHT,
                LayoutGroup.VERTICAL, BORDER_GAP).addChildren(voiceControlPlane);

        drawingPanel = new SimpleGroup(SEPARATION_LEFT, 0, SEPARATION_RIGHT, WINDOW_HEIGHT);

        drawingPanel.addChildren(radioPanel, sFilledRect);

        addChildren(controlPlane, separationLine, drawingPanel);
        return drawingPanel;
    }
}