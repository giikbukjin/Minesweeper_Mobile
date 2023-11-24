package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Button;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

@SuppressLint("AppCompatCustomView")
public class BlockButton extends Button {
    private int x, y; // 버튼의 좌표
    private boolean mine; // 지뢰인지 아닌지 표시
    private boolean flag; // 깃발이 꽂혔는지 표시
    private int neighborMines; // 블록 주변의 지뢰 수

    public static int flags = 0; // 깃발이 꽂힌 블록 수
    public static int blocks = 0; // 남은 블록 수

    public BlockButton(Context context, int x, int y) {
        super(context); // 부모의 생성자 호출
        this.x = x; // 필드 초기화
        this.y = y;
        this.mine = false;
        this.flag = false;
        this.neighborMines = 0;

        blocks++; // 남은 블록 수 증가

        LinearLayout.LayoutParams params = // LayoutParameter 설정
                new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                );
        params.weight = 1;
        setLayoutParams(params);
    }

    public void toggleFlag() { // 깃발 꽂기 or 해제 메소드
        if (!flag) { // 깃발이 없는 상태
            flag = true;
            flags++; // 깃발 꽂기
            setText("*"); // 깃발 표시로 '*' 문자 사용
        } else {
            flag = false;
            flags--; // 깃발 해제
            setText("");
        }
    }

    public boolean breakBlock() { // 블록 열기 메소드
        setClickable(false); // 블록을 클릭 안되는 상태로 변경
        blocks--; // 남은 블록 수 감소

        if (mine) { // 블록이 지뢰라면
            setText("*");
            return true;
        } else { // 블록이 지뢰가 아니라면
            if (neighborMines > 0) {
                setText(String.valueOf(neighborMines)); // 그 블록 주변 지뢰 수 표시
            } else {
                setText(""); // 열린 블록으로 표시
            }
            return false;
        }
    }
}