package xyz.thetbw.game_2048.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import xyz.thetbw.game_2048.MyGame;
import xyz.thetbw.game_2048.input.GameInputProcess;
import xyz.thetbw.game_2048.input.GestureListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class GameRegion extends GestureListenerAdapter {
    private static final String LOG_TAG = "GameRegion";

    //游戏存档properties键名称
    public static final String GAME_DATA_PROFILE_NAME = "save";
    public static final String GAME_DATA_PROFILE_KEY = "data";

    public MyGame game;
    private GameListener listener;
    private SpriteBatch batch;

    private float width = 950;
    private float height = 950;
    private float x = 0;
    private float y = 0;
    public float zoom = 1;
    private float offset; //空隙
    private int size;  //大小

    private float flingVelocityLimit = 500; //滑动最小速度限制

    private float[][] element_x; //x：列
    private float[][] element_y;//y:行

    private GameElement[][] gameElements;
    private int elements_size;

    private Texture background;
    private Texture space_bg;

    private int score;//游戏分数
    private GameInputProcess inputProcess;
    private List<Texture> bgs;

    public GameRegion(MyGame game) {
        this.game = game;
        size = 4;
        inputProcess = new GameInputProcess(this);
        game.multiplexer.addProcessor(inputProcess);
        init();
    }

    public GameRegion(MyGame game, GameListener listener) {
        this(game);
        this.listener = listener;
    }

    /**
     * 初始化宽和高
     */
    private void init() {
        float screen_w = Gdx.graphics.getWidth();
        float screen_h = Gdx.graphics.getHeight();
        if (screen_w < width || screen_h / 2 < height) {
            float difference_w = width - screen_w;
            float difference_h = height = screen_h / 2;
            if (difference_w > difference_h) {
                width = screen_w - 100;
                zoom = width / screen_w;
                height = width;
            } else {
                height = screen_h / 2 - 100;
                zoom = height / (screen_h / 2);
                width = height;
            }
        }
        loadTexture();
        batch = new SpriteBatch();
        gameElements = new GameElement[size][size];
        initElementPosition();
    }

    public void recovery(GameData data) {
        this.size = data.size;
        for (int i = 0; i < size; i++) {
            for (int y = 0; y < size; y++) {
                if (data.elements[i][y] != -1) {
                    int power = data.elements[i][y];
                    this.gameElements[i][y] = new GameElement(this, power);
                }

            }
        }
    }

    /**
     * 从最后一个存档恢复
     */
    public void recoveryFromLast() {
        if (!existGameData()) {
            game.showNotice("未找到存档");
            return;
        }
        String json = Gdx.app.getPreferences(GAME_DATA_PROFILE_NAME).getString(GAME_DATA_PROFILE_KEY);
        Gson gson = new Gson();
        GameData data = gson.fromJson(json, GameData.class);
        recovery(data);
    }


    private void loadTexture() {
        bgs = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            Texture texture = game.manager.get("texture/game/game_element_" + i + ".png", Texture.class);
            bgs.add(texture);
        }
    }

    public Texture getBg(int power) {
        if (power <= 16) {
            return bgs.get(power - 1);
        } else {
            return bgs.get(15);
        }
    }

    private void initElementPosition() {
        offset = (width - (GameElement.DEFAULT_WIDTH * size)) / (size + 1);
        element_x = new float[size][size];
        element_y = new float[size][size];
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                element_x[x][y] = this.x + (offset * (x + 1)) + (GameElement.DEFAULT_WIDTH * x);
                element_y[x][y] = this.y + (offset * (y + 1)) + (GameElement.DEFAULT_WIDTH * y);
            }
        }
    }

    /**
     * 设置坐标
     *
     * @param x
     * @param y
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        initElementPosition();
        relocationElement();
    }

    /**
     * 准备显示调用
     */
    public void show() {
        space_bg = game.manager.get("texture/game/game_element_bg.png", Texture.class);
        background = game.manager.get("texture/game/game_checkerboard_bg.png", Texture.class);
        initGame();
    }

    /**
     * 初始化游戏
     */
    public void initGame() {
        randomElement();
    }

    /**
     * 重定位元素
     */
    private void relocationElement() {
        boolean inAnimation = inAnimating();
        elements_size = 0;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (gameElements[x][y] == null)
                    continue;
                if (gameElements[x][y].isDestroyed()){
                    gameElements[x][y] = null;
                }
                if (!inAnimation && gameElements[x][y] != null) {
                    elements_size ++;
                    gameElements[x][y].setPosition(element_x[x][y], element_y[x][y]);
                }

            }
        }
    }

    private boolean inAnimating(){
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (gameElements[x][y] != null && gameElements[x][y].isAnimating())
                    return true;
            }
        }
        return false;
    }

    /**
     * 随机生成元素
     */
    public void randomElement() {
        if (elements_size >= size * size) {
            return;
        }
        int element_position = (int) ((Math.random() * 100) % (Math.pow(size, 2) - elements_size));
        GameElement element = new GameElement(this, 1);
        System.out.println("element_positon:" + element_position);

        for (int i = 0; i < size; i++) {
            if (element_position < 0) break;
            for (int a = 0; a < size; a++) {
                if (element_position < 0) break;
                if (gameElements[i][a] == null) {
                    if (element_position == 0) {
                        Gdx.app.log(LOG_TAG, "添加元素:x-" + element_x[i][a] + ";y-" + element_y[i][a]);
                        gameElements[i][a] = element;
                        element.setPosition(element_x[i][a], element_y[i][a]);
                    }
                    element_position--;
                }
            }
        }
        elements_size++;
    }


    /**
     * 获取当前游戏数据,用于保存数据
     *
     * @return
     */
    private GameData getCurrentGameData() {
        int _size = this.size;
        int[][] elements = new int[_size][_size];
        for (int i = 0; i < _size; i++) {
            for (int y = 0; y < _size; y++) {
                if (this.gameElements[i][y] != null) {
                    elements[i][y] = this.gameElements[i][y].power;
                } else {
                    elements[i][y] = -1;
                }
            }
        }
        GameData data = new GameData();
        data.size = _size;
        data.elements = elements;
        return data;
    }

    /**
     * 检查游戏结果 输
     */
    public boolean isFault() {
        if (elements_size < size * size)
            return false;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (gameElements[x][y] == null)
                    continue;
                if (!(x + 1 >= size)) {
                    if (gameElements[x + 1][y] != null)
                        if (gameElements[x][y].power == gameElements[x + 1][y].power)
                            return false;
                }
                if (!(y + 1 >= size)) {
                    if (gameElements[x][y + 1] != null)
                        if (gameElements[x][y].power == gameElements[x][y + 1].power)
                            return false;
                }
                if (!(x - 1 < 0)) {
                    if (gameElements[x - 1][y] != null)
                        if (gameElements[x][y].power == gameElements[x - 1][y].power)
                            return false;
                }
                if (!(y - 1 < 0)) {
                    if (gameElements[x][y - 1] != null)
                        if (gameElements[x][y].power == gameElements[x][y - 1].power)
                            return false;
                }
            }
        }
        return true;
    }

    /**
     * 重置游戏
     */
    public void reset() {
        for (GameElement[] elements : gameElements) {
            for (GameElement element : elements) {
                if (element != null)
                    element.toDestroy();
            }
        }
        gameElements = new GameElement[size][size];
        elements_size = 0;
        score = 0;
        if (listener != null)
            listener.onScoreChange(score);
        initGame();
    }

    /**
     * 保存游戏进度
     */
    public void save() {
        GameData data = getCurrentGameData();
        new Thread(() -> {
            Gson gson = new Gson();
            String json = gson.toJson(data);
            Preferences preferences = Gdx.app.getPreferences(GAME_DATA_PROFILE_NAME);
            preferences.putString(GAME_DATA_PROFILE_KEY, json);
            preferences.flush();
            game.showNotice("保存成功");
        }).start();
    }

    /**
     * 当前是否存在游戏存档
     *
     * @return
     */
    public static boolean existGameData() {
        return Gdx.app.getPreferences(GAME_DATA_PROFILE_NAME).contains(GAME_DATA_PROFILE_KEY);
    }


    /**
     * 绘制游戏界面
     *
     * @param delta
     */
    public void draw(float delta) {
        relocationElement();
        batch.begin();
        batch.draw(background, x, y, width, height);
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                batch.draw(space_bg, element_x[x][y], element_y[x][y], GameElement.DEFAULT_WIDTH * zoom, GameElement.DEFAULT_HEIGHT * zoom);
            }
        }
        for (GameElement[] elements : gameElements) {
            for (GameElement element : elements) {
                if (element != null)
                    element.draw(batch, delta);
            }
        }
        batch.end();
    }

    private void countScore(GameElement element) {
        score += Math.pow(2, element.power) * 2;
        if (listener != null)
            listener.onScoreChange(score);
    }

    /**
     * 各种事件
     */
    private void flingUp() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (gameElements[x][y] == null || y == size-1) {
                    continue;
                }
                GameElement current = gameElements[x][y];
                float final_x = -1; //最终落的位置
                float final_y = -1;
                for (int i = y; i < size - 1; i++) {
                    GameElement next = gameElements[x][i + 1];
                    if (gameElements[x][i + 1] != null && !next.isDestroyed() && !next.isDestroying()) {
                        if (gameElements[x][i].power == next.power) {
                            countScore(gameElements[x][i]);
                            next.beDouble();
                            gameElements[x][i].toDestroy();
                            elements_size--;
                            final_x = element_x[x][i+1];
                            final_y = element_y[x][i+1];
                        }
                        break;
                    }
                    final_x = element_x[x][i + 1];
                    final_y = element_y[x][i + 1];
                    gameElements[x][i + 1] = gameElements[x][i];
                    gameElements[x][i] = null;
                }
                if (final_x != -1 && final_y != -1){
                    current.moveTo(final_x, final_y);
                }

            }
        }


    }

    private void flingDown() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (gameElements[x][y] == null) {
                    continue;
                }
                for (int i = y; i > 0 ; i--) {
                    GameElement next = gameElements[x][i - 1];
                    if (next != null && !next.isDestroyed() && !next.isDestroying()) {
                        if (gameElements[x][i].power == next.power) {
                            countScore(gameElements[x][i]);
                            next.beDouble();
                            gameElements[x][i].toDestroy();
                            gameElements[x][i].moveTo(element_x[x][i - 1], element_y[x][i - 1]);
                            elements_size--;
                        }
                        break;
                    }
                    gameElements[x][i].moveTo(element_x[x][i - 1], element_y[x][i - 1]);
                    gameElements[x][i - 1] = gameElements[x][i];
                    gameElements[x][i] = null;
                }

            }
        }
    }

    private void flingLeft() {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (gameElements[x][y] == null) continue;
                for (int i = x; i > 0; i--) {
                    GameElement next = gameElements[i - 1][y];
                    if (next != null && !next.isDestroyed() && !next.isDestroying()) {
                        if (gameElements[i][y].power == next.power) {
                            countScore(gameElements[i][y]);
                            next.beDouble();
                            gameElements[i][y].toDestroy();
                            gameElements[i][y].moveTo(element_x[i - 1][y], element_y[i - 1][y]);
                            elements_size--;
                        }
                        break;
                    }
                    gameElements[i][y].moveTo(element_x[i - 1][y], element_y[i - 1][y]);
                    gameElements[i - 1][y] = gameElements[i][y];
                    gameElements[i][y] = null;
                }
            }
        }
    }

    private void flingRight() {
        for (int y = 0; y < size; y++) {
            for (int x = size - 1; x > -1; x--) {
                if (gameElements[x][y] == null) continue;
                for (int i = x; i < size - 1 ; i++) {
                    GameElement next = gameElements[i + 1][y];
                    if (next != null && !next.isDestroyed() && !next.isDestroying()) {
                        if (gameElements[i][y].power == next.power) {
                            countScore(gameElements[i][y]);
                            next.beDouble();
                            gameElements[i][y].toDestroy();
                            elements_size--;
                            gameElements[i][y].moveTo(element_x[i + 1][y], element_y[i + 1][y]);
                        }
                        break;
                    }
                    gameElements[i][y].moveTo(element_x[i + 1][y], element_y[i + 1][y]);
                    gameElements[i + 1][y] = gameElements[i][y];
                    gameElements[i][y] = null;
                }
            }
        }
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        if (inAnimating()){
            return false;
        }
        if (Math.abs(velocityX) < flingVelocityLimit && Math.abs(velocityY) < flingVelocityLimit)
            return false;
        if ((Math.abs(velocityX) - Math.abs(velocityY)) > 0) {
            if (velocityX > 0)
                flingRight();
            else flingLeft();
            if (!isFault()) {
                randomElement();
            } else {
                if (listener != null)
                    listener.onGameOver(false);
            }
            return true;
        } else if ((Math.abs(velocityX) - Math.abs(velocityY)) < 0) {
            if (velocityY > 0)
                flingDown();
            else flingUp();
            if (!isFault()) {
                randomElement();
            } else {
                if (listener != null)
                    listener.onGameOver(false);
            }
            return true;
        } else return false;
    }

    /**
     * 游戏信息接口
     */
    public interface GameListener {

        void onScoreChange(int score);

        void onGameOver(boolean win);


    }


    public int getScore() {
        return score;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Texture getBackground() {
        return background;
    }

    public void setBackground(Texture background) {
        this.background = background;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void dispose() {
        for (GameElement[] elements : gameElements) {
            for (GameElement element : elements) {
                if (element != null)
                    element.toDestroy();
            }
        }
        batch.dispose();
        game.multiplexer.removeProcessor(inputProcess);
    }

    public void onVisible() {

    }


    public static class GameData {
        private int size;
        private int[][] elements;


        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int[][] getElements() {
            return elements;
        }

        public void setElements(int[][] elements) {
            this.elements = elements;
        }
    }
}
