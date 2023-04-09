package com.example.project_sound_classification;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class ServiceInfoActivity extends AppCompatActivity {
    private SearchView searchViewText;
    private LinearLayout buttonLayout;
    private TextView whiteButtonCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serviceinfo);

        // 액션바 색깔을 바꾸는 코드
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.actionbar_background)));
        }

        buttonLayout = findViewById(R.id.button_layout);
        searchViewText = findViewById(R.id.searchView);

        // SearchView에 입력한 문자열을 포함하는 Q.~ 버튼들의 개수를 알려주는 코드
        int whiteButtonCount = 0;
        for (int i = 0; i < buttonLayout.getChildCount(); i++) {
            Button button = (Button) buttonLayout.getChildAt(i);
            if (button.getBackground() instanceof ColorDrawable) {
                int color = ((ColorDrawable) button.getBackground()).getColor();
                if (color == Color.WHITE) {
                    whiteButtonCount++;
                }
            }
        }
        whiteButtonCountTextView = findViewById(R.id.whiteButtonCount);
        whiteButtonCountTextView.setText("" + whiteButtonCount);

        // 제목 변경
        getSupportActionBar().setTitle("이용안내");

        //------------------------------------------------------------------------------------------------
        // 버튼들
        // 이용 안내 버튼
        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceInfoActivity.this, ServiceInfoActivity_DeveloperInfo.class);
                startActivity(intent);

            }
        });

        // 사용하는 방법 안내 버튼
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceInfoActivity.this, ServiceInfoActivity_HowToUse.class);
                startActivity(intent);

            }
        });

        // 버전 및 업데이트 버튼
        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceInfoActivity.this, ServiceInfoActivity_VersionOrUpdate.class);
                startActivity(intent);            }
        });

        // 고객센터 버튼
        Button button4 = findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceInfoActivity.this, ServiceInfoActivity_CustomerServiceCenter.class);
                startActivity(intent);               }
        });

        // 건의사항 버튼
        Button button5 = findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceInfoActivity.this, ServiceInfoActivity_Suggestion.class);
                startActivity(intent);              }
        });

        // 더보기 버튼
        Button button6 = findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "추가적인 질문사항은 추후에 추가 예정입니다.", Toast.LENGTH_LONG).show();
            }
        });

        //------------------------------------------------------------------------------------------------
        // Set OnQueryTextListener on the SearchView
        searchViewText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String searchWord = newText;
                searchWord(searchWord);
                return false;
            }
        });
    }

    // 검색란(TextView)에 입력한 문자열과 동일(및 포함)한 문자열 찾는 함수
    private void searchWord(String searchWord) {

        // Reset the button background color
        for (int i = 0; i < buttonLayout.getChildCount(); i++) {
            Button button = (Button) buttonLayout.getChildAt(i);
            button.setBackgroundColor(getResources().getColor(R.color.ServiceInfo_background));
        }

        int whiteButtonCount = 5; // set default value to 5
        String text = searchViewText.getQuery().toString().toLowerCase();
        if (!searchWord.isEmpty() && text.contains(searchWord.toLowerCase())) {
            whiteButtonCount = 0; // reset the count to 0 if a search is performed
            for (int i = 0; i < buttonLayout.getChildCount(); i++) {
                Button button = (Button) buttonLayout.getChildAt(i);
                if (button.getText().toString().toLowerCase().contains(searchWord.toLowerCase())) {
                    button.setBackgroundColor(getResources().getColor(R.color.white));
                    if (button.getBackground() instanceof ColorDrawable) {
                        int color = ((ColorDrawable) button.getBackground()).getColor();
                        if (color == Color.WHITE) {
                            whiteButtonCount++;
                        }
                    }
                }
            }
        } else if (searchWord.isEmpty()) {
            for (int i = 0; i < buttonLayout.getChildCount(); i++) {
                Button button = (Button) buttonLayout.getChildAt(i);
                button.setBackgroundColor(getResources().getColor(R.color.white));
            }
        }

        // Update the textView with the white button count
        whiteButtonCountTextView.setText("" + whiteButtonCount);

    }
}
