package com.programmers.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.programmers.ui_elements.MyButton;

public abstract class ReturnableScreen extends Stage implements Screen, InputProcessor {

    final ScreenLoader screenLoader;
    private final Screen previousScreen;
    private final OrthographicCamera camera;

    ReturnableScreen(final ScreenLoader screenLoader, final Screen previousScreen) {
        this.screenLoader = screenLoader;
        this.previousScreen = previousScreen;

        Texture texture = screenLoader.getAssetManager().get("Sprites/Background/back.jpg");
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        TextureRegion textureRegion = new TextureRegion(texture);
        textureRegion.setRegion(865, 250, texture.getWidth() * 2, texture.getHeight() * 2);

        Image back = new Image();
        back.setDrawable(new TextureRegionDrawable(textureRegion));
        back.setSize(texture.getWidth() * 2, texture.getHeight() * 2);
        addActor(back);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setViewport(new FillViewport(1280, 720, camera));

        TextButton returnButton =
                new MyButton("   BACK   ", ScreenLoader.getGameSkin()) {
            @Override
            public void call() {
                returnToPreviousScreen();
            }
        };
        returnButton.getLabel().setFontScale(1.25f);

        addActor(returnButton);
        returnButton.setPosition(1270, 710, Align.topRight);
    }

    private void returnToPreviousScreen() {
        dispose();
        if (!(previousScreen instanceof ReturnableScreen)) {
            ScreenLoader.getMusicManager().getMenuTheme().pause();

            ScreenLoader.getMusicManager().getMainTheme().stop();
            ScreenLoader.getMusicManager().getMainTheme().play();
        }
        screenLoader.setScreen(previousScreen);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        act(Gdx.graphics.getDeltaTime());
        getBatch().setProjectionMatrix(camera.combined);
        draw();
    }

    @Override
    public void resize(int width, int height) {
        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public boolean keyDown(int keyCode) {
        if (keyCode == Input.Keys.BACK) {
            returnToPreviousScreen();
            return true;
        }
        return false;
    }
}
