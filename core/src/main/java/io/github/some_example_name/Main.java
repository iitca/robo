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
    private Body characterBody;
    private Body platformBody;
    private OrthographicCamera camera;
    private Box2DDebugRenderer debugRenderer;
    SpriteBatch spriteBatch;

    private Texture characterTexture;
    private TextureRegion characterTextureRegion;

    private boolean isOnGround = false;

    @Override
    public void create() {
        // Initialize Box2D world with gravity
        world = new World(new Vector2(0, -10f), true);

        // Set up the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800 / 32f, 480 / 32f); // Convert pixels to meters

        // Initialize Box2D debug renderer
        debugRenderer = new Box2DDebugRenderer();

        spriteBatch = new SpriteBatch();
        // Create the platform and character bodies
        createPlatform();
        createCharacter();



        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if (isSensor(contact))
                {
                    isOnGround = true;
                }
            }

            @Override
            public void endContact(Contact contact) {
                if (isSensor(contact))
                {
                    isOnGround = false;
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    private void createPlatform() {
        // Define the platform body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody; // Platform is static
        bodyDef.position.set(5, 1); // Position in Box2D meters

        // Create the body in the world
        platformBody = world.createBody(bodyDef);

        // Define the shape of the platform
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4, 0.5f); // Half-width and half-height in meters

        // Attach the shape to the platform body
        platformBody.createFixture(shape, 0f); // Density = 0 for static bodies

        // Dispose of the shape after use
        shape.dispose();
    }


    private void createCharacter() {
        // Define the character body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; // Character is dynamic
        bodyDef.position.set(5, 3); // Position in Box2D meters

        // Create the body in the world
        characterBody = world.createBody(bodyDef);

        // Define the shape of the character
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f); // Half-width and half-height in meters

        // Attach the shape to the character body
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f; // Density affects mass
        fixtureDef.friction = 0.5f; // Friction with ground
        fixtureDef.restitution = 0.1f; // Slight bounce
        characterBody.createFixture(fixtureDef);

        // Load character texture
        characterTexture = new Texture("robi/move_left/sketch_L_0.png"); // Ensure this is in the assets folder
        characterTextureRegion = new TextureRegion(characterTexture);


        PolygonShape sensorShape = new PolygonShape();
        sensorShape.setAsBox(0.4f, 0.1f, new Vector2(0, -0.5f), 0);
        FixtureDef sensorFixtureDef = new FixtureDef();
        sensorFixtureDef.shape = sensorShape;
        sensorFixtureDef.isSensor = true;

        characterBody.createFixture(sensorFixtureDef);

        // Dispose of the shape after use
        sensorShape.dispose();
        shape.dispose();
    }

    private boolean isSensor(Contact contact)
    {
        Fixture A = contact.getFixtureA();
        Fixture B = contact.getFixtureB();
        return A.isSensor() || B.isSensor();
    }

    private void characterMotion()
    {
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            characterBody.applyLinearImpulse(new Vector2(0.2f, 0), characterBody.getWorldCenter(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            characterBody.applyLinearImpulse(new Vector2(-0.2f, 0), characterBody.getWorldCenter(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            //characterBody.applyLinearImpulse(new Vector2(0, 1), characterBody.getWorldCenter(), true);
            characterBody.setLinearVelocity(new Vector2(0, 10));
            characterBody.setLinearDamping(10);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            characterBody.applyLinearImpulse(new Vector2(0, -1), characterBody.getWorldCenter(), true);
        }

        if (characterBody.getLinearVelocity().y == 0)
        {
            characterBody.setLinearDamping(0);
            System.out.println("zero!!!");
        }
    }

    @Override
    public void render() {
        // Step the physics world
        world.step(1 / 60f, 6, 2);

        // Clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        characterMotion();

        // Get the body's position and angle
        Vector2 position = characterBody.getPosition();
        float angle = characterBody.getAngle(); // In radians

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        // Draw the texture at the character's position
        float textureWidth = 1.0f; // Width in meters
        float textureHeight = 1.0f; // Height in meters
        spriteBatch.draw(characterTextureRegion,
                position.x - textureWidth / 2, position.y - textureHeight / 2, // Bottom-left corner
                textureWidth / 2, textureHeight / 2, // Origin for rotation (center of the texture)
                textureWidth, textureHeight, // Dimensions
                1, 1, // Scale
                angle * 57.2958f // Convert radians to degrees for rotation
        );

        spriteBatch.end();


        // Render the debug view
        debugRenderer.render(world, camera.combined);

        // Update the camera
        camera.update();

        System.out.println("Is on ground: " + isOnGround);

        //System.out.println("x: " + characterBody.getLinearVelocity().x + "; y: " + characterBody.getLinearVelocity().y);
    }

    @Override
    public void dispose() {
        // Dispose of all resources
        world.dispose();
        debugRenderer.dispose();
    }
}