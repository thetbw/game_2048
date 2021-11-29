package xyz.thetbw.game_2048;

public interface FileChoose {


    public void getFile(String fileType,FileCallback callback);

    interface FileCallback{
        void onFileChoosed(String path);
    }

}
