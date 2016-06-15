package com.example.p4;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PuzzleActivity extends Activity implements OnClickListener {
	private int getShuffleCount()
	{
		return 100 * mNumRows * mNumCols;
		//return 1;
	}

	private Pattern inputPattern = Pattern.compile("(\\d+) (\\d+)");
	
	private int mNumRows;
	private int mNumCols;
	
	private int mZeroRow;
	private int mZeroCol;
	
	private int mIndex[];
	private Button mButton[];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_puzzle);

		Button button = (Button) findViewById(R.id.makeButton);
		button.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {		
		if (view.equals(findViewById(R.id.makeButton))) {
			startNewGame();
			return;
		}

		for (int i = 0; i < mNumRows; ++i) {
			for (int j = 0; j < mNumCols; ++j) {
				if (view.equals(mButton[i * mNumCols + j])) {
					movePuzzle(i, j);
				}
			}
		}
	}

	private void movePuzzle(int row, int col) {
		final int dX[] = { 0, 0, 1, -1 };
		final int dY[] = { -1, 1, 0, 0 };

		for (int i = 0; i < dX.length; ++i) {
			if(row + dX[i] == mZeroRow && col + dY[i] == mZeroCol)
			{
				mIndex[mZeroRow * mNumCols + mZeroCol] = mIndex[row * mNumCols + col];
				mIndex[row * mNumCols + col] = 0;
	
				mZeroRow = row;
				mZeroCol = col;
				
				break;
			}
		}
		
		updateButtons();
		if(checkEnd()) finish();
	}

	private void startNewGame()
	{
		LinearLayout container = (LinearLayout)findViewById(R.id.container);
		container.removeAllViews();
		
		if(!getInput())
		{
			TextView txtError = new TextView(this);
			txtError.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			txtError.setText(R.string.wrongArguments);
			container.addView(txtError);
			return;
		}
		
		calcShuffledIndex();
		mButton = new Button[mIndex.length];
		
		LinearLayout.LayoutParams lineParam = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f);
		LinearLayout.LayoutParams buttonParam = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
		
		for(int i = 0; i < mNumRows; ++i)
		{
			LinearLayout line = new LinearLayout(this);
			line.setLayoutParams(lineParam);
			line.setOrientation(LinearLayout.HORIZONTAL);
			
			for(int j = 0; j < mNumCols; ++j)
			{
				int buttonIdx = i * mNumCols + j; 
				
				mButton[buttonIdx] = new Button(this);
				mButton[buttonIdx].setLayoutParams(buttonParam);
				mButton[buttonIdx].setOnClickListener(this);
				line.addView(mButton[buttonIdx]);
			}
			
			container.addView(line);
		}
		
		updateButtons();
	}

	private boolean checkEnd()
	{
		for(int i = 1; i < mIndex.length; ++i)
		{
			if(mIndex[i - 1] != i) return false;
		}
		
		return true;
	}
	
	private void updateButtons() {
		for (int i = 0; i < mNumRows; ++i) {
			for (int j = 0; j < mNumCols; ++j) {
				int buttonIdx = i * mNumCols + j;

				if (mIndex[buttonIdx] != 0) {
					mButton[buttonIdx].setText(Integer.toString(mIndex[buttonIdx]));
					mButton[buttonIdx].setBackgroundResource(android.R.drawable.btn_default);
				}
				else {
					mButton[buttonIdx].setText("");
					mButton[buttonIdx].setBackgroundColor(0xFF000000);
				}
			}
		}
	}

	private boolean getInput() {
		EditText input = (EditText)findViewById(R.id.puzzleInput);
		Matcher m = inputPattern.matcher(input.getText().toString());
		
		if(m.matches())
		{
			mNumRows = Integer.parseInt(m.group(1));
			mNumCols = Integer.parseInt(m.group(2));
			
			if(mNumRows <= 0 || mNumCols <= 0) return false;
			if(mNumRows == 1 && mNumCols == 1) return false;
			
			return true;
		}
		
		return false;
	}

	private void shuffleOnce()
	{
		final int dX[] = { 0, 0, 1, -1 };
		final int dY[] = { -1, 1, 0, 0 };
		
		int nextRow;
		int nextCol;
		
		while(true)
		{
			int dir = (int) (Math.random() * 4);
			
			nextRow = mZeroRow + dX[dir];
			nextCol = mZeroCol + dY[dir];
	
			if (nextRow < 0 || nextCol < 0) continue;
			if (mNumRows <= nextRow || mNumCols <= nextCol) continue;
			
			break;
		}

		mIndex[mZeroRow * mNumCols + mZeroCol] = mIndex[nextRow * mNumCols + nextCol];
		mIndex[nextRow * mNumCols + nextCol] = 0;
		
		mZeroRow = nextRow;
		mZeroCol = nextCol;
	}
	
	private void calcShuffledIndex() {
		mIndex = new int[mNumRows * mNumCols];

		for (int i = 1; i < mIndex.length; ++i) mIndex[i - 1] = i;
		mIndex[mIndex.length - 1] = 0;

		mZeroRow = mNumRows - 1;
		mZeroCol = mNumCols - 1;

		int remain = getShuffleCount();
		while (0 < remain) {
			shuffleOnce();
			--remain;
		}

		if(checkEnd()) shuffleOnce();
	}

}
