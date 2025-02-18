package io.github.rsookram.ten;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Insets;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.BitSet;
import java.util.Random;

public class MainActivity extends Activity {

    private final Random random = new Random();

    private GridLayout grid;

    private int[] digits;
    private BitSet selectedDigits;
    private int currentSum = 0;
    private int score = 0;

    @SuppressLint("ClickableViewAccessibility") // The view isn't clickable
    private final View.OnTouchListener gridTouchListener = (View v, MotionEvent event) -> {
        int childWidth = grid.getChildAt(0).getWidth();
        int childHeight = grid.getChildAt(0).getHeight();

        int gridRow = (int) (event.getY() / childHeight);
        int gridColumn = (int) (event.getX() / childWidth);

        int columnCount = grid.getColumnCount();
        int digitOffset = gridRow * columnCount + gridColumn;
        if (gridColumn >= columnCount || digitOffset < 0 || digitOffset >= digits.length) {
            onSelectionEnd();
            return false;
        }

        int digit = digits[digitOffset];

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentSum = digit;
                selectedDigits.clear();
                selectedDigits.set(digitOffset);
                setSelectedBackgroundColour();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!selectedDigits.get(digitOffset)) {
                    currentSum += digit;
                    selectedDigits.set(digitOffset);
                    setSelectedBackgroundColour();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                if (!selectedDigits.get(digitOffset)) {
                    currentSum += digit;
                    selectedDigits.set(digitOffset);
                }

                onSelectionEnd();
        }

        return true;
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        grid = findViewById(R.id.digits);

        int rowCount = getResources().getInteger(R.integer.row_count);
        int columnCount = grid.getColumnCount();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < rowCount; i++) {
            inflater.inflate(R.layout.row, grid, true);
        }

        int numDigits = rowCount * columnCount;
        digits = new int[numDigits];
        selectedDigits = new BitSet(numDigits);

        setUpInitialState(false);

        View resetButton = findViewById(R.id.reset);
        resetButton.setOnClickListener(v -> setUpInitialState(true));
        resetButton.setOnLongClickListener(v -> {
            setUpInitialState(false);
            return true;
        });

        grid.setOnTouchListener(gridTouchListener);

        findViewById(android.R.id.content).setOnApplyWindowInsetsListener((v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsets.Type.systemBars());
            v.setPadding(systemInsets.left, systemInsets.top, systemInsets.right, systemInsets.bottom);
            return insets;
        });
    }

    private void setUpInitialState(boolean enableTimer) {
        for (int i = 0; i < grid.getChildCount(); i++) {
            int digit = nextDigit();
            digits[i] = digit;
            ((TextView) grid.getChildAt(i)).setText(String.valueOf(digit));
        }

        setScore(0);

        resetSelectionState();

        VerticalProgressBar verticalProgress = findViewById(R.id.vertical_progress);
        verticalProgress.reset();
        if (enableTimer) {
            verticalProgress.start();
            verticalProgress.setOnComplete(() -> {
                grid.setAlpha(0.5f);
                grid.setEnabled(false);
            });
        }

        grid.setAlpha(1.0f);
        grid.setEnabled(true);
    }

    private void setSelectedBackgroundColour() {
        int colour;
        if (currentSum == 10) {
            colour = Color.GREEN;
        } else if (currentSum > 10) {
            colour = Color.RED;
        } else {
            colour = Color.LTGRAY;
        }

        for (int i = selectedDigits.nextSetBit(0); i >= 0; i = selectedDigits.nextSetBit(i + 1)) {
            grid.getChildAt(i).setBackgroundColor(colour);
        }
    }

    private void onSelectionEnd() {
        if (currentSum != 10) {
            resetSelectionState();
            return;
        }

        int additionalScore = 0;

        for (int i = selectedDigits.nextSetBit(0); i >= 0; i = selectedDigits.nextSetBit(i + 1)) {
            // 0s (empty spaces) don't count towards the score
            if (digits[i] > 0) {
                additionalScore++;
            }

            digits[i] = 0;
            ((TextView) grid.getChildAt(i)).setText("");
        }

        setScore(score + additionalScore);

        resetSelectionState();
    }

    private void resetSelectionState() {
        currentSum = 0;
        selectedDigits.clear();

        for (int i = 0; i < grid.getChildCount(); i++) {
            grid.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void setScore(int s) {
        score = s;
        ((TextView) findViewById(R.id.score)).setText(String.valueOf(s));
    }

    private int nextDigit() {
        // Bias toward smaller numbers so that there are fewer cases of having
        // digits that can't be matched up (e.g. 9 can only be matched with 1).
        // 1 - 12%
        // 2 - 14%
        // 3 - 15%
        // 4 - 13%
        // 5 - 11%
        // 6 - 11%
        // 7 - 10%
        // 8 - 9%
        // 9 - 5%
        int num = random.nextInt(100); // [0, 100)

        if (num < 12) {
            return 1;
        } else if (num < 26) {
            return 2;
        } else if (num < 41) {
            return 3;
        } else if (num < 54) {
            return 4;
        } else if (num < 65) {
            return 5;
        } else if (num < 76) {
            return 6;
        } else if (num < 86) {
            return 7;
        } else if (num < 95) {
            return 8;
        } else {
            return 9;
        }
    }
}
