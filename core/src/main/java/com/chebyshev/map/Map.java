package com.chebyshev.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;

// 最终版 瓦片地图Actor，适配map.tmx所有特性，无缝加入Stage
public class Map extends Actor implements Disposable {
    // ====== 和你的地图完全一致的配置，固定32x32 ======
    public static final int TILE_SIZE = 16;
    private final TiledMap tiledMap;
    private final TiledMapRenderer mapRenderer;
    private final int mapWidth;  // 地图总像素宽度
    private final int mapHeight; // 地图总像素高度

    // 构造方法：直接传入tmx地图路径即可加载
    public Map(String tmxFilePath) {
        // 1. 核心加载：一行代码加载你的map.tmx
        tiledMap = new TmxMapLoader().load(tmxFilePath);

        // 2. 创建正交渲染器，渲染所有瓦片层（自动按Tiled图层顺序渲染：上遮下）
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // 3. 自动读取地图尺寸（从map.tmx的属性中获取，不用手写死，适配任意地图大小）
        int mapTileWidth = tiledMap.getProperties().get("width", Integer.class);
        int mapTileHeight = tiledMap.getProperties().get("height", Integer.class);
        this.mapWidth = mapTileWidth * TILE_SIZE;
        this.mapHeight = mapTileHeight * TILE_SIZE;

        // 4. 设置Actor的基础属性，坐标原点(0,0)，和你的战车坐标完全统一
        setX(0);
        setY(0);
        setWidth(mapWidth);
        setHeight(mapHeight);
    }

    // ====== Actor核心绘制方法：渲染整个地图 ======
    // 自动渲染所有瓦片层：ground→obstacle→decor，顺序和Tiled里一致
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        mapRenderer.setView((OrthographicCamera) getStage().getCamera());
        mapRenderer.render();
    }

    // ====== 对外提供获取地图的方法，给Player读取图层/属性用 ======
    public TiledMap getTiledMap() {
        return tiledMap;
    }

    // ====== 获取地图总尺寸，给Player做边界检测用 ======
    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    // ====== 资源释放，防止内存泄漏，必须实现 ======
    @Override
    public void dispose() {
        tiledMap.dispose();
    }
}
