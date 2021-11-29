package xyz.thetbw.game_2048.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import xyz.thetbw.game_2048.MyGame;
import xyz.thetbw.game_2048.utils.Util_FreeTypeCreater;

import java.util.LinkedList;

/**
 * 通知屏幕
 */
public class NoticesScreen extends AbstractScreen{
    private LinkedList<String> notices; //通知队列
    private BitmapFont notice;
    private String notice_text;
    private Texture notice_bg;
    private boolean show;//是否在绘制
    private float x;
    private float y;

    private  float n_x;
    private float width;
    private float height =100;

    private float font_width;
    private float font_height;
    private float font_x;
    private float font_y;
    private float time=0;
    private float animationSpeed = 1000;

    private float temp;
    private boolean drawProgress;

    private SpriteBatch batch;

    private boolean delete;


    public NoticesScreen(MyGame game) {
        super(game);
        notices = new LinkedList<>();
        batch = new SpriteBatch();
    }



    @Override
    public void show() {
        game.manager.load("texture/notice_bg.png", Texture.class);
        game.manager.finishLoading();
        notice_bg = game.manager.get("texture/notice_bg.png",Texture.class);
        y=(int) stage.getHeight()-200;
    }

    private void prepareNotice(){
        width = font_width+100;
        font_y = y+height-((height-font_height)/2);
        x = stage.getWidth()-width;
        System.out.println(width+"---"+height);
    }
    private void initNotice(){
       if (!show){
            if (!notices.isEmpty()) {
                notice_text = notices.removeFirst();
                notice = Util_FreeTypeCreater.getInstance().createBitmapFont(notice_text, 50, null, 0);
                GlyphLayout layout = notice.getCache().addText(notice_text,0,0);
                font_width = (int)layout.width;
                font_height = (int) layout.height;
                show =true;
                prepareNotice();
            }
        }
    }

    public void addNotice(String notice){
        synchronized (notices){
            notices.addLast(notice);
        }
    }

    private void update(float delta){
        initNotice();
        if (!show)
            return;
        if (!drawProgress){
            temp +=  delta*animationSpeed;
            if (temp<width){
                n_x  = stage.getWidth()-temp;
            }else {
                n_x=x;
                time+=delta;
                if (time>=3){
                    time=0;
                    drawProgress =true;
                    temp = width;
                }
            }
        }else {
            temp -= delta*animationSpeed;
            if (temp>=0){
                n_x  = stage.getWidth()-temp;
            }else {
                show = false;
                drawProgress =false;
                delete =true;
            }
        }




    }
    @Override
    public void drawSelf(float delta) {
        update(delta);
        if (show){
            batch.begin();
            batch.draw(notice_bg,n_x,y,width,height);
            notice.draw(batch,notice_text,n_x+30,font_y);
            batch.end();
        }
        if (delete){
            notice.dispose();
            notice =null;
            delete =false;
        }
    }

    @Override
    public void resetBackground() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void hide() {

    }
}
