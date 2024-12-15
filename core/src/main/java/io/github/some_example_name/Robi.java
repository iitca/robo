package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Robi {

    private Body characterBody;
    private Texture characterTexture;
    private TextureRegion characterTextureRegion;
    private boolean isOnGround = false;

    private float jumpSpeed = 10.0f;

    private float jumpTime = 0f; // How long the jump lasts
    private final float maxJumpTime = 0.5f; // Maximum jump duration in seconds
    private World w;

    public Robi(World world)
    {
        w = world;
    }
    private boolean isSensor(Contact contact)
    {
        Fixture A = contact.getFixtureA();
        Fixture B = contact.getFixtureB();
        return A.isSensor() || B.isSensor();
    }
    public void create()
    {
        // Define the character body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; // Character is dynamic
        bodyDef.position.set(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2+5); // Position in Box2D meters

        // Create the body in the world
        characterBody = w.createBody(bodyDef);

        // Define the shape of the character
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f); // Half-width and half-height in meters

        // Attach the shape to the character body
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f; // Density affects mass
        fixtureDef.friction = 0.5f; // Friction with ground
        fixtureDef.restitution = 0.3f; // Slight bounce
        fixtureDef.filter.maskBits = 0xFF;
        fixtureDef.filter.categoryBits = 0x1;

        characterBody.createFixture(fixtureDef);
        characterBody.setFixedRotation(true);
        characterBody.setGravityScale(5.0f);

        // Load character texture
        characterTexture = new Texture("robi/move_left/sketch_L_0.png"); // Ensure this is in the assets folder
        characterTextureRegion = new TextureRegion(characterTexture);

        shape.dispose();
    }

    public void move()
    {
        Vector2 currentVelocity = characterBody.getLinearVelocity();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            characterBody.setLinearVelocity(new Vector2(5, currentVelocity.y));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            characterBody.setLinearVelocity(new Vector2(-5f, currentVelocity.y));
        }

        // Check if space is held and jump time hasn't reached the limit
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (jumpTime < maxJumpTime) {
                // Apply upward force
                characterBody.setLinearVelocity(new Vector2(currentVelocity.x, jumpSpeed));

                // Increment jump time
                jumpTime += Gdx.graphics.getDeltaTime();
            }
        }
        else
        {
            jumpTime = 0.0f;
        }



        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            characterBody.setLinearVelocity(new Vector2(currentVelocity.x, -5));
        }

        if ( !(Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
                Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                Gdx.input.isKeyPressed(Input.Keys.DOWN)) && isOnGround )
        {
            characterBody.setLinearVelocity(0,0 );
        }

        if (isOnGround)
        {
            //characterBody.setGravityScale(1.0f);
        }
    }

    public void render(SpriteBatch b)
    {
        // Get the body's position and angle
        Vector2 position = characterBody.getPosition();
        float angle = characterBody.getAngle(); // In radians

        // Draw the texture at the character's position
        float textureWidth = 1.0f; // Width in meters
        float textureHeight = 1.0f; // Height in meters
        b.draw(characterTextureRegion,
                position.x - textureWidth / 2, position.y - textureHeight / 2, // Bottom-left corner
                textureWidth / 2, textureHeight / 2, // Origin for rotation (center of the texture)
                textureWidth, textureHeight, // Dimensions
                1, 1, // Scale
                angle * 57.2958f // Convert radians to degrees for rotation
        );
    }

    public Vector2 getPosition()
    {
        return characterBody.getPosition();
    }



}
