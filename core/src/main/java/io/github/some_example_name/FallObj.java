package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
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

public class FallObj {


    private Body boxBody;
    private int x, y;

    private World w;

    public FallObj(World world, int xInst, int yInst)
    {
        w = world;
        x = xInst;
        y = yInst;
    }

    public void create()
    {
        // Define the character body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; // Character is dynamic
        bodyDef.position.set(x, y); // Position in Box2D meters

        // Create the body in the world
        boxBody = w.createBody(bodyDef);

        // Define the shape of the character
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.2f, 0.2f); // Half-width and half-height in meters

        // Attach the shape to the character body
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f; // Density affects mass
        fixtureDef.friction = 0.5f; // Friction with ground
        fixtureDef.restitution = 0.3f; // Slight bounce
        boxBody.createFixture(fixtureDef);

        // Dispose of the shape after use
        shape.dispose();
    }

    public Vector2 getPosition()
    {
        return boxBody.getPosition();
    }




}
