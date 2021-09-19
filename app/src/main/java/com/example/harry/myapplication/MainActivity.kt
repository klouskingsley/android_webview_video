package com.example.harry.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText url_text;
    public static final String URL_MESSAGE = "com.example.harry.myapplication.URL_MESSAGE";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url_text = (EditText) findViewById(R.id.url_text);
        Button entry_button = (Button) findViewById(R.id.entry_button);
        entry_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.entry_button:
                String url = url_text.getText().toString();
                Intent intent = new Intent(this, MuduRoom.class);
                intent.putExtra(URL_MESSAGE, url);
                startActivity(intent);
        }
    }
}
