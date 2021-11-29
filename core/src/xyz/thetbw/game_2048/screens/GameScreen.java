package xyz.thetbw.game_2048.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import xyz.thetbw.game_2048.MyGame;
import xyz.thetbw.game_2048.game.GameRegion;
import xyz.thetbw.game_2048.utils.Util_FreeTypeCreater;

import javax.xml.soap.Text;

public class GameScreen extends AbstractScreen implements GameRegion.GameListener {
    private GameRegion gameRegion;
    private String text = "0123456789";


    private Label score;
    private Texture bg;
    private ImageButton resetButton;
    private TextButton backButton;
    //保存按钮
    private TextButton saveButton;
    private SpriteBatch batch;
    private GameScreen gameScreen;

    private Window dialog_window;
    private TextButton window_reset_button;
    private TextButton window_back_button;
    private Label window_tip_text;
    private Label window_score_text;
    private boolean recoveryMode;

    public GameScreen(MyGame game) {
        super(game);
        gameScreen = this;
        gameRegion = new GameRegion(game, this);
        batch = new SpriteBatch();
    }

    public GameScreen(MyGame game,boolean recoveryMode){
        this(game);
        this.recoveryMode = recoveryMode;
    }

    @Override
    public void drawSelf(float delta) {
        batch.begin();
        batch.draw(bg, 0, 0, stage.getWidth(), stage.getHeight());
        batch.end();
        gameRegion.draw(delta);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gameRegion.setPosition((width - gameRegion.getWidth()) / 2, (height - gameRegion.getHeight()) / 2);
        score.setSize(350, 186);
        score.setAlignment(Align.center);
        score.setPosition(50, gameRegion.getY() + gameRegion.getHeight() + 100);
        resetButton.setPosition(score.getX() + score.getWidth() + 50, score.getY());
        backButton.setPosition(width - backButton.getWidth() - 50, resetButton.getY());
        saveButton.setPosition(width - backButton.getWidth() - 50,backButton.getY()+backButton.getHeight()+10);
        dialog_window.setPosition((width - dialog_window.getWidth()) / 2, (height - dialog_window.getHeight()) / 2);
        window_back_button.setPosition((dialog_window.getWidth() - window_back_button.getWidth()) / 2, 50);
        window_reset_button.setPosition(window_back_button.getX(), window_back_button.getY() + window_back_button.getHeight() + 30);
        window_tip_text.setPosition((dialog_window.getWidth() - window_tip_text.getWidth()) / 2, dialog_window.getHeight() - window_tip_text.getHeight() - 30);
        window_score_text.setPosition((dialog_window.getWidth() - window_score_text.getWidth()) / 2, window_tip_text.getY() - window_tip_text.getHeight() - 20);
    }

