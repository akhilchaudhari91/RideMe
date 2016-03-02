package ridemecabs.RateCard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ridemecabs.rideme.R;

public class TabContentActivity extends AppCompatActivity {

    TextView showText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showText = new TextView(this);
        showText.setText("See above Tab Customization");
        setContentView(showText);
    }
}
