package rawe.gordon.com.retrodownload;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import rawe.gordon.com.retrodownload.download.ProgressEvent;
import rawe.gordon.com.retrodownload.download.Retroload;

public class MainActivity extends AppCompatActivity {

    public static String FOLDER;
    private TextView percentView, startView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
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
            Retroload.getInstance().startDownload("123345");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onEvent(final ProgressEvent event) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                percentView.setText("finished -> " + event.current + " total -> " + event.total);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void pauseDownload() {

    }

    private void resumeDownload() {

    }

    private void cancelDownload() {

    }
}
