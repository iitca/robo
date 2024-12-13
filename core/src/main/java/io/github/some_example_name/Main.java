package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Main extends ApplicationAdapter {
    private World world;
    private Body platformBody;
    private OrthographicCamera camera;
    private float followSpeed = 0.25f;
    private Box2DDebugRenderer debugRenderer;
    SpriteBatch spriteBatch;
    private Robi r;
    private FallObjCloud cloud;

    private Texture backgroundTexture;
    float zoomFactor = 0.08f;

    @Override
    public void create() {
        // Initialize Box2D world with gravity
        world = new World(new Vector2(0, -10f), true);

        r = new Robi(world);
        r.create();

        // Set up the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024 / 32.0f, 768 / 32.0f); // Convert pixels to meters
        camera.position.x = Gdx.graphics.getWidth()/2;
        camera.position.y = Gdx.graphics.getHeight()/2 + 5;

        // Initialize Box2D debug renderer
        debugRenderer = new Box2DDebugRenderer();

        spriteBatch = new SpriteBatch();
        // Create the platform and character bodies
        createPlatform();

        backgroundTexture = new Texture("background/space_background.png"); // Place this file in core/assets

        cloud = new FallObjCloud(world, Gdx.graphics.getWidth()/2 - 5, Gdx.graphics.getWidth()/2 + 5);
    }

    private void createPlatform() {
        // Define the platform body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody; // Platform is static
        bodyDef.position.set(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2); // Position in Box2D meters

        // Create the body in the world
        platformBody = world.createBody(bodyDef);

        // Define the shape of the platform
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8, 0.5f); // Half-width and half-height in meters

        // Attach the shape to the platform body
        platformBody.createFixture(shape, 0f); // Density = 0 for static bodies

        // Dispose of the shape after use
        shape.dispose();
    }

    private void updateCamera() {
        // Get the character's position
        Vector2 characterPosition = r.getPosition();

        int xCenter = Gdx.graphics.getWidth()/2;
        int yCenter = Gdx.graphics.getHeight()/2;

        // Smoothly move the camera towards the character
        camera.position.x += (characterPosition.x - camera.position.x) * followSpeed * Gdx.graphics.getDeltaTime();
        camera.position.y += (characterPosition.y - camera.position.y) * followSpeed * Gdx.graphics.getDeltaTime();

        // Optional: Clamp the camera to certain bounds
        int diffX = 500;
        int diffY = 400;
        camera.position.x = MathUtils.clamp(camera.position.x, xCenter-diffX, xCenter+diffX); // Adjust max bounds
        camera.position.y = MathUtils.clamp(camera.position.y, yCenter-diffY, yCenter+diffY);
        System.out.println("Camera position: (" + camera.position.x + ", " + camera.position.y + ")");
    }

    @Override
    public void render() {
        // Step the physics world
        world.step(1 / 60f, 6, 4);
        updateCamera();
        // Clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        r.move();
        cloud.spawn();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, Gdx.graphics.getWidth()/2 - 800*zoomFactor/2, Gdx.graphics.getHeight()/2 - 800*zoomFactor/2, 800*zoomFactor, 800*zoomFactor);
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