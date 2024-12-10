package io.github.some_example_name;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class FallObjCloud {


    int l;
    int r;
    private World w;
    private int objNumMax = 20;

    private Array<FallObj> myObjects;

    public FallObjCloud(World world, int left, int right)
    {
        r = right;
        l = left;
        w = world;

        myObjects = new Array<>();
    }

    public void spawn()
    {
        for (int i = 0; i < myObjects.size; i++)
        {
            if(myObjects.get(i).getPosition().y < 0)
            {
                myObjects.removeIndex(i);
            }
        }

        for (int i = 0; i < (objNumMax-myObjects.size); i++)
        {
            FallObj fO = new FallObj(w, MathUtils.random(l, r), MathUtils.random(3, 10));
            fO.create();

            myObjects.add(fO);
        }

    }



}
