package com.freak.labelview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.freak.label.FlowLayout;
import com.freak.label.LabelAdapter;
import com.freak.label.LabelFlowLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LabelFlowLayout label;
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        label = findViewById(R.id.label);
        list=new ArrayList<>();
        list.add("标签1");
        list.add("标签2");
        list.add("标签3333");
        list.add("标签44");
        list.add("标签555");
        list.add("标签1");
        list.add("标签6666666666");
        list.add("标签7777777777777");
        list.add("标签888888");
        list.add("标签9999999");
        list.add("标签000000000000000");
        label.setAdapter(new LabelAdapter<String>(list) {
            @Override
            public View getView(FlowLayout parent, int position, String o) {
                TextView textView = (TextView) LayoutInflater.from(MainActivity.this).inflate(R.layout.view_item_search_history_item,
                        parent, false);
                textView.setText(o);
                return textView;
            }
        });
    }
}
