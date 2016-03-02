package ridemecabs.RateCard;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.example.ridemecabs.rideme.R;

public class RateCardActivity extends TabActivity implements OnTabChangeListener {
    /** Called when the activity is first created. */
    TabHost tHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.tab_main);
            Resources resources = getResources();

            tHost = getTabHost();
            TabHost.TabSpec tSpec;
            Intent intent;

            intent = new Intent().setClass(this, TabContentActivity.class);

            tSpec = tHost.newTabSpec("first").setIndicator("One").setContent(intent);
            tHost.addTab(tSpec);

            intent = new Intent().setClass(this, TabContentActivity.class);
            tSpec = tHost.newTabSpec("second").setIndicator("Second").setContent(intent);
            tHost.addTab(tSpec);

            intent = new Intent().setClass(this, TabContentActivity.class);
            tSpec = tHost.newTabSpec("third").setIndicator("Third").setContent(intent);
            tHost.addTab(tSpec);

            tHost.setCurrentTab(0); // Default Selected Tab

            tHost.setOnTabChangedListener(this);

            tHost.getTabWidget().getChildAt(0).getLayoutParams().height = 40;
            tHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.WHITE);
            tHost.getTabWidget().getChildAt(1).getLayoutParams().height = 40;
            tHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.WHITE);
            tHost.getTabWidget().getChildAt(2).getLayoutParams().height = 40;
            tHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.WHITE);

            tHost.getTabWidget().getChildAt(0)
                    .setBackgroundColor(Color.rgb(00, 219, 239));

        }
        catch (Exception ex)
        {
            Log.d("Tab",ex.getMessage());
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        if (tabId.equals("first")) {
            tHost.getTabWidget().getChildAt(0)
                    .setBackgroundResource(R.drawable.tab_selector);
            tHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.WHITE);
            tHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.WHITE);
        } else if (tabId.equals("second")) {
            tHost.getTabWidget().getChildAt(1)
                    .setBackgroundResource(R.drawable.tab_selector);

            tHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.WHITE);
            tHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.WHITE);
        } else if (tabId.equals("third")) {
            tHost.getTabWidget().getChildAt(2)
                    .setBackgroundResource(R.drawable.tab_selector);
            tHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.WHITE);
            tHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.WHITE);
        }

    }

}