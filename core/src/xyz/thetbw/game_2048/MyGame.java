package xyz.thetbw.game_2048;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import xyz.thetbw.game_2048.screens.GameScreen;
import xyz.thetbw.game_2048.screens.LobbyScreen;
import xyz.thetbw.game_2048.screens.NoticesScreen;
import xyz.thetbw.game_2048.utils.Util_FreeTypeCreater;

public class MyGame extends Game {

	public AssetManager manager;
	public InputMultiplexer multiplexer;
	private boolean isLoad = false;
	private FileChoose fileChoose;
	public Texture background;

	private NoticesScreen noticesScreen;

	public MyGame(FileChoose fileChoose){
		this.fileChoose = fileChoose;
	}

	public void openFile(FileChoose.FileCallback fileCallback){
		fileChoose.getFile("",fileCallback);
	}
	@Override
	public void create () {
		init();
		this.setScreen(new LobbyScreen(this));

	}

	public void drawNotice(float delta){
		noticesScreen.drawSelf(delta);
	}

	public void showNotice(String notice){
		noticesScreen.addNotice(notice);
	}

	private void init(){
		Util_FreeTypeCreater.getInstance().init();
		manager = new AssetManager();
		multiplexer = new InputMultiplexer();
		noticesScreen = new NoticesScreen(this);
		noticesScreen.show();
		Gdx.input.setInputProcessor(multiplexer);
		loadAssets();
		initBackGround();
	}

	public void initBackGround(){
		FileHandle fileHandle = Gdx.files.local("background");
		if (fileHandle.exists()){
			background = new Texture(fileHandle);
		}else {
			background = new Texture("texture/game_bg.png");
		}
	}

	@Override
	public void render() {
		super.render();
		Gdx.app.log("开始渲染","");
	}

	private void loadAssets(){
		manager.load("texture/lobby/game_lobby_title.png",Texture.class);
		manager.load("texture/lobby/game_lobby_button_pink_clicked.png",Texture.class);
		manager.load("texture/lobby/game_lobby_button_pink_.png",Texture.class);
		manager.load("texture/lobby/game_lobby_button_blue_clicked.png",Texture.class);
		manager.load("texture/lobby/game_lobby_button_blue_.png",Texture.class);
		manager.load("texture/game_dialog_bg.png",Texture.class);
		manager.load("texture/lobby/game-setting_.png",Texture.class);
		manager.load("texture/lobby/game-setting_clicked.png",Texture.class);
		manager.load("texture/dialog_close_button.png",Texture.class);
		manager.load("texture/dialog_close_button_clicked.png",Texture.class);
	}

	public void loadGameAssets(){
		if (isLoad)
			return;
		isLoad = true;
		manager.load("texture/game/game_back_lobby_.png",Texture.class);
		manager.load("texture/game/game_back_lobby_clicked.png",Texture.class);
		manager.load("texture/game/game_checkerboard_bg.png",Texture.class);
		manager.load("texture/game/game_element_1.png",Texture.class);
		manager.load("texture/game/game_element_2.png",Texture.class);
		manager.load("texture/game/game_element_3.png",Texture.class);
		manager.load("texture/game/game_element_4.png",Texture.class);
		manager.load("texture/game/game_element_5.png",Texture.class);
		manager.load("texture/game/game_element_6.png",Texture.class);
		manager.load("texture/game/game_element_7.png",Texture.class);
		manager.load("texture/game/game_element_8.png",Texture.class);
		manager.load("texture/game/game_element_9.png",Texture.class);
		manager.load("texture/game/game_element_10.png",Texture.class);
		manager.load("texture/game/game_element_11.png",Texture.class);
		manager.load("texture/game/game_element_12.png",Texture.class);
		manager.load("texture/game/game_element_13.png",Texture.class);
		manager.load("texture/game/game_element_14.png",Texture.class);
		manager.load("texture/game/game_element_15.png",Texture.class);
		manager.load("texture/game/game_element_16.png",Texture.class);
		manager.load("texture/game/game_reset_.png",Texture.class);
		manager.load("texture/game/game_reset_clicked.png",Texture.class);
		manager.load("texture/game/game_score_bg.png",Texture.class);
		manager.load("texture/game/game_element_bg.png",Texture.class);
	}

}
