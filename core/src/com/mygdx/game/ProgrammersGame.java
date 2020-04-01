package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class ProgrammersGame extends ApplicationAdapter {

	static int size;

	private Field field;
	private Car[] cars;
	static Difficulty difficulty = Difficulty.Hard;

	static Array<ModelInstance> instances;
	static AssetManager assetManager;
	private static boolean loading;

	private static PerspectiveCamera camera;
	private static Environment environment;
	private static ModelBatch modelBatch;
	private static SpriteBatch spriteBatch;
	private static MyGestureDetector myGestureDetector;

	@Override
	public void create() {
		switch (difficulty) {
			case Hard:
				size = 9;
				break;
			case Easy:
				size = 6;
		}

		instances = new Array<>();
		assetManager = new AssetManager();

		modelBatch = new ModelBatch();
		spriteBatch = new SpriteBatch();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		camera = new PerspectiveCamera(67f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(size, size, size);
		camera.lookAt(0f,0f,0f);
		camera.near = 0.1f;
		camera.far = 100f;
		camera.update();

		myGestureDetector = new MyGestureDetector(camera);
		Gdx.input.setInputProcessor(new GestureDetector(myGestureDetector));

		field = new Field(size);
		cars = new Car[] {
				new Car(0, field.chunks[0][0].getY() + 1, 0, field.chunks[0][0].getBaseColor(), field),
				new Car(0, field.chunks[0][size - 1].getY() + 1, size - 1, field.chunks[0][size - 1].getBaseColor(), field),
				new Car(size - 1, field.chunks[size - 1][0].getY() + 1, 0, field.chunks[size - 1][0].getBaseColor(), field),
				new Car(size - 1, field.chunks[size - 1][size - 1].getY() + 1, size - 1,
						field.chunks[size - 1][size - 1].getBaseColor(), field)
		};

		loading();

		// X axis (red) is i, Y axis (green) is height, Z axis (blue) is j
		{
			Vector3 start = new Vector3(0f, 5f, 0f), end = new Vector3();

			ModelBuilder modelBuilder1 = new ModelBuilder();
			modelBuilder1.begin();
			MeshPartBuilder builder1 = modelBuilder1.part("Y", 1, 3, new Material());
			builder1.setColor(Color.GREEN);
			end.set(start).add(new Vector3(Vector3.Y).scl(3));
			builder1.line(start.x, start.y, start.z, end.x, end.y, end.z);
			instances.add(new ModelInstance(modelBuilder1.end()));

			ModelBuilder modelBuilder2 = new ModelBuilder();
			modelBuilder2.begin();
			MeshPartBuilder builder2 = modelBuilder2.part("X", 1, 3, new Material());
			builder2.setColor(Color.RED);
			end.set(start).add(new Vector3(Vector3.X).scl(3));
			builder2.line(start.x, start.y, start.z, end.x, end.y, end.z);
			instances.add(new ModelInstance(modelBuilder2.end()));

			ModelBuilder modelBuilder3 = new ModelBuilder();
			modelBuilder3.begin();
			MeshPartBuilder builder3 = modelBuilder3.part("Z", 1, 3, new Material());
			builder3.setColor(Color.BLUE);
			end.set(start).add(new Vector3(Vector3.Z).scl(3));
			builder3.line(start.x, start.y, start.z, end.x, end.y, end.z);
			instances.add(new ModelInstance(modelBuilder3.end()));
		}
	}

	private void loading() {
		field.loading();
        for (Car car : cars) {
            car.loading();
        }
		loading = true;
	}

	private void doneLoading() {
		field.doneLoading();
		for (Car car : cars) {
			car.doneLoading();
		}
		loading = false;
	}

	@Override
	public void render() {
		if (loading && assetManager.update()) {
			doneLoading();
			myGestureDetector.unlockCamera();
		}

		//Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		myGestureDetector.cameraPosChange();
		camera.update();

		modelBatch.begin(camera);
		modelBatch.render(instances, environment);
		modelBatch.end();

		spriteBatch.begin();
		//field.drawUI(spriteBatch, camera);
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		instances.clear();
		assetManager.dispose();
		Chunk.bitmapFont.dispose();
	}

	enum Difficulty {
		Easy,
		Hard
	}

	enum Direction {
		Forward,
		Back,
		Left,
		Right
	}
}
