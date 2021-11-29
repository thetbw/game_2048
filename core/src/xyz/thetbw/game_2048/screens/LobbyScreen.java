package xyz.thetbw.game_2048.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import xyz.thetbw.game_2048.MyGame;
import xyz.thetbw.game_2048.utils.Util_FreeTypeCreater;

public class LobbyScreen extends AbstractScreen {
    private Image title;
    private Image background;

    private TextButton play;
    private TextButton exit;
    private TextButton recovery;


    private ImageButton setting;
    private Window setting_window;
    private TextButton window_set_background;
    private ImageButton windows_close;

    private boolean isPlay = false;
    private boolean isRecovery = false;

    public LobbyScreen(MyGame game) {
        super(game);
        game.multiplexer.addProcessor(stage);
    }

    @Override
    public void show() {
        super.show();
        game.manager.finishLoading();
        game.loadGameAssets();
        title = new Image(game.manager.get("texture/lobby/game_lobby_title.png", Texture.class));
        background = new Image(game.background);
        TextButton.TextButtonStyle play_style = new TextButton.TextButtonStyle();
        play_style.font = Util_FreeTypeCreater.getInstance().createBitmapFont("play",100,null,0);
        play_style.up = new TextureRegionDrawable(game.manager.get("texture/lobby/game_lobby_button_pink_.png", Texture.class));
        play_style.down = new TextureRegionDrawable(game.manager.get("texture/lobby/game_lobby_button_pink_clicked.png", Texture.class));
        play = new TextButton("play",play_style);

        TextButton.TextButtonStyle exit_style = new TextButton.TextButtonStyle();
        exit_style.font = Util_FreeTypeCreater.getInstance().createBitmapFont("exit设置背景",100,null,0);
        exit_style.up = new TextureRegionDrawable(game.manager.get("texture/lobby/game_lobby_button_blue_.png", Texture.class));
        exit_style.down = new TextureRegionDrawable(game.manager.get("texture/lobby/game_lobby_button_blue_clicked.png", Texture.class));
        exit = new TextButton("exit",exit_style);

        TextButton.TextButtonStyle recovery_style = new TextButton.TextButtonStyle();
        recovery_style.font = Util_FreeTypeCreater.getInstance().createBitmapFont("load",100,null,0);
        recovery_style.up = new TextureRegionDrawable(game.manager.get("texture/lobby/game_lobby_button_blue_.png", Texture.class));
        recovery_style.down = new TextureRegionDrawable(game.manager.get("texture/lobby/game_lobby_button_blue_clicked.png", Texture.class));
        recovery = new TextButton("load",recovery_style);

        ImageButton.ImageButtonStyle setting_style = new ImageButton.ImageButtonStyle();
        setting_style.imageUp = new TextureRegionDrawable(game.manager.get("texture/lobby/game-setting_.png",Texture.class));
        setting_style.imageDown = new TextureRegionDrawable(game.manager.get("texture/lobby/game-setting_clicked.png",Texture.class));
        setting = new ImageButton(setting_style);
        setting.setSize(setting.getWidth()-30,setting.getHeight()-30);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.background = new TextureRegionDrawable(game.manager.get("texture/game_dialog_bg.png",Texture.class));
        windowStyle.titleFont = new BitmapFont();
        setting_window = new Window("",windowStyle);
        setting_window.setVisible(false);
        setting_window.setSize(722,1052);
        setting_window.setZIndex(1000);

        TextButton.TextButtonStyle set_background = new TextButton.TextButtonStyle();
        set_background.font = Util_FreeTypeCreater.getInstance().createBitmapFont("exit设置背景",50,null,0);
        set_background.up = new TextureRegionDrawable(game.manager.get("texture/lobby/game_lobby_button_blue_.png", Texture.class));
        set_background.down = new TextureRegionDrawable(game.manager.get("texture/lobby/game_lobby_button_blue_clicked.png", Texture.class));
        window_set_background = new TextButton("设置背景",set_background);


        ImageButton.ImageButtonStyle close_style = new ImageButton.ImageButtonStyle();
        close_style.imageUp = new TextureRegionDrawable(game.manager.get("texture/dialog_close_button.png",Texture.class));
        close_style.imageDown = new TextureRegionDrawable(game.manager.get("texture/dialog_close_button_clicked.png",Texture.class));
        windows_close = new ImageButton(close_style);

        setting_window.addActor(window_set_background);
        setting_window.addActor(windows_close);

        stage.addActor(background);
        stage.addActor(title);
        stage.addActor(play);
        stage.addActor(recovery);
        stage.addActor(exit);
        stage.addActor(setting);
        stage.addActor(setting_window);
        addListener();

    }

    private void loadAeeset(){

    }

    private void addListener(){
        play.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                isPlay = true;
            }
        });
        exit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.exit();
            }
        });
        setting.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setting_window.setVisible(true);
            }
        });
        windows_close.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setting_window.setVisible(false);
            }
        });
        window_set_background.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.openFile((s)->{
//                    game.initBackGround();
//                    resetBackground();
                });

            }
        });
        recovery.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                isRecovery = true;
            }
        });
    }

    @Override
    public void resetBackground() {
        background.setDrawable(new TextureRegionDrawable(game.background));
    }

    @Override
    public void drawSelf(float delta) {
        if (isPlay){
            if (game.manager.update()){
                game.setScreen(new GameScreen(game));
                this.dispose();
            }
        }
        if (isRecovery){
            if (game.manager.update()){
                game.setScreen(new GameScreen(game,true));
                this.dispose();
            }
        }
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        background.setSize(width,height);
        background.setPosition(0,0);
        title.setPosition((width-title.getWidth())/2,height-500);
        play.setPosition((width-play.getWidth())/2,title.getY()-400);
        recovery.setPosition((width-exit.getWidth())/2,play.getY()-200);
        exit.setPosition((width-exit.getWidth())/2,recovery.getY()-200);
        setting.setPosition(width-setting.getWidth()-20,20);
        setting_window.setPosition((width-setting_window.getWidth())/2,(height-setting_window.getHeight())/2);
        window_set_background.setPosition((setting_window.getWidth()-window_set_background.getWidth())/2,
                (setting_window.getHeight()-window_set_background.getHeight())/2);
        windows_close.setPosition(10,setting_window.getHeight()-windows_close.getHeight()-10);
    }

}
