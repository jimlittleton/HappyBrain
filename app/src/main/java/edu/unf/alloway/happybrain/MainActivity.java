package edu.unf.alloway.happybrain;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);

        // Load the template for now
        setContentView(R.layout.template_post);

        // Makes the link in the text view clickable
        TextView webLink = findViewById(R.id.web_link);
        webLink.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
