package com.freak.labelview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freak.label.widget.FlowLayout;
import com.freak.label.view.LabelAdapter;
import com.freak.label.view.LabelFlowLayout;
import com.freak.label.widget.KingoitFlowLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LabelFlowLayout label;
    //    private LabelFlowLayout label1;
    private List<String> list;
    private KingoitFlowLayout flowLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        label = findViewById(R.id.label);
        flowLayout = findViewById(R.id.kingoit_flow_layout);

//        label1 = findViewById(R.id.label1);
        list = new ArrayList<>();
        list.add("标签1");
        list.add("标签2");
        list.add("标签3333");
        list.add("标签44");
        list.add("标签555");
        list.add("标签6666666666");
        list.add("标签7777777777777");
        list.add("标签888888");
        list.add("标签1");
        list.add("标签1");
        list.add("标签1");
        list.add("标签9999999");
        list.add("标签000000000000000");
        list.add("1");

//        label1.setAdapter(new LabelAdapter<String>(list) {
//            @Override
//            public View getView(ViewGroup parent, int position, String o) {
//                TextView textView = (TextView) LayoutInflater.from(MainActivity.this).inflate(R.layout.view_item_search_history_item,
//                        parent, false);
//                textView.setText(o);
//                return textView;
//            }
//        });
        initData();
        initView();
        label.setAdapter(new LabelAdapter<String>(list) {
            @Override
            public View getView(ViewGroup parent, int position, String o) {
//                View view =  LayoutInflater.from(MainActivity.this).inflate(R.layout.view_item,
//                        parent, false);
//                TextView textView=view.findViewById(R.id.text_view_search_history_name);
                TextView textView = (TextView) LayoutInflater.from(MainActivity.this).inflate(R.layout.view_item_search_history_item,
                        parent, false);
                textView.setText(o);
                return textView;
            }
        });
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            list.add("战争女神");
            list.add("蒙多");
            list.add("德玛西亚皇子");
            list.add("殇之木乃伊");
            list.add("狂战士");
            list.add("布里茨克拉克");
            list.add("冰晶凤凰 艾尼维亚");
            list.add("德邦总管");
            list.add("野兽之灵 乌迪尔 （德鲁伊）");
            list.add("赛恩");
            list.add("诡术妖姬");
            list.add("永恒梦魇");
        }
    }

    private void initView() {
        flowLayout.showTag(list, new KingoitFlowLayout.ItemClickListener() {
            @Override
            public void onClick(String currentSelectedkeywords, List<String> allSelectedKeywords) {

            }
        }); }


}
