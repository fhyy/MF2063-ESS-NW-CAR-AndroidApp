package kth.ess_androidapp;

import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ControlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        
        setContentView(R.layout.activity_control);
    
        SurfaceView colorSurface = findViewById(R.id.colorSurface);
        colorSurface.setBackgroundColor(getResources().getColor(R.color.RED));
        
        colorSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor(1);
            }
        });
    }
    
    int colorIndex = 0;
    protected void changeColor(int increment){
    
        colorIndex += increment;
        while(colorIndex >= 4){
            colorIndex -= 4 ;
        }
        while (colorIndex < 0){
            colorIndex += 4;
        }
        
        SurfaceView colorSurface = findViewById(R.id.colorSurface);
        if(colorIndex == 0 || colorIndex == 2) {
            colorSurface.setBackgroundColor(getResources().getColor(R.color.RED));
        }else if (colorIndex == 1){
            colorSurface.setBackgroundColor(getResources().getColor(R.color.GREEN));
        }else if (colorIndex == 3){
            colorSurface.setBackgroundColor(getResources().getColor(R.color.YELLOW));
        }
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    changeColor(1);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    changeColor(-1);
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
}
