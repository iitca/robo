package io.github.some_example_name;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    Texture[] framesL;  // Array to hold individual frames
    Texture[] framesR;  // Array to hold individual frames
    int currentFrameInd;  // Index of the current frame
    Texture currentFrame;
    float frameTime;   // Time since the last frame update
    float frameDuration = 0.1f; // Duration of each frame (in seconds)

    float x, y;        // Position of the character
    float speed = 200; // Movement speed
    boolean moving;    // Is the character moving?
    boolean leftMove;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG); // Enable debug logs
        batch = new SpriteBatch();

        // Load frames into the array
        int frameCount = 7; // Number of animation frames
        framesL = new Texture[frameCount];
        framesR = new Texture[frameCount];
        for (int i = 0; i < frameCount; i++) {
            framesL[i] = new Texture(Gdx.files.internal("robi/move_left/sketch_L_" + i + ".png"));
            framesR[i] = new Texture(Gdx.files.internal("robi/move_right/sketch_R_" + i + ".png"));  // Array to hold individual frames
        }

        // Initialize position and frame tracking
        x = -50;
        y = -250;
        currentFrameInd = 0;
        frameTime = 0;
        moving = false;
    }

    @Override
    public void render() {
        // Handle input and update position
        moving = false; // Reset moving state
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += speed * Gdx.graphics.getDeltaTime();
            moving = true;
            leftMove = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= speed * Gdx.graphics.getDeltaTime();
            moving = true;
            leftMove = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            y += speed * Gdx.graphics.getDeltaTime();
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            y -= speed * Gdx.graphics.getDeltaTime();
            moving = true;
        }

        // Update frame if moving
        if (moving) {

            frameTime += Gdx.graphics.getDeltaTime();
            if (frameTime > frameDuration) {
                frameTime = 0;
                if (currentFrameInd < framesL.length-1) {
                    currentFrameInd = currentFrameInd + 1; // Cycle through frames
                }
                Gdx.app.log("currentFrame", "X: " + currentFrameInd);
            }
        } else {
            currentFrameInd = 0; // Reset to the first frame when idle
        }

        if (leftMove)
        {
            currentFrame = framesL[currentFrameInd];
        }
        else
        {
            currentFrame = framesR[currentFrameInd];
        }

        Gdx.gl.glClearColor(1, 1, 1, 1);
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the current frame at the updated position
        batch.begin();
        batch.draw(currentFrame, x, y);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
