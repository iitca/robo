package io.github.some_example_name;

import static com.badlogic.gdx.math.MathUtils.floor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Main extends ApplicationAdapter {
    private World world;
    private Body platformBody;
  //  private OrthographicCamera camera;
    private float followSpeed = 0.25f;
    private Box2DDebugRenderer debugRenderer;
    private PerspectiveCamera camera;
    SpriteBatch spriteBatch;
    private Robi r;
    private FallObjCloud cloud;

    private Texture backgroundTexture;
    private Texture planetTexture;
    float zoomFactor = 0.08f;
    float cameraPosition = 0.0f;

    @Override
    public void create() {
        // Initialize Box2D world with gravity
        world = new World(new Vector2(0, -10f), true);

        r = new Robi(world);
        r.create();

        // Set up the camera
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth() / 32.0f, Gdx.graphics.getHeight() / 32.0f);
        // 67 is the field of view in degrees

        // Set the camera's position and direction
        camera.position.set(Gdx.graphics.getWidth() / 64.0f, Gdx.graphics.getHeight() / 64.0f + 5, 10f);
        // Add depth with the Z-coordinate
        camera.lookAt(0, 0, 0); // Look towards the center of the world

        // Configure near and far clipping planes
        camera.near = 1f;
        camera.far = 300f;

        // Update the camera
        camera.update();

        // Initialize Box2D debug renderer
        debugRenderer = new Box2DDebugRenderer();

        spriteBatch = new SpriteBatch();

        // Create the platform and character bodies
        createPlatform();

        backgroundTexture = new Texture("background/space_background.png"); // Place this file in core/assets
        planetTexture = new Texture("background/planet.png");

        cloud = new FallObjCloud(world, Gdx.graphics.getWidth() / 2 - 5, Gdx.graphics.getWidth() / 2 + 5);
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

        int xCenter = Gdx.graphics.getWidth() / 2;
        int yCenter = Gdx.graphics.getHeight() / 2;

        // Smoothly move the camera towards the character
        camera.position.x += (characterPosition.x - camera.position.x) * followSpeed * Gdx.graphics.getDeltaTime();
        camera.position.y += (characterPosition.y - camera.position.y) * followSpeed * Gdx.graphics.getDeltaTime();

        // Maintain a fixed Z-depth for perspective
        camera.position.z = 10f;

        // Optional: Clamp the camera to certain bounds
        int diffX = 500;
        int diffY = 400;
        camera.position.x = MathUtils.clamp(camera.position.x, xCenter - diffX, xCenter + diffX); // Adjust max bounds
        camera.position.y = MathUtils.clamp(camera.position.y, yCenter - diffY, yCenter + diffY);

        // Ensure the camera is looking at the center of the world or the character
        camera.lookAt(characterPosition.x, characterPosition.y, 0);

        // Update the camera
        camera.update();
    }

    @Override
    public void render() {
        // Step the physics world
        world.step(1 / 60f, 6, 4);
        updateCamera();


        //cameraPosition += 2 * Gdx.graphics.getDeltaTime() * 0.5;
        float dX = (r.getPosition().x - Gdx.graphics.getWidth()/2) / 16;
        float dY = (r.getPosition().y - Gdx.graphics.getHeight()/2) / 16;

        // Clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        r.move();
        cloud.spawn();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, Gdx.graphics.getWidth()/2 - 800*zoomFactor/2, Gdx.graphics.getHeight()/2 - 800*zoomFactor/4, 800*zoomFactor, 800*zoomFactor);
        //spriteBatch.draw(planetTexture, -cameraPosition + Gdx.graphics.getWidth()/2 - 400*zoomFactor/2, Gdx.graphics.getHeight()/2 - 400*zoomFactor/2, 400*zoomFactor, 400*zoomFactor);

        spriteBatch.draw(
                planetTexture,
                -dX + Gdx.graphics.getWidth() / 2 - 400*zoomFactor/2,
                -dY + Gdx.graphics.getHeight() / 2 - 400*zoomFactor/2,
                400*zoomFactor, 400*zoomFactor
        );
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