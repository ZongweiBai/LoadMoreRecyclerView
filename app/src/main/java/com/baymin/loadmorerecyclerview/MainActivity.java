package com.baymin.loadmorerecyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.btn_linear_layout)
    Button btnLinearLayout;
    @Bind(R.id.btn_grid_layout)
    Button btnGridLayout;
    @Bind(R.id.btn_staggered_grid)
    Button btnStaggeredGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        btnLinearLayout.setOnClickListener(myOnClickListener);
        btnGridLayout.setOnClickListener(myOnClickListener);
        btnStaggeredGrid.setOnClickListener(myOnClickListener);
    }

    View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent;
            switch (view.getId()) {
                case R.id.btn_linear_layout:
                    intent = new Intent(MainActivity.this, LinearLayoutActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_grid_layout:
                    intent = new Intent(MainActivity.this, GridLayoutActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_staggered_grid:
                    intent = new Intent(MainActivity.this, StaggeredGridActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

}
