package xyz.thetbw.game_2048.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import xyz.thetbw.game_2048.MyGame;


/**
 * @author Mats Svensson
 */
public abstract class AbstractScreen implements Screen {

    public static Queue<String> toast_queue = new Queue();

    protected MyGame game;
    protected Stage stage;
    private SpriteBatch batch;


    public AbstractScreen(MyGame game) {
        this.game = game;
        stage = new Stage();
        batch = new SpriteBatch();
    }

    public void add(Actor actor){
        stage.addActor(actor);
    }


    public void addNoRepeat(Actor actor){
        Array<Actor> actors = stage.getActors();
        boolean flag = false;
        for (Actor a:actors) {
            if(a==actor)
                flag = true;
        }
        if(!flag)
            stage.addActor(actor);
    }

    public void remove(Actor actor){
        Array<Actor> actors = stage.getActors();
        Array<Actor> actors_copy = new Array<>();
        for (Actor a:actors) {
            if(a!=actor){
                actors_copy.add(a);
            }
        }
        stage.clear();
        for (Actor a:actors_copy) {
            if(a!=actor){
                stage.addActor(a);
            }
        }

    }

    @Override
    public void show() {
        game.multiplexer.addProcessor(stage);
    }

    /**
     * 绘制自身
     * @param delta
     */
    public abstract void drawSelf(float delta);

    /**
     * 绘制消息弹窗
     * @param delta
     */
    public void drawNotices(float delta){
        game.drawNotice(delta);
    }

    public void drawOthers(float delta){

    }

    /**
     *
     */
    public abstract void resetBackground();

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        drawSelf(delta);
        drawNotices(delta);
        drawOthers(delta);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        game.multiplexer.addProcessor(stage);
    }

    @Override
    public void hide() {
        game.multiplexer.removeProcessor(stage);
    }

    @Override
    public void dispose() {
        game.multiplexer.removeProcessor(stage);
        System.out.println("正在关闭");
    }

}
