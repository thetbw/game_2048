package xyz.thetbw.game_2048.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Queue;
import xyz.thetbw.game_2048.utils.Util_FreeTypeCreater;

import java.util.Iterator;

public class GameElement {
    /**
     * 游戏元素的尺寸
     */
    public static final float DEFAULT_WIDTH = 200;
    public static final float DEFAULT_HEIGHT = 200;
    public static final String TEXT = "0123456789";

    private GameRegion gameRegion;
    private float x;
    private float y;
    private int font_size = 80;

    private float font_x;
    private float font_y;
    private float font_width;
    private float font_height;

    private float width = DEFAULT_WIDTH;
    private float height = DEFAULT_HEIGHT;

    public int power;
    private Texture bg;
    private BitmapFont font;


    /**
     * 当前元素是否已被销毁
     */
    private boolean destroyed;


    /**
     * 是否要销毁
     */
    private boolean be_destroy;



    private Animate moveAnimate;

    private Animate beDoubleAnimate;


    public GameElement(GameRegion game, int power) {
        this.gameRegion = game;
        this.power = power;
        init();
    }

    private void init() {
        loadTexture();
        loadFont();
    }

    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    private void loadTexture() {
        bg = gameRegion.getBg(power);
    }

    private void loadFont() {
        font = Util_FreeTypeCreater.getInstance().createBitmapFont(TEXT, font_size, null, 0);
    }

    private void updateFontSize() {
        BitmapFontCache cache = font.getCache();
        GlyphLayout layout = cache.addText(String.valueOf((int) (Math.pow(2, power))), 0, 0);
        font_height = layout.height;
        font_width = layout.width;

        while (font_width > width) {
            font_size -= 10;
            loadFont();
            updateFontSize();
            return;
        }
        font_x = x + (width * gameRegion.zoom - font_width) / 2;
        font_y = y + (height * gameRegion.zoom + font_height) / 2;

    }

    private void update(float delta) {
        updateFontSize();
        if (destroyed) return;
        if (moveAnimate != null){
            moveAnimate.update(delta);
            if ( moveAnimate.isEnd()){
                moveAnimate = null;
            }
        }

        if (beDoubleAnimate != null) {
            beDoubleAnimate.update(delta);
            if (beDoubleAnimate.isEnd()) {
                beDoubleAnimate = null;
            }
        }

    }

    public void draw(SpriteBatch batch, float delta) {
        update(delta);
        if (destroyed) return;
        batch.draw(bg, getX(), getY(), width * gameRegion.zoom, height * gameRegion.zoom);
        font.draw(batch, String.valueOf((int) (Math.pow(2, power))), font_x, font_y);
    }

    public void beDouble() {
        if (moveAnimate != null){
            moveAnimate.addOnAnimateEndCallBack((animate -> beDoubleAnimate = new BeDoubleAnimate().start()));
        }else {
            beDoubleAnimate = new BeDoubleAnimate().start();
        }
    }

    private void _double() {
        power++;
        loadTexture();
    }

    public void toDestroy() {
        be_destroy = true;
    }

    private void destroy() {
        destroyed = true;
        try {
            if (font != null)
                font.dispose();
        } catch (GdxRuntimeException e) {
        }
    }

    /**
     * 移动
     */
    public void moveTo(float x, float y) {
        if (x == this.x && y == this.y) return;
        moveAnimate = new MoveAnimate(x, y).start();
    }

    public boolean isAnimating() {
        return moveAnimate != null || beDoubleAnimate != null;
    }

    /**
     * 当前元素是否已被销毁
     */
    public boolean isDestroyed() {
        return destroyed;
    }

    /**
     * 当前元素是否正在销毁
     */
    public boolean isDestroying() {
        return be_destroy;
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


    private class MoveAnimate extends Animate {
        private static final float ANIMATE_DURATION_TIME = 0.2f;
        private float moved_speed_x;
        private float moved_speed_y;

        private float target_x;
        private float target_y;

        public int frame_count;
        public float animate_time;


        public MoveAnimate(float targetX, float targetY) {
            super(ANIMATE_DURATION_TIME, (animate) -> {
                MoveAnimate moveAnimate = (MoveAnimate) animate;
                Gdx.app.log("MoveAnimate", "end");
                Gdx.app.log("MoveAnimate", "x:" + x + " y:" + y+" targetX:"+targetX+" targetY:"+targetY);
                Gdx.app.log("动画持续时间",moveAnimate.animate_time  +"");
                Gdx.app.log("持续帧数",moveAnimate.frame_count  +"");
                x = targetX;
                y = targetY;
                if (be_destroy) {
                    destroy();
                }
            });
            this.target_x = targetX;
            this.target_y = targetY;
            moved_speed_x = (targetX-x) / ANIMATE_DURATION_TIME;
            moved_speed_y = (targetY-y) / ANIMATE_DURATION_TIME;
            Gdx.app.log("MoveAnimate", "x:" + x + " y:" + y + " targetX:" + targetX + " targetY:" + targetY);
            Gdx.app.log("speed", moved_speed_x + " :" + moved_speed_y);
        }

        @Override
        public void updateAnimate(float deltaTime, float animateTime) {
            frame_count ++;
            animate_time += deltaTime;
            x += moved_speed_x * deltaTime;
            y += moved_speed_y * deltaTime;
        }
    }

    private class BeDoubleAnimate extends Animate {
        private static final float ANIMATE_DURATION_TIME = 0.2f;

        private float scale_speed;


        public BeDoubleAnimate() {
            super(ANIMATE_DURATION_TIME, (animate) -> {
                _double();
                width = DEFAULT_WIDTH;
                height = DEFAULT_HEIGHT;
            });
            float scaleWidth = DEFAULT_WIDTH * 1.2f;
            scale_speed =  (scaleWidth - DEFAULT_WIDTH) / (ANIMATE_DURATION_TIME /2);
        }

        @Override
        public void updateAnimate(float deltaTime, float animateTime) {
            if (animateTime < ANIMATE_DURATION_TIME / 2) {
                width += scale_speed * deltaTime;
                height += scale_speed * deltaTime;
            }else {
                width -= scale_speed * deltaTime;
                height -= scale_speed * deltaTime;
            }
        }
    }

}
