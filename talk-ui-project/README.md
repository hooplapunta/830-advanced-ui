# Final Project: Talk UI

## Update Log

Note that the projectID in pom.xml has been changed! Get the correct service account credentials to work.

To test dialogflow:

1. Download API key
2. Set `export GOOGLE_APPLICATION_CREDENTIALS=/path/to/your-project-credentials.json`
3. Run `mvn exec:java`

The audio will be recorded and saved to `resources/recording.wav`, which is then uploaded to detect intent, and the query result returned will then be used to render graphics

Caveat: if you are using VSCode, you might not be prompted to grant mic permission and the recording may not work

## General Information

Current code organization in src/main/java:
```
ui
|__  talk (Talk UI main interface)
|___ toolkit
|       |___ behavior
|       |___ constraint
|       |___ graphics
|       |___ widget
|___ editor (my editor implemented out of the toolkit)
```

Command `mvn exec:java` will start running the main class.

READMEs for the package:

- Constraint: see hw3
- Behaviors: see hw4
- Widgets: see hw5

Implementations are subject to change if more features are needed.

Change log of basic toolkit:
- Implementation of `behavior.InteractiveWindowGroup` changed completely to solve previous bugs where children's behaviors cannot propagate up to top group