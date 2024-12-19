package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Floating {

    private Texture floatableTexture;
    private TextureRegion floatableTextureRegion;
    private Body floatableBody;
    private World w;

    public Floating(World world)
    {
        w = world;
        floatableTexture = new Texture("environment/cosmonaut.png");
        floatableTextureRegion = new TextureRegion(floatableTexture);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; // Character is dynamic
        bodyDef.position.set(Gdx.graphics.getWidth()/2 - 10, Gdx.graphics.getHeight()/2+5); // Position in Box2D meters
        floatableBody = w.createBody(bodyDef);

        // Define the shape of the character
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f); // Half-width and half-height in meters

        // Attach the shape to the character body
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f; // Density affects mass
        fixtureDef.friction = 0.5f; // Friction with ground
        fixtureDef.restitution = 0.3f; // Slight bounce
        fixtureDef.filter.categoryBits = 0x0001; // Body's category
        fixtureDef.filter.maskBits = 0x0000; // Collides with nothing

        floatableBody.createFixture(fixtureDef);
        floatableBody.setGravityScale(0.0f);



        floatableBody.setLinearVelocity(1.1f, 0.3f);
        floatableBody.setAngularVelocity(0.2f);
    }

    public void render(SpriteBatch b, Camera c)
    {

        float textureWidth = 1;
        float textureHeight = 1;


        if (floatableBody.getPosition().x > c.position.x + c.viewportWidth/2)
            floatableBody.setTransform(floatableBody.getPosition().x - c.viewportWidth,
                                            floatableBody.getPosition().y, floatableBody.getAngle()  );


        if (floatableBody.getPosition().x < c.position.x - c.viewportWidth/2)
            floatableBody.setTransform(floatableBody.getPosition().x + c.viewportWidth,
                                            floatableBody.getPosition().y, floatableBody.getAngle()   );


        if(floatableBody.getPosition().y > c.position.y + c.viewportHeight/2)
            floatableBody.setTransform(floatableBody.getPosition().x,
                    floatableBody.getPosition().y - c.viewportHeight, floatableBody.getAngle()   );


       if (floatableBody.getPosition().y < c.position.y - c.viewportHeight/2)
            floatableBody.setTransform(floatableBody.getPosition().x,
                    floatableBody.getPosition().y + c.viewportHeight, floatableBody.getAngle()  );


        b.draw(floatableTextureRegion,
                floatableBody.getPosition().x - 1/2f,
                floatableBody.getPosition().y - 1/2f,
                textureWidth,
                textureHeight,
                textureWidth,
                textureHeight,
                textureWidth,
                textureHeight,
                floatableBody.getAngle()*10
        ); // Dr

        System.out.println("float angle: " + floatableBody.getPosition().x + " : " + floatableBody.getPosition().y);
    }

}
