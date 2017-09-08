package com.huahan.hhbaseutils.ui;

import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.huahan.hhbaseutils.HHTipUtils;
import com.huahan.hhbaseutils.HHViewHelper;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.model.HHLoadState;
import com.huahan.hhbaseutils.view.refreshlist.HHRefreshListView;
import com.huahan.hhbaseutils.view.refreshlist.HHRefreshListView.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;
/**
 * 基本的显示列表的页面<br/>
 *FLAG_LOAD_MORE：是否可以加载更多，默认为true<br/>
 *FLAG_REFRESH:是否可以下拉刷新，默认为true<br/>
 *FLAG_TITLE：界面显示的标题<br/>
 * @author yuan
 * @param <T>
 */
public abstract class HHBaseRefreshListViewActivity<T> extends HHBaseDataActivity implements OnRefreshListener,OnScrollListener,OnItemClickListener
{
	/**
	 * 是否加载更多
	 */
	public static final String FLAG_LOAD_MORE="load_more";
	/**
	 * 是否可以下拉刷新
	 */
	public static final String FLAG_REFRESH="refresh";
	/**
	 * 界面显示的标题
	 */
	public static final String FLAG_TITLE="title";
	//获取listview显示数据的发送消息的what
	public static final int GET_LIST_DATA=1000;
	//用于显示数据的listview
	private HHRefreshListView mListView;
//	private HHRefreshListView mListView;
	//ListView显示的数据
	private List<T> mList;
	//用于临时保存ListView显示的数据
	private List<T> mTempList;
	//listview的adapter
	private BaseAdapter mAdapter;
	//当前获取的是第几页的数据，当前可见的数据的数量，当前页获取的数据的条数
	private int mPageIndex=1,mVisibleCount=0,mPageCount=0;
	//时候加载更多
	private boolean mIsLoadMore=true;
	//时候下拉刷新
	private boolean mRefresh=true;
	//ListView的FooterView
	private View mFooterView;
	
	@Override
	public boolean initOnCreate()
	{
		loadActivityInfo();
		return false;
	}
	/**
	 * 加载Activity的数据，该方法在initOnCreate中执行,主要用于设置Activity的一些信息
	 */
	protected abstract void loadActivityInfo();
	@Override
	public void onPageLoad()
	{
		getListData();
	}
	@Override
	public View initView()
	{
		View view=View.inflate(getPageContext(), R.layout.hh_activity_refresh_listview, null);
		mListView=HHViewHelper.getViewByID(view, R.id.hh_lv_base);
		return view;
	}
	@Override
	public void initValues()
	{
		mIsLoadMore=getIntent().getBooleanExtra(FLAG_LOAD_MORE, true);
		mRefresh=getIntent().getBooleanExtra(FLAG_REFRESH, true);
	}
	@Override
	public void initListeners()
	{
		mListView.setOnItemClickListener(this);
		if (mRefresh)
		{
			mListView.setOnRefreshListener(this);
		}
		mListView.setOnScrollListener(this);
	}
	@Override
	public void processHandlerMsg(Message msg)
	{
		switch (msg.what)
		{
		case GET_LIST_DATA:
			if (mListView!=null)
			{
				mListView.onRefreshComplete();
			}
			if (mFooterView!=null&&mListView.getFooterViewsCount()>0&&getPageSize()!=mPageCount)
			{
				mListView.removeFooterView(mFooterView);
			}
			if (mTempList==null)
			{
				changeLoadState(HHLoadState.FAILED);
			}else if (mTempList.size()==0)
			{
				if (mPageIndex==1)
				{
					changeLoadState(HHLoadState.NODATA);
				}else {
					HHTipUtils.getInstance().showToast(getPageContext(), R.string.hh_no_data);
				}
			}else {
				changeLoadState(HHLoadState.SUCCESS);
				if (mPageIndex==1)
				{
					if (mList==null)
					{
						mList=new ArrayList<T>();
					}else {
						mList.clear();
					}
					mList.addAll(mTempList);
					mAdapter=instanceAdapter(mList);
					if(mIsLoadMore&&mPageCount==getPageSize()&&mListView.getFooterViewsCount()==0)
					{
						if (mFooterView==null)
						{
							mFooterView=View.inflate(getPageContext(), R.layout.hh_include_footer, null);
						}
						mListView.addFooterView(mFooterView);
					}
					mListView.setAdapter(mAdapter);
				}else {
					mList.addAll(mTempList);
					mAdapter.notifyDataSetChanged();
				}
			}
			break;

		default:
			break;
		}
	}
	/**
	 * 获取listView显示的数据
	 */
	private void getListData()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				mTempList=getListDataInThread(mPageIndex);
				mPageCount=mTempList==null?0:mTempList.size();
				sendHandlerMessage(GET_LIST_DATA);
			}
		}).start();
	}
	/**
	 * 在线程中获取listview显示的数据，该方法会在线程中调用
	 * @param pageIndex			当前获取的是第几页的数据
	 * @return
	 */
	protected abstract List<T> getListDataInThread(int pageIndex);
	/**
	 * 实例化一个Adapter
	 * @param list			listView显示的数据的集合
	 * @return
	 */
	protected abstract BaseAdapter instanceAdapter(List<T> list);
	/**
	 * 获取当前页每页获取的数据的大小
	 * @return
	 */
	protected abstract int getPageSize();
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
		if (mIsLoadMore&&mPageCount==getPageSize()&&mVisibleCount==mAdapter.getCount()&&scrollState==OnScrollListener.SCROLL_STATE_IDLE)
		{
			++mPageIndex;
			getListData();
		}
	}
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
	{
		// TODO Auto-generated method stub
		mListView.setFirstVisibleItem(firstVisibleItem);
		mVisibleCount=firstVisibleItem+visibleItemCount-mListView.getFooterViewsCount()-mListView.getHeaderViewsCount();
	}
	/**
	 * 下载刷新的时候执行的代码
	 */
	@Override 
	public void onRefresh()
	{
		mPageIndex=1;
		getListData();
	}
	/**
	 * 获取当前页面的数据
	 * @return
	 */
	protected List<T> getPageDataList()
	{
		return mList;
	}
	/**
	 * 当前页面显示的ListView
	 * @return
	 */
	protected HHRefreshListView getPageListView()
	{
		return mListView;
	}
	/**
	 * 设置页码
	 * @param pageIndex
	 */
	public void setPageIndex(int pageIndex)
	{
		this.mPageIndex=pageIndex;
	}
	/**
	 * 当前页码
	 * @return
	 */
	public int getPageIndex()
	{
		return this.mPageIndex;
	}
	
}
