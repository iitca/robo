package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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

enum Sweets {
    bread,
    cake,
    croissant
}

public class FallObj {


    private Body boxBody;
    private int x, y;

    private Texture texture;
    private TextureRegion textureRegion;

    private World w;

    String[] sweetsTexturePath = new String[]{"falling/bread.png", "falling/cake.png", "falling/croissant.png"};
    public FallObj(World world, int xInst, int yInst)
    {
        w = world;
        x = xInst;
        y = yInst;
    }

    public void create(int s)
    {
        // Define the character body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; // Character is dynamic
        bodyDef.position.set(x, y); // Position in Box2D meters

        // Create the body in the world
        boxBody = w.createBody(bodyDef);
        boxBody.setFixedRotation(true);

        // Define the shape of the character
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.3f, 0.3f); // Half-width and half-height in meters

        // Attach the shape to the character body
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f; // Density affects mass
        fixtureDef.friction = 0.5f; // Friction with ground
        fixtureDef.restitution = 0.3f; // Slight bounce
        boxBody.createFixture(fixtureDef);

        texture = new Texture(sweetsTexturePath[s]);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
        textureRegion = new TextureRegion(texture);

        // Dispose of the shape after use
        shape.dispose();
    }

    public Vector2 getPosition()
    {
        return boxBody.getPosition();
    }

    void render(SpriteBatch b)
    {
        float textureWidth = 1.0f; // Width in meters
        float textureHeight = 1.0f; // Height in meters

        b.draw(textureRegion, boxBody.getPosition().x - textureWidth / 2, boxBody.getPosition().y - textureHeight / 2, // Bottom-left corner
                textureWidth / 2, textureHeight / 2, // Origin for rotation (center of the texture)
                textureWidth, textureHeight, // Dimensions
                1, 1, // Scale
                0 * 57.2958f); // Convert radians to degrees for rotation);// Convert radians to degrees for rotation);
    }



}
