package robin.sdk.testapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import robin.sdk.hotfix.RobinClient;
import robin.sdk.hotfix.RobinService;
import robin.sdk.hotfix.SdkListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RobinClient robinClient = new RobinClient();
        robinClient.start(this, new SdkListener() {
            @Override
            public void onSuccess(String data) {
                ((TextView)findViewById(R.id.txt)).setText(data);
            }
        });
    }
}
