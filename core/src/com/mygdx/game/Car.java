package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

class Car extends GameObject {
    Car(final int x, final int y, final int z, final GameColor color) {
        super(x, y, z);

        Color color_;
        switch (color) {
            case RED:
                color_ = Color.RED;
                break;
            case GREEN:
                color_ = Color.GREEN;
                break;
            case YELLOW:
                color_ = Color.YELLOW;
                break;
            case BLUE:
            default:
                color_ = Color.BLUE;
        }

        model = new ModelBuilder().createBox(1, 1, 1,
                new Material(ColorAttribute.createDiffuse(color_)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelInstance = new ModelInstance(model);
        modelInstance.transform.translate(new Vector3(x, z, y).add(Field.getOffset()));
    }
}
