package com.huahan.hhbaseutils.view.refreshlist;





import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 重写了TextView，一般不用
 * @author 陈
 *
 */
public class HandyTextView extends AppCompatTextView {

	public HandyTextView(Context context) {
		super(context);
	}

	public HandyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HandyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		if (text == null) {
			text = "";
		}
		super.setText(text, type);
	}
}
