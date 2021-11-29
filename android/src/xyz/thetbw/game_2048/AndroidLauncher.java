package xyz.thetbw.game_2048;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import xyz.thetbw.game_2048.MyGame;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AndroidLauncher extends AndroidApplication implements FileChoose{
	private FileCallback fileCallback;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		config.useWakelock =true;
//		config.hideStatusBar = true;
		initialize(new MyGame(this), config);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

			if (data != null) {
				final Uri uri = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = getContext().openFileOutput("background", MODE_PRIVATE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    byte[] temp = new byte[1024];
                    while (inputStream.read(temp) != -1) {
                        outputStream.write(temp);
                        outputStream.flush();
                    }
                    inputStream.close();
                    outputStream.close();
                    if (fileCallback != null)
                        fileCallback.onFileChoosed(null);
                    Toast.makeText(this,"重启生效",Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
			}
		}
	}

	@Override
	public void getFile(String fileType, FileCallback callback) {
		this.fileCallback = callback;
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.setType("image/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, 1);

	}
}
