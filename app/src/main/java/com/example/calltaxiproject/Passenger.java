package com.example.calltaxiproject;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Passenger extends Activity {

    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger_main);

        backBtn = (Button) findViewById(R.id.backBtn2);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                finish();
            }
        });
    }
}
