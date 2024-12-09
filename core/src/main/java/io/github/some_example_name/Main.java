package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Main extends ApplicationAdapter {
    private World world;
    private Body platformBody;
    private OrthographicCamera camera;
    private Box2DDebugRenderer debugRenderer;
    SpriteBatch spriteBatch;
    private Robi r;

    @Override
    public void create() {
        // Initialize Box2D world with gravity
        world = new World(new Vector2(0, -10f), true);

        r = new Robi(world);
        r.create();

        // Set up the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800 / 32f, 480 / 32f); // Convert pixels to meters

        // Initialize Box2D debug renderer
        debugRenderer = new Box2DDebugRenderer();

        spriteBatch = new SpriteBatch();
        // Create the platform and character bodies
        createPlatform();
    }

    private void createPlatform() {
        // Define the platform body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody; // Platform is static
        bodyDef.position.set(12, 1); // Position in Box2D meters

        // Create the body in the world
        platformBody = world.createBody(bodyDef);

        // Define the shape of the platform
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(24, 0.5f); // Half-width and half-height in meters

        // Attach the shape to the platform body
        platformBody.createFixture(shape, 0f); // Density = 0 for static bodies

        // Dispose of the shape after use
        shape.dispose();
    }

    @Override
    public void render() {
        // Step the physics world
        world.step(1 / 60f, 6, 2);

        // Clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        r.move();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        r.render(spriteBatch);

        spriteBatch.end();


        // Render the debug view
        debugRenderer.render(world, camera.combined);

        // Update the camera
        camera.update();
    }

    @Override
    public void dispose() {
        // Dispose of all resources
        world.dispose();
        debugRenderer.dispose();
    }
}