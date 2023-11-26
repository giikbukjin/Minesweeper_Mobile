package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final BlockButton[][] buttons = new BlockButton[9][9];
    private boolean gameOver = false;
    private final int mineCount = 10;
    private int blocksLeft = 81 - mineCount; // 지뢰가 아닌 블록 수

    private ToggleButton toggleButton; // 토글 버튼 추가
    private TextView textView; // 지뢰 수를 표시할 텍스트뷰 추가
    private LinearLayout gameOverDialog;
    private TextView gameStatusText;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleButton = findViewById(R.id.toggleButton); // 토글 버튼 초기화
        textView = findViewById(R.id.textView); // 텍스트뷰 초기화
        textView.setText("Mines : " + mineCount); // 시작 시 지뢰 수 설정
        gameOverDialog = findViewById(R.id.gameOverDialog);
        Button restartButton = findViewById(R.id.restartButton);
        TableLayout table = findViewById(R.id.tableLayout);
        gameStatusText = findViewById(R.id.gameStatusText);

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

                buttons[i][j].setOnClickListener(view -> {
                    BlockButton blockButton = (BlockButton) view;
                    if (toggleButton.isChecked()) {
                        blockButton.toggleFlag();
                        updateMineDisplay();
                    } else {
                        if (!gameOver && !blockButton.isFlag()) {
                            boolean mineHit = blockButton.breakBlock();
                            if (mineHit) {
                                gameOver = true;
                                showGameOver();
                                disableAllButtons();
                            } else if (blockButton.getNeighborMines() == 0) {
                                openAdjacentBlocks((int) blockButton.getX(), (int) blockButton.getY());
                            }
                            if (checkWinCondition()) {
                                gameOver = true;
                                showWin();
                                disableAllButtons();
                            }
                        }
                    }
                });
                tableRow.addView(buttons[i][j]);
            }
            table.addView(tableRow);
        }
        placeMines(buttons);
        calculateNeighborMines(buttons);

        restartButton.setOnClickListener(v -> restartGame());
    }

    private void placeMines(BlockButton[][] buttons) {
        Random random = new Random();
        HashSet<Integer> minePositions = new HashSet<>();

        while (minePositions.size() < 10) {
            int position = random.nextInt(81); // 81개 버튼 중 10개 선택
            minePositions.add(position);
        }

        for (int position : minePositions) {
            int i = position / 9;
            int j = position % 9;
            buttons[i][j].setMine(true);
        }
    }

    private void calculateNeighborMines(BlockButton[][] buttons) {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                if (!buttons[i][j].isMine()) {
                    int mines = countMinesAround(buttons, i, j);
                    buttons[i][j].setNeighborMines(mines);
                }
            }
        }
    }

    private int countMinesAround(BlockButton[][] buttons, int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = x + i;
                int newY = y + j;

                if (newX >= 0 && newX < 9 && newY >= 0 && newY < 9 && buttons[newX][newY].isMine()) {
                    count++;
                }
            }
        }
        return count;
    }

    private void breakBlock(BlockButton blockButton) {
        if (blockButton.isFlag() || gameOver) {
            return; // 깃발이 꽂힌 블록이거나 게임 오버 상태면 아무 작업도 하지 않음
        }

        if (blockButton.isMine()) {
            // 지뢰를 클릭했을 경우 게임 오버 처리
            gameOver = true;
            blockButton.setText("*");
            showGameOver();
            disableAllButtons();
        } else {
            // 지뢰가 아닌 블록을 클릭했을 경우
            int neighborMines = blockButton.getNeighborMines();
            blockButton.breakBlock(); // BlockButton 클래스의 메서드 호출
            blocksLeft--;

            if (neighborMines == 0) {
                // 주변 지뢰 수가 0일 경우 주변 블록 열기
                openAdjacentBlocks((int) blockButton.getX(), (int) blockButton.getY());
            } else {
                blockButton.setText(String.valueOf(neighborMines));
            }

            if (blocksLeft == 0) {
                // 모든 비지뢰 블록을 열었을 경우 승리 처리
                gameOver = true;
                showWin();
                disableAllButtons();
            }
        }
    }

    private void openAdjacentBlocks(int x, int y) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = x + i;
                int newY = y + j;

                if (newX >= 0 && newX < 9 && newY >= 0 && newY < 9 && buttons[newX][newY].isClickable()) {
                    // 재귀적으로 인접 블록 열기
                    breakBlock(buttons[newX][newY]);
                }
            }
        }
    }

    private void disableAllButtons() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                buttons[i][j].setClickable(false);
            }
        }
    }

    // 게임 오버 메시지를 표시하는 메서드
    @SuppressLint("SetTextI18n")
    private void showGameOver() {
        gameStatusText.setText("Game Over"); // 게임 오버 텍스트 설정
        gameOverDialog.setVisibility(View.VISIBLE);
    }

    // 승리 메시지를 표시하는 메서드
    @SuppressLint("SetTextI18n")
    private void showWin() {
        gameStatusText.setText("Win"); // 승리 텍스트 설정
        gameOverDialog.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    private void updateMineDisplay() {
        int minesLeft = mineCount - BlockButton.flags;
        textView.setText("Mines : " + minesLeft);
    }

    private boolean checkWinCondition() {
        int flaggedMines = 0;
        int oppenedBlocks = 0;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                BlockButton button = buttons[i][j];
                if (button.isMine() && button.isFlag()) {
                    flaggedMines++;
                }
                if (!button.isMine() && !button.isClickable()) {
                    oppenedBlocks++;
                }
            }
        }
        return (flaggedMines == mineCount && oppenedBlocks == (81 - mineCount));
    }

    private void restartGame() {
        gameOverDialog.setVisibility(View.GONE); // 다이얼로그 숨기기
        gameOver = false;
        blocksLeft = 81 - mineCount;
        BlockButton.flags = 0; // 깃발 수 초기화
        BlockButton.blocks = 81; // 블록 수 초기화

        // 모든 버튼을 초기화하고 새 게임을 설정
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                buttons[i][j].reset();
            }
        }
        placeMines(buttons); // 지뢰 재배치
        calculateNeighborMines(buttons); // 주변 지뢰 수 재계산
        updateMineDisplay(); // 지뢰 표시 업데이트
    }
}
