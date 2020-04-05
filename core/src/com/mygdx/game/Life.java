package com.mygdx.game;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

class Life extends GameObject {

    final static float width = 0.9f, height = 0.2f;

    private Field field;

    private Model typeModel;
    private String typeModelName;

    Life(final int x, final int y, final int z, final Type type, final Field field) {
        super(x, y, z);
        this.field = field;
        field.chunks[getX()][getZ()].lives.add(this);
        StringBuilder stringBuilder = new StringBuilder("Models/LifeObjects/LifeObject");
        int number;
        switch (type) {
            case Yellow:
                number = 1;
                break;
            case Purple:
                number = 3;
                break;
            case Green:
                number = 2;
                break;
            case Blue:
            default:
                number = 4;
        }
        stringBuilder.append(number).append("/LifeObject").append(number).append(".obj");
        typeModelName = stringBuilder.toString();
    }

    @Override
    void loading() {
        ProgrammersGame.assetManager.load("Models/LifeObjects/LifeObject0/LifeObject0.obj", Model.class);
        ProgrammersGame.assetManager.load(typeModelName, Model.class);
    }

    @Override
    void doneLoading() {
        model = ProgrammersGame.assetManager.get("Models/LifeObjects/LifeObject0/LifeObject0.obj", Model.class);
        typeModel = ProgrammersGame.assetManager.get(typeModelName, Model.class);
        modelInstance = new ModelInstance(model);
        modelInstance.transform.setTranslation(new Vector3(
                getX() * Chunk.width + 0.002f,
                getY() * Chunk.height + 0.002f,
                getZ() * Chunk.width + 0.002f
        ).add(field.getOffset()));
        ProgrammersGame.instances.add(modelInstance);
    }

    @Override
    void setPosition(int x, int y, int z) {
        super.setPosition(x, y, z);
        if (modelInstance != null) {
            modelInstance.transform.setTranslation(new Vector3(
                    getX() * Chunk.width + 0.002f,
                    getY() * Chunk.height + 0.002f,
                    getZ() * Chunk.width + 0.002f
            ).add(field.getOffset()));
        }
    }

    enum Type {
        Yellow,
        Purple,
        Green,
        Blue
    }
}
