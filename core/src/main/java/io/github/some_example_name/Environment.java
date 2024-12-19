package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Environment {



    private ParticleEffect[] particles;
    private int num;

    public Environment(int particlesNum)
    {
        num = particlesNum;
    }
    public void create()
    {
        particles = new ParticleEffect[num];

        for (int i = 0; i < num; i++)
        {
            ParticleEffect e = new ParticleEffect();
            e.load(Gdx.files.internal("environment/fly.p"), Gdx.files.internal("environment/effects/"));
            e.setPosition(Gdx.graphics.getWidth() / 2f + MathUtils.random(-Gdx.graphics.getWidth() / 16f, Gdx.graphics.getWidth() / 16f),
                          Gdx.graphics.getHeight() / 2f + MathUtils.random(-Gdx.graphics.getWidth() / 16f, Gdx.graphics.getWidth() / 16f));
            e.scaleEffect( MathUtils.random(0.005f, 0.015f));
            e.start();
            particles[i] = e;
        }

    }


    public void render(SpriteBatch b)
    {
        float delta = Gdx.graphics.getDeltaTime();

        for (int i = 0; i < num; i++)
        {
            particles[i].update(delta);
            particles[i].draw(b);
        }
    }

}
