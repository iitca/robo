package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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
    private Texture characterTexture, characterTextureL, characterTextureR;
    private TextureRegion characterTextureRegion, characterTextureRegionL, characterTextureRegionR;

    private float jumpSpeed = 10.0f;

    private float jumpTime = 0f; // How long the jump lasts
    private final float maxJumpTime = 0.5f; // Maximum jump duration in seconds
    private World w;

    private float stateTime = 0.0f;


    private Animation<TextureRegion> thrustRAnimation;


    private Animation<TextureRegion> thrustLAnimation;



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
        characterTexture = new Texture("robi/robi_toaster_f.png"); // Ensure this is in the assets folder
        characterTextureRegion = new TextureRegion(characterTexture);

        characterTextureL = new Texture("robi/robi_toaster_l.png"); // Ensure this is in the assets folder
        characterTextureRegionL = new TextureRegion(characterTextureL);

        characterTextureR = new Texture("robi/robi_toaster_r.png"); // Ensure this is in the assets folder
        characterTextureRegionR = new TextureRegion(characterTextureR);


        // Load spritesheet
        Texture fireballSheet = new Texture("robi/fireball_r.png");

        // Split into frames
        TextureRegion[][] tmpFrames = TextureRegion.split(fireballSheet, 67, 8);
        TextureRegion[] thrustFrames = new TextureRegion[tmpFrames[0].length];
        System.arraycopy(tmpFrames[0], 0, thrustFrames, 0, tmpFrames[0].length);

        // Create thrust animation right
        thrustRAnimation = new Animation<>(0.05f, thrustFrames);
        thrustRAnimation.setPlayMode(Animation.PlayMode.LOOP);

        fireballSheet = new Texture("robi/fireball_l.png");
        tmpFrames = TextureRegion.split(fireballSheet, 67, 8);
        thrustFrames = new TextureRegion[tmpFrames[0].length];
        System.arraycopy(tmpFrames[0], 0, thrustFrames, 0, tmpFrames[0].length);

        // Create thrust animation left
        thrustLAnimation = new Animation<>(0.05f, thrustFrames);
        thrustLAnimation.setPlayMode(Animation.PlayMode.LOOP);


        shape.dispose();
    }

    public void move()
    {
        Vector2 velocity = characterBody.getLinearVelocity();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x = 5f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x = -5f;
        }

        // Check if space is held and jump time hasn't reached the limit
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (jumpTime < maxJumpTime) {
                // Apply upward force
                velocity.y = jumpSpeed;

                // Increment jump time
                jumpTime += Gdx.graphics.getDeltaTime();
            }
        }
        else
        {
            jumpTime = 0.0f;
        }

        characterBody.setLinearVelocity(velocity);
    }

    public void render(SpriteBatch b)
    {
        // Get the body's position and angle
        Vector2 position = characterBody.getPosition();
        float angle = characterBody.getAngle(); // In radians

        stateTime += Gdx.graphics.getDeltaTime(); // Update elapsed time

        // Get the current frame
        TextureRegion currentFrame = thrustLAnimation.getKeyFrame(stateTime);
        int ind = thrustLAnimation.getKeyFrameIndex(stateTime);

        // Draw the texture at the character's position
        float textureWidth = 1.0f; // Width in meters
        float textureHeight = 1.0f; // Height in meters

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
        {
            b.draw(characterTextureRegionL,
                    position.x - textureWidth / 2, position.y - textureHeight / 2, // Bottom-left corner
                    textureWidth / 2, textureHeight / 2, // Origin for rotation (center of the texture)
                    textureWidth, textureHeight, // Dimensions
                    1, 1, // Scale
                    angle * 57.2958f // Convert radians to degrees for rotation
            );
            b.draw(currentFrame, position.x + 0.2f, position.y - textureHeight * 0.35f,
                    textureWidth / 2, textureHeight / 2,
                    textureWidth * 2, textureHeight / 2,
                    0.4f, 0.4f,
                    (float) 0 * 57.2958f // Convert radians to degrees for rotation
            ); // Draw at position (100, 100)
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        {
            b.draw(characterTextureRegionR,
                    position.x - textureWidth / 2, position.y - textureHeight / 2, // Bottom-left corner
                    textureWidth / 2, textureHeight / 2, // Origin for rotation (center of the texture)
                    textureWidth, textureHeight, // Dimensions
                    1, 1, // Scale
                    angle * 57.2958f // Convert radians to degrees for rotation
            );
            b.draw(currentFrame, position.x - textureWidth - 0.2f, position.y - textureHeight * 0.5f,
                    textureWidth / 2, textureHeight / 2,
                    textureWidth * 2, textureHeight / 2,
                    0.4f, 0.4f,
                    (float) Math.PI * 57.2958f // Convert radians to degrees for rotation
            ); // Draw at position (100, 100)
        }
        else
        {
            b.draw(characterTextureRegion,
                    position.x - textureWidth / 2, position.y - textureHeight / 2, // Bottom-left corner
                    textureWidth / 2, textureHeight / 2, // Origin for rotation (center of the texture)
                    textureWidth, textureHeight, // Dimensions
                    1, 1, // Scale
                    angle * 57.2958f // Convert radians to degrees for rotation
            );
        }


        if (jumpTime > 0.0f && jumpTime < maxJumpTime) {
            b.draw(currentFrame, position.x - textureWidth / 4, position.y - textureHeight * 1.5f,
                    textureWidth / 2, textureHeight / 2,
                    textureWidth * 2, textureHeight / 2,
                    1, 1,
                    (float) Math.PI * 3 / 2 * 57.2958f // Convert radians to degrees for rotation
            ); // Draw at position (100, 100)
        }
    }

    public Vector2 getPosition()
    {
        return characterBody.getPosition();
    }
}
