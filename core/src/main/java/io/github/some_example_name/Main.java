package io.github.some_example_name;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    Texture[] framesL;  // Array to hold individual frames
    Texture[] framesR;  // Array to hold individual frames
    int currentFrameInd;  // Index of the current frame
    Texture currentFrameTexture;
    float frameTime;   // Time since the last frame update
    float frameDuration = 0.1f; // Duration of each frame (in seconds)
    private Texture platformTexture;


    // Box2D World and Bodies
    private World world;
    private Body platformBody;
    private Body characterBody;

    float x, y;        // Position of the character
    float speed = 200; // Movement speed
    boolean moving;    // Is the character moving?
    boolean leftMove;

    private Rectangle platform;
    private Rectangle characterRect;

    private Box2DDebugRenderer debugRenderer;



    private void createPlatform() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(5, 1); // Box2D coordinates (meters)

        platformBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4, 0.5f); // Half-width and half-height

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;

        platformBody.createFixture(fixtureDef);
        shape.dispose();
    }

    private void createCharacter() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(5, 3); // Start above the platform

        characterBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f); // Half-width and half-height

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f; // Bounciness

        characterBody.createFixture(fixtureDef);
        shape.dispose();
    }


    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG); // Enable debug logs
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Create the Box2D world with gravity
        world = new World(new Vector2(0, -10f), true);
        Box2DDebugRenderer b2dr = new Box2DDebugRenderer();
        b2dr.setDrawBodies(true); //This method set the body lines to invisible

        createPlatform();
        createCharacter();

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

        // Initialize the platform
        platform = new Rectangle();
        platform.x = 200; // X position
        platform.y = 100; // Y position
        platform.width = 4000;
        platform.height = 50;

        characterRect = new Rectangle();
        characterRect.x = 300; // X position
        characterRect.y = platform.y + platform.height + 100; // Start on top of the platform
        characterRect.width = 50*2;
        characterRect.height = 50*2;


        platformTexture = new Texture("libgdx.png");
    }

    @Override
    public void render() {
        world.step(1 / 60f, 6, 2);
        // Handle input and update position
        moving = false; // Reset moving state
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            characterRect.x += speed * Gdx.graphics.getDeltaTime();
            moving = true;
            leftMove = false;

            //characterBody.applyLinearImpulse(new Vector2(1f, 0), new Vector2(characterBody.getPosition().x, characterBody.getPosition().y), true);
            characterBody.applyLinearImpulse(new Vector2(0.2f, 0), characterBody.getWorldCenter(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            characterRect.x -= speed * Gdx.graphics.getDeltaTime();
            moving = true;
            leftMove = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            characterRect.y += speed * Gdx.graphics.getDeltaTime();
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            characterRect.y -= speed * Gdx.graphics.getDeltaTime();
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
            currentFrameTexture = framesL[currentFrameInd];
        }
        else
        {
            currentFrameTexture = framesR[currentFrameInd];
        }

        Gdx.gl.glClearColor(0.5f, 1, 1, 1);
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the current frame at the updated position
        //batch.setProjectionMatrix(camera.combined);
        batch.begin();
        //batch.draw(currentFrameTexture, characterRect.x, characterRect.y, characterRect.width, characterRect.height);
        batch.draw(platformTexture, platform.x, platform.y, platform.width, platform.height);

        batch.draw(currentFrameTexture, characterBody.getPosition().x - 0.5f, characterBody.getPosition().y - 0.5f, 100, 100); // Scale to match Box2D size

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
