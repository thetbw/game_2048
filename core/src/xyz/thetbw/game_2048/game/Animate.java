package xyz.thetbw.game_2048.game;


import java.util.ArrayList;
import java.util.List;

/**
 * 动画
 */
public abstract class Animate {

    /**
     * 动画已经进行的时长,-1 ：还未开始
     */
    private float animateTime = -1;


    private float animateDurationTime;

    private List<AnimateEndCallBack> endCallBacks = new ArrayList<>();


    public Animate(float animateDurationTime) {
        this(animateDurationTime,null);
    }

    public Animate(float animateDurationTime, AnimateEndCallBack endCallBack) {
        this.animateDurationTime = animateDurationTime;
        endCallBacks.add(endCallBack);
    }

    public void addOnAnimateEndCallBack(AnimateEndCallBack endCallBack) {
        endCallBacks.add(endCallBack);
    }

    /**
     * 开始动画
     */
    public Animate start(){
        animateTime = 0;
        return this;
    }

    /**
     * 重置动画
     */
    public Animate reset(){
        animateTime = -1;
        return this;
    }

    /**
     * 动画是否结束
     */
    public boolean isEnd(){
        return animateTime >= animateDurationTime;
    }

    public  void update(float deltaTime){
        if (isEnd()) return;
        if(animateTime >= 0){
            animateTime += deltaTime;
            if(animateTime >= animateDurationTime){
                for (AnimateEndCallBack endCallBack : endCallBacks) {
                    endCallBack.onAnimateEnd(this);
                }
                return;
            }
            updateAnimate(deltaTime,animateTime);
        }
    }

    public abstract void updateAnimate(float deltaTime,float animateTime);


    public  interface AnimateEndCallBack {
        void onAnimateEnd(Animate animate);
    }
}
