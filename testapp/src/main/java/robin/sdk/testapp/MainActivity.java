package robin.sdk.testapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import robin.sdk.hotfix.RobinClient;
import robin.sdk.hotfix.RobinService;
import robin.sdk.hotfix.SdkListener;

public class MainActivity extends AppCompatActivity {
    RobinClient robinClient = new RobinClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                robinClient.start(MainActivity.this, new SdkListener() {
                    @Override
                    public void onSuccess(String data) {

                    }
                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        robinClient.stop(this);
        super.onDestroy();
    }
}
