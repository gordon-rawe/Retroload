package rawe.gordon.com.retrodownload;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import rawe.gordon.com.retrodownload.download.Retroload;
import rawe.gordon.com.retrodownload.download.WorkGroup;

public class MainActivity extends AppCompatActivity {

    public static String FOLDER;
    private TextView percentView, startView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        percentView = (TextView) findViewById(R.id.percent);
        startView = (TextView) findViewById(R.id.start_download);
        FOLDER = getExternalCacheDir().toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownload();
            }
        });
    }

    private void startDownload() {
        try {
            Retroload.getInstance().startDownload("123345", new WorkGroup.ProgressListener() {
                @Override
                public void callbackPercent(final int finished, final int total) {
                    Log.d("Retroload", "finished -> " + finished + " total -> " + total);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            percentView.setText("finished -> " + finished + " total -> " + total);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pauseDownload() {

    }

    private void resumeDownload() {

    }

    private void cancelDownload() {

    }
}
