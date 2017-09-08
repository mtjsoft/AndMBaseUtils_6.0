package com.huahan.hhbaseutils.view.swipe;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.WrapperListAdapter;

import com.huahan.hhbaseutils.view.swipe.SwipeMenuView.OnSwipeItemClickListener;
import com.huahan.hhbaseutils.view.swipe.SwipeRefreshListView.OnMenuItemClickListener;

/**
 * 
 * @author baoyz
 * @date 2014-8-24
 * 
 */
public class SwipeMenuAdapter implements WrapperListAdapter,
		OnSwipeItemClickListener {

	private ListAdapter mAdapter;
	private Context mContext;
	private OnMenuItemClickListener onMenuItemClickListener;

	public SwipeMenuAdapter(Context context, ListAdapter adapter) {
		mAdapter = adapter;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mAdapter.getCount();
	}

	@Override
	public Object getItem(int position) {
		return mAdapter.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return mAdapter.getItemId(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SwipeMenuLayout layout = null;
		View contentView = mAdapter.getView(position, null, parent);
		Log.i("chenyuan", "swipe menu adapter position===="+position);
		if (convertView == null) {
			Log.i("chenyuan", "swipemenu  adapter position==="+position+"====="+contentView);
			SwipeMenu menu = new SwipeMenu(mContext);
			menu.setViewType(mAdapter.getItemViewType(position));
			createMenu(menu,position);
			SwipeMenuView menuView = new SwipeMenuView(menu);
//			SwipeMenuView menuView = new SwipeMenuView(menu,
//					(SwipeMenuListView) parent);
			menuView.setOnSwipeItemClickListener(this);
			SwipeRefreshListView listView = (SwipeRefreshListView) parent;
			layout = new SwipeMenuLayout(contentView, menuView,
					listView.getCloseInterpolator(),
					listView.getOpenInterpolator());
			layout.setPosition(position);

		} else {
			layout = (SwipeMenuLayout) convertView;
			layout.closeMenu();
			layout.setPosition(position);
			layout.setContentView(contentView);
//			View view = mAdapter.getView(position, layout.getContentView(),
//					parent);
		}
		boolean flag=changeMenu(layout.getSwipeMenuView().getSwipeMenu(),position);
		if (!flag) 
		{
			SwipeMenu swipeMenu = layout.getSwipeMenuView().getSwipeMenu();
			if (swipeMenu.getMenuItems()!=null&&swipeMenu.getMenuItems().size()>0)
			{
				Log.i("test", position+"重新绘制显示的内容:"+layout.getSwipeMenuView().getSwipeMenu().getMenuItem(0).getTitle());
				LinearLayout linearLayout=(LinearLayout) layout.getSwipeMenuView().getChildAt(0);
				View childAt = linearLayout.getChildAt(0);
				if (childAt instanceof TextView)
				{
					Log.i("test", "重新设置显示的文本");
					TextView textView=(TextView) childAt;
					textView.setText(layout.getSwipeMenuView().getSwipeMenu().getMenuItem(0).getTitle());
				}
			}
		}
		return layout;
	}
	/**
	 * 是否改变Menu。true不改变，false改变
	 * @param menu
	 * @param position
	 * @return
	 */
	public boolean  changeMenu(SwipeMenu menu,int position)
	{
		return true;
	}
	public void createMenu(SwipeMenu menu,int posi) {
		// Test Code
		SwipeMenuItem item = new SwipeMenuItem(mContext);
		item.setTitle("Item 1");
		item.setBackground(new ColorDrawable(Color.GRAY));
		item.setWidth(300);
		menu.addMenuItem(item);

		item = new SwipeMenuItem(mContext);
		item.setTitle("Item 2");
		item.setBackground(new ColorDrawable(Color.RED));
		item.setWidth(300);
		menu.addMenuItem(item);
	}

	@Override
	public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
		if (onMenuItemClickListener != null) {
			onMenuItemClickListener.onMenuItemClick(view.getPosition(), menu,
					index);
		}
	}

	public void setOnMenuItemClickListener(
			OnMenuItemClickListener onMenuItemClickListener) {
		this.onMenuItemClickListener = onMenuItemClickListener;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		mAdapter.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		mAdapter.unregisterDataSetObserver(observer);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return mAdapter.areAllItemsEnabled();
	}

	@Override
	public boolean isEnabled(int position) {
		return mAdapter.isEnabled(position);
	}

	@Override
	public boolean hasStableIds() {
		return mAdapter.hasStableIds();
	}

	@Override
	public int getItemViewType(int position) {
		return mAdapter.getItemViewType(position);
	}

	@Override
	public int getViewTypeCount() {
		return mAdapter.getViewTypeCount();
	}

	@Override
	public boolean isEmpty() {
		return mAdapter.isEmpty();
	}

	@Override
	public ListAdapter getWrappedAdapter() {
		return mAdapter;
	}

}
