package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TableLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TableLayout table;
        table = findViewById(R.id.tableLayout);

        BlockButton[][] buttons = new BlockButton[9][9];

        // TableRows 9개 추가
        for (int i = 0; i < 9; i++) {
            TableRow tableRow = new TableRow(this);

            tableRow.setLayoutParams(
                    new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT, // 테이블 넓이 (꽉 채움)
                        TableLayout.LayoutParams.WRAP_CONTENT, // 테이블 높이 (꽉 채움)
                        1.0f
                    )
            );

            // 각 TableRow에 Button 9개 추가
            for (int j = 0; j < 9; j++) {
                buttons[i][j] = new BlockButton(this, i, j);

                TableRow.LayoutParams layoutParams =
                        new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT,
                            1.0f // 버튼 넓이 일정하게
                        );
                buttons[i][j].setLayoutParams(layoutParams);

                buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BlockButton blockButton = (BlockButton) view;
                        blockButton.breakBlock(); // Assuming breakBlock() method does not require any arguments
                    }
                });

                buttons[i][j].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        BlockButton blockButton = (BlockButton) view;
                        blockButton.toggleFlag();
                        return true; // Return true to indicate that the event was consumed.
                    }
                });
                tableRow.addView(buttons[i][j]);
            }
            table.addView(tableRow);
        }
    }
}
