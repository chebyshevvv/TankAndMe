package com.chebyshev;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.chebyshev.character.Player;
import com.chebyshev.map.Map;

public class GameScreen implements Screen {
    private final Stage stage;
    private Map map;
    private Player player;

    public GameScreen() {
        stage = new Stage(new FitViewport(16 * 30,16 * 20));
    }

    @Override
    public void show() {
        map = new Map("map/map.tmx");
        player = new Player(map);
        stage.addActor(map);
        stage.addActor(player);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }
    @Override
    public void dispose() {
        stage.dispose();
        map.dispose();
        player.dispose();
    }

    // 空实现方法（无改动）
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
