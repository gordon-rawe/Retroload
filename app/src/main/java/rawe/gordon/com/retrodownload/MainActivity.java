package rawe.gordon.com.retrodownload;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;

import rawe.gordon.com.retrodownload.download.ProgressEvent;
import rawe.gordon.com.retrodownload.download.Retroload;
import rawe.gordon.com.retrodownload.download.Worker;

public class MainActivity extends AppCompatActivity {

    public static String FOLDER;
    private TextView percentView, startView, retrieveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        percentView = (TextView) findViewById(R.id.percent);
        startView = (TextView) findViewById(R.id.start_download);
        retrieveView = (TextView) findViewById(R.id.retrieve_data);
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
        retrieveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Worker.Entry> entries =Worker.getCheckList("123345");
                Toast.makeText(MainActivity.this,String.valueOf(entries.size()),Toast.LENGTH_SHORT).show();
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
