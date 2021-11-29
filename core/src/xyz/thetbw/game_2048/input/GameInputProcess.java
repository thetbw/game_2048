package xyz.thetbw.game_2048.input;

import com.badlogic.gdx.input.GestureDetector;
import xyz.thetbw.game_2048.game.GameRegion;

public class GameInputProcess extends GestureDetector {

    public GameInputProcess(GameRegion region){
        super(20,0.0f,1.0f,0.3f,region);

    }


}
