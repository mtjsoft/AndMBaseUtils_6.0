package com.huahan.hhbaseutils.adapter;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class HHMultiItemRowListAdapter implements WrapperListAdapter {
    private final ListAdapter mAdapter;
    private final int mItemsPerRow;
    private final int mCellSpacing;
    private final WeakReference<Context> mContextReference;
    private final LinearLayout.LayoutParams mItemLayoutParams;
    private final AbsListView.LayoutParams mRowLayoutParams;
    private OnItemClickListener listener;
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    public HHMultiItemRowListAdapter(Context context, ListAdapter adapter, int itemsPerRow, int cellSpacing,OnItemClickListener listener) {
        if (itemsPerRow <= 0) {
            throw new IllegalArgumentException("Number of items per row must be positive");
        }
        mContextReference = new WeakReference<Context>(context);
        mAdapter = adapter;
        mItemsPerRow = itemsPerRow;
        mCellSpacing = cellSpacing;
        this.listener=listener;
        int height=LayoutParams.MATCH_PARENT;
//        if (adapter!=null&&adapter.getCount()>0)
//		{
//			height=adapter.getView(0, null,null).getLayoutParams().height;
//		}
        mItemLayoutParams = new LinearLayout.LayoutParams(0, height);
        mItemLayoutParams.setMargins(cellSpacing, cellSpacing, 0, 0);
        mItemLayoutParams.weight = 1;
        mRowLayoutParams = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean isEmpty() {
        return (mAdapter == null || mAdapter.isEmpty());
    }

    public int getItemsPerRow() {
        return mItemsPerRow;
    }

    @Override
    public int getCount() {
        if (mAdapter != null) {
            return (int)Math.ceil(1.0f * mAdapter.getCount() / mItemsPerRow);
        }
        return 0;
    }

    @Override
    public boolean areAllItemsEnabled() {
        if (mAdapter != null) {
            return mAdapter.areAllItemsEnabled();
        } else {
            return true;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        if (mAdapter != null) {
            // the cell is enabled if at least one item is enabled
            boolean enabled = false;
            for (int i = 0; i < mItemsPerRow; ++i) {
                int p = position * mItemsPerRow + i;
                if (p < mAdapter.getCount()) {
                    enabled |= mAdapter.isEnabled(p);
                }
            }
            return enabled;
        }
        return true;
    }

    @Override
    public Object getItem(int position) {
        if (mAdapter != null) {
            List<Object> items = new ArrayList<Object>(mItemsPerRow);
            for (int i = 0; i < mItemsPerRow; ++i) {
                int p = position * mItemsPerRow + i;
                if (p < mAdapter.getCount()) {
                    items.add(mAdapter.getItem(p));
                }
            }
            return items;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mAdapter != null) {
            return position;
        }
        return -1;
    }

    @Override
    public boolean hasStableIds() {
        if (mAdapter != null) {
            return mAdapter.hasStableIds();
        }
        return false;
    }

    @Override
    public View getView(int position, View convertView,final ViewGroup parent) {
        Context c = mContextReference.get();
        if (c == null || mAdapter == null) return null;

        LinearLayout view = null;
        if (convertView == null
                || !(convertView instanceof LinearLayout)
                || !((Integer)convertView.getTag()).equals(mItemsPerRow)) {
            // create a linear Layout
            view = new LinearLayout(c);
            view.setPadding(0, 0, mCellSpacing, 0);
            view.setLayoutParams(mRowLayoutParams);
            view.setOrientation(LinearLayout.HORIZONTAL);
            view.setBaselineAligned(false);
            view.setTag(Integer.valueOf(mItemsPerRow));
        } else {
            view = (LinearLayout) convertView;
        }

        for (int i = 0; i < mItemsPerRow; ++i) 
        {
        	//获取到即将显示的View
            View subView = i < view.getChildCount() ? view.getChildAt(i) : null;
            //计算View的位置
            final int p = position * mItemsPerRow + i;

            View newView = subView;
            if (p < mAdapter.getCount()) 
            {
            	if (subView instanceof PlaceholderView){
            		view.removeView(subView);
            		subView = null;
            	}
                newView = mAdapter.getView(p, subView, view);
            } else if (subView == null || !(subView instanceof PlaceholderView)) {
                newView = new PlaceholderView(c);
            }
            
            if (newView != subView || i >= view.getChildCount()) {
                if (i < view.getChildCount()) {
                    view.removeView(subView);
                }
                newView.setLayoutParams(mItemLayoutParams);
                view.addView(newView, i);
            }
            if (listener!=null&&!(newView instanceof PlaceholderView))
			{
            	final View clickView=newView;
				newView.setOnClickListener(new OnClickListener()
				{
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Log.i("chenyuan", "执行点击事件======");
						listener.onItemClick(null, clickView, p, p);
					}
				});
			}else {
				newView.setOnClickListener(null);
			}
        }

        return view;
    }
    public void setOnItemClickListener(OnItemClickListener listener)
    {
    	this.listener=listener;
    }
    public OnItemClickListener getOnItemClickListener()
    {
    	return listener;
    }
    
    @Override
    public int getItemViewType(int position) {
        if (mAdapter != null) {
            return mAdapter.getItemViewType(position);
        }

        return -1;
    }

    @Override
    public int getViewTypeCount() {
        if (mAdapter != null) {
            return mAdapter.getViewTypeCount();
        }
        return 1;
    }

//    @Override
//    public void registerDataSetObserver(DataSetObserver observer) {
//        if (mAdapter != null) {
//            mAdapter.registerDataSetObserver(observer);
//        }
//    }
//
//    @Override
//    public void unregisterDataSetObserver(DataSetObserver observer) {
//        if (mAdapter != null) {
//            mAdapter.unregisterDataSetObserver(observer);
//        }
//    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }
    @Override
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

    public static class PlaceholderView extends View {

        public PlaceholderView(Context context) {
            super(context);
        }

    }
}