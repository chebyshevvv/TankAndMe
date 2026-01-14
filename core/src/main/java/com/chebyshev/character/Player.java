package com.chebyshev.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.chebyshev.map.Map;

import static com.chebyshev.map.Map.TILE_SIZE;

public class Player extends Actor implements Disposable {
    public static final int TANK_SIZE = 8;
    public final float TANK_SPEED = 90f;
    private final Map map;

    private TextureRegion tankUp, tankUpRight, tankRight, tankDownRight;
    private TextureRegion tankDown, tankDownLeft, tankLeft, tankUpLeft;
    private TextureRegion currentTank;

    public Player(Map map) {
        this.map = map;
        for (MapLayer layer : map.getTiledMap().getLayers()) {
            if (layer.getName().equals("spawn")){
                for (MapObject object : layer.getObjects()) {
                    if (object.getName().equals("player")){
                        setX(object.getProperties().get("x", Float.class) - (float) TANK_SIZE / 2);
                        setY(object.getProperties().get("y", Float.class) - (float) TANK_SIZE / 2);
                    }
                }
            }
        }
        setWidth(TANK_SIZE);
        setHeight(TANK_SIZE);
        tankUp = new TextureRegion(new Texture(Gdx.files.internal("tank/tank_up.png")));
        tankUpRight = new TextureRegion(new Texture(Gdx.files.internal("tank/tank_up_right.png")));
        tankRight = new TextureRegion(new Texture(Gdx.files.internal("tank/tank_right.png")));
        tankDownRight = new TextureRegion(new Texture(Gdx.files.internal("tank/tank_down_right.png")));
        //tankDown = new TextureRegion(new Texture(Gdx.files.internal("tank/tank_down.png")));
        tankDown = new TextureRegion(new Texture(Gdx.files.internal("map/river1.png")));
        tankDownLeft = new TextureRegion(new Texture(Gdx.files.internal("tank/tank_down_left.png")));
        tankLeft = new TextureRegion(new Texture(Gdx.files.internal("tank/tank_left.png")));
        tankUpLeft = new TextureRegion(new Texture(Gdx.files.internal("tank/tank_up_left.png")));

        currentTank = tankDown;
    }

    @Override
    public void act(float delta) {
        updateTankMovement(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(currentTank, getX(), getY(), getWidth(), getHeight());
    }
    private void updateTankMovement(float delta) {
        float moveX = 0;
        float moveY = 0;

        // 按键监听：WASD/上下左右都支持
        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);

        // 8方向判断+纹理切换+移动矢量赋值
        if (up && !down && !left && !right) {
            //currentTank = tankUp;
            moveY = TANK_SPEED * delta;
        } else if (up && right) {
            //currentTank = tankUpRight;
            moveX = TANK_SPEED * delta * 0.7f;
            moveY = TANK_SPEED * delta * 0.7f;
        } else if (right && !down) {
            //currentTank = tankRight;
            moveX = TANK_SPEED * delta;
        } else if (down && right) {
            //currentTank = tankDownRight;
            moveX = TANK_SPEED * delta * 0.7f;
            moveY = -TANK_SPEED * delta * 0.7f;
        } else if (down && !up && !left) {
            //currentTank = tankDown;
            moveY = -TANK_SPEED * delta;
        } else if (down && left) {
            //currentTank = tankDownLeft;
            moveX = -TANK_SPEED * delta * 0.7f;
            moveY = -TANK_SPEED * delta * 0.7f;
        } else if (left && !up) {
            //currentTank = tankLeft;
            moveX = -TANK_SPEED * delta;
        } else if (up && left) {
            //currentTank = tankUpLeft;
            moveX = -TANK_SPEED * delta * 0.7f;
            moveY = TANK_SPEED * delta * 0.7f;
        }
        float targetX = getX() + moveX;
        float targetY = getY() + moveY;
        if (isBlocked(targetX, getY())) {
            targetX = getX();
        }
        if (isBlocked(getX(), targetY)) {
            targetY = getY();
        }
        setX(targetX);
        setY(targetY);
    }

    @Override
    public void dispose() {
        tankUp.getTexture().dispose();
        tankUpRight.getTexture().dispose();
        tankRight.getTexture().dispose();
        tankDownRight.getTexture().dispose();
        tankDown.getTexture().dispose();
        tankDownLeft.getTexture().dispose();
        tankLeft.getTexture().dispose();
        tankUpLeft.getTexture().dispose();
        currentTank.getTexture().dispose();
    }
    private boolean isBlocked(float targetX, float targetY) {
        MapLayer tempLayer = map.getTiledMap().getLayers().get("obstacle");
        TiledMapTileLayer obstacleLayer = null;
        if(tempLayer instanceof TiledMapTileLayer){
            obstacleLayer = (TiledMapTileLayer) tempLayer;
        }
        if(obstacleLayer == null) return false;

        float tankRight = targetX + this.getWidth();
        float tankTop = targetY + this.getHeight();

        // 转瓦片坐标
        int startTileX = (int) (targetX / TILE_SIZE);
        int startTileY = (int) (targetY / TILE_SIZE);
        int endTileX = (int) (tankRight / TILE_SIZE);
        int endTileY = (int) (tankTop / TILE_SIZE);

        for (int x = startTileX; x <= endTileX; x++) {
            for (int y = startTileY; y <= endTileY; y++) {
                TiledMapTileLayer.Cell cell = obstacleLayer.getCell(x, y);
                if (cell != null && cell.getTile() != null) {
                    return true;
                }
            }
        }
        return false;

    }
}
