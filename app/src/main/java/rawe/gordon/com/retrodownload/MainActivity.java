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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static String FOLDER;
    private TextView percentView1, startView1, pauseView1, resumeView1, cancelView1;
    private TextView percentView2, startView2, pauseView2, resumeView2, cancelView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        percentView1 = (TextView) findViewById(R.id.percent_1);
        startView1 = (TextView) findViewById(R.id.start_1);
        pauseView1 = (TextView) findViewById(R.id.pause_1);
        resumeView1 = (TextView) findViewById(R.id.resume_1);
        cancelView1 = (TextView) findViewById(R.id.cancel_1);
        percentView2 = (TextView) findViewById(R.id.percent_2);
        startView2 = (TextView) findViewById(R.id.start_2);
        pauseView2 = (TextView) findViewById(R.id.pause_2);
        resumeView2 = (TextView) findViewById(R.id.resume_2);
        cancelView2 = (TextView) findViewById(R.id.cancel_2);
        startView1.setOnClickListener(this);
        startView2.setOnClickListener(this);
        pauseView1.setOnClickListener(this);
        pauseView2.setOnClickListener(this);
        resumeView1.setOnClickListener(this);
        resumeView2.setOnClickListener(this);
        cancelView1.setOnClickListener(this);
        cancelView2.setOnClickListener(this);
        FOLDER = getExternalCacheDir().toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Subscribe
    public void onEvent(final ProgressEvent event) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                percentView1.setText("finished -> " + event.current + " total -> " + event.total);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_1:
                try {
                    Retroload.getInstance().startDownload("1");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.start_2:
                try {
                    Retroload.getInstance().startDownload("2");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.pause_1:
                Retroload.getInstance().pauseDownload("1");
                break;
            case R.id.pause_2:
                Retroload.getInstance().pauseDownload("2");
                break;
            case R.id.resume_1:
                Retroload.getInstance().resumeDownload("1");
                break;
            case R.id.resume_2:
                Retroload.getInstance().resumeDownload("2");
                break;
            case R.id.cancel_1:
                Retroload.getInstance().cancelDownload("1");
                break;
            case R.id.cancel_2:
                Retroload.getInstance().cancelDownload("2");
                break;
        }
    }
}
