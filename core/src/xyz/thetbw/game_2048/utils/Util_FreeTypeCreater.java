package xyz.thetbw.game_2048.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Util_FreeTypeCreater {
    private static Util_FreeTypeCreater ourInstance = new Util_FreeTypeCreater();
    public static Util_FreeTypeCreater getInstance() {
        return ourInstance;
    }
    public static String font = "font/FZHTJW.TTF";
    FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    private Util_FreeTypeCreater() {

    }

    public void init(){
        generator =  new FreeTypeFontGenerator(Gdx.files.internal(Util_FreeTypeCreater.font));
    }

    public BitmapFont createBitmapFont(String text, int size, Color color,int border){
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        if(color!=null)
            parameter.color =color;
        else
            parameter.color = new Color(0,0,0,1);
        parameter.borderWidth = border;
        parameter.characters = text;

        BitmapFont bitmapFont = generator.generateFont(parameter);
        parameter=null;
        return bitmapFont;
    }
}