    @Override
    public void show() {
        super.show();
        gameRegion.show();
        gameRegion.reset();
        if (recoveryMode){
            gameRegion.recoveryFromLast();
        }
        bg = game.background;

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.background = new TextureRegionDrawable(game.manager.get("texture/game/game_score_bg.png", Texture.class));
        labelStyle.font = Util_FreeTypeCreater.getInstance().createBitmapFont(text, 100, null, 0);
        score = new Label("0", labelStyle);

        ImageButton.ImageButtonStyle resetButtonStyle = new ImageButton.ImageButtonStyle();
        resetButtonStyle.imageDown = new TextureRegionDrawable(game.manager.get("texture/game/game_reset_clicked.png", Texture.class));
        resetButtonStyle.imageUp = new TextureRegionDrawable(game.manager.get("texture/game/game_reset_.png", Texture.class));
        resetButton = new ImageButton(resetButtonStyle);

        TextButton.TextButtonStyle backButtonStyle = new TextButton.TextButtonStyle();
        backButtonStyle.down = new TextureRegionDrawable(game.manager.get("texture/game/game_back_lobby_clicked.png", Texture.class));
        backButtonStyle.up = new TextureRegionDrawable(game.manager.get("texture/game/game_back_lobby_.png", Texture.class));
        backButtonStyle.font = Util_FreeTypeCreater.getInstance().createBitmapFont("返回主页", 50, null, 0);
        backButton = new TextButton("返回主页", backButtonStyle);

        TextButton.TextButtonStyle saveButtonStyle = new TextButton.TextButtonStyle();
        saveButtonStyle.down = new TextureRegionDrawable(game.manager.get("texture/game/game_back_lobby_clicked.png", Texture.class));
        saveButtonStyle.up = new TextureRegionDrawable(game.manager.get("texture/game/game_back_lobby_.png", Texture.class));
        backButtonStyle.font = Util_FreeTypeCreater.getInstance().createBitmapFont("保存游戏",50,Color.WHITE,0);
        saveButton = new TextButton("保存游戏",backButtonStyle);


        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.background = new TextureRegionDrawable(game.manager.get("texture/game_dialog_bg.png", Texture.class));
        windowStyle.titleFont = new BitmapFont();

        dialog_window = new Window("", windowStyle);
        dialog_window.setZIndex(1000);
        dialog_window.setSize(722, 1052);
        dialog_window.setVisible(false);

        TextButton.TextButtonStyle window_reset_button_style = new TextButton.TextButtonStyle();
        window_reset_button_style.font = Util_FreeTypeCreater.getInstance().createBitmapFont("重新游戏返回大厅", 70, null, 0);
        window_reset_button_style.up = new TextureRegionDrawable(game.manager.get("texture/game/game_back_lobby_.png", Texture.class));
        window_reset_button_style.down = new TextureRegionDrawable(game.manager.get("texture/game/game_back_lobby_clicked.png", Texture.class));
        window_reset_button = new TextButton("重新游戏", window_reset_button_style);

        window_back_button = new TextButton("返回大厅", window_reset_button_style);



        Label.LabelStyle tip_label_style = new Label.LabelStyle();
        tip_label_style.font = Util_FreeTypeCreater.getInstance().createBitmapFont("游戏得分", 100, new Color(1f, .8f, .4f, 1f), 0);
        Label.LabelStyle score_label_style = new Label.LabelStyle();
        score_label_style.font = Util_FreeTypeCreater.getInstance().createBitmapFont("1234567890", 70, null, 0);
        window_tip_text = new Label("游戏得分", tip_label_style);
        window_score_text = new Label("0", score_label_style);

        dialog_window.addActor(window_tip_text);
        dialog_window.addActor(window_score_text);
        dialog_window.addActor(window_reset_button);
        dialog_window.addActor(window_back_button);


        stage.addActor(score);
        stage.addActor(resetButton);
        stage.addActor(backButton);
        stage.addActor(saveButton);
        stage.addActor(dialog_window);
        addListener();
    }

    private void back() {
        game.setScreen(new LobbyScreen(game));
        dialog_window.setVisible(false);
        gameScreen.dispose();
    }

    private void addListener() {
        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameRegion.reset();
            }
        });
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                back();
            }
        });
        window_back_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                back();
            }
        });
        window_reset_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameRegion.reset();
                dialog_window.setVisible(false);
            }
        });

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameRegion.save();
            }
        });
    }

    @Override
    public void resetBackground() {

    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void dispose() {
        super.dispose();
        gameRegion.dispose();
    }

    @Override
    public void onScoreChange(int score) {
        if (this.score != null)
            this.score.setText(String.valueOf(score));
    }

    private void show_Score(int Score) {
        dialog_window.setVisible(true);
        window_score_text.setText(String.valueOf(score));
        window_score_text.setPosition((dialog_window.getWidth() - window_score_text.getWidth()) / 2, window_tip_text.getY() - window_tip_text.getHeight() - 20);
    }

    @Override
    public void onGameOver(boolean win) {
        show_Score(gameRegion.getScore());
    }



}
