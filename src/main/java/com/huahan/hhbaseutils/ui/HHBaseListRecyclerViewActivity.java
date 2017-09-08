package com.huahan.hhbaseutils.ui;

import android.graphics.Rect;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.huahan.hhbaseutils.HHDensityUtils;
import com.huahan.hhbaseutils.HHTipUtils;
import com.huahan.hhbaseutils.R;
import com.huahan.hhbaseutils.adapter.HHBaseRecyclerViewAdapter;
import com.huahan.hhbaseutils.imp.HHCallBackImp;
import com.huahan.hhbaseutils.model.HHLoadState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android.mtj on 2017/4/1.
 */

public abstract class HHBaseListRecyclerViewActivity<T> extends
        HHBaseDataActivity {
    // 获取listview显示数据的发送消息的what
    public static final int GET_LIST_DATA = 1000;
    private int mark = 2;// 【0：LinearLayoutManager 1：GridLayoutManager
    // 2：StaggeredGridLayoutManager】
    private int pager = 1;
    private int pager_size = 10;
    private int mLastVisibleItem;
    private List<T> list = new ArrayList<>();
    private List<T> temp = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private HHBaseRecyclerViewAdapter<T> adapter;
    //
    private int count = 2;// 列数，默认为每行2列
    private View footView;
    //
    private boolean isSwipeRefresh = false;
    //
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    //
    private boolean load_more = true;// 是否需要加载更多功能
    private boolean refresh = true;// 是否需要下拉功能

    @Override
    public void onPageLoad() {
        // TODO Auto-generated method stub
        getListData();
    }

    @Override
    public boolean initOnCreate() {
        // TODO Auto-generated method stub
        loadActivityInfo();
        return false;
    }

    /**
     * 加载Activity的数据，该方法在initOnCreate中执行,主要用于设置Activity的一些信息
     */
    protected abstract void loadActivityInfo();

    @Override
    public View initView() {
        // TODO Auto-generated method stub
        View view = View.inflate(getPageContext(),
                R.layout.hh_activity_recycleview, null);
        swipeRefreshLayout = getViewByID(view, R.id.swipe_refresh);
        recyclerView = getViewByID(view, R.id.recycler);
        return view;
    }

    @Override
    public void initValues() {
        // TODO Auto-generated method stub
        if (setCount() > 0) {
            count = setCount();
        }
        mark = setLayoutManagerType();
        pager_size = setPageSize();
        linearLayoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this, count);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(count,
                StaggeredGridLayoutManager.VERTICAL);

        if (setItemDecoration() >= 0) {
            final int itemPad = HHDensityUtils.dip2px(getPageContext(),setItemDecoration());
            // 设置间隔
            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.left = itemPad / 2;
                    outRect.right = itemPad / 2;
                    outRect.top = itemPad;
                }
            });
            recyclerView.setPadding(itemPad / 2, 0, itemPad / 2, 0);
        }
    }

    @Override
    public void initListeners() {
        // TODO Auto-generated method stub
        if (refresh) {
            swipeRefreshLayout
                    .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            isSwipeRefresh = true;
                            swipeRefreshLayout.setRefreshing(true);
                            pager = 1;
                            getListData();
                        }
                    });
        }
        if (load_more) {
            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                                mLastVisibleItem = ((LinearLayoutManager) recyclerView
                                        .getLayoutManager())
                                        .findLastVisibleItemPosition();
                            } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                                View view = recyclerView.getLayoutManager()
                                        .getChildAt(
                                                recyclerView.getLayoutManager()
                                                        .getChildCount() - 1);
                                mLastVisibleItem = recyclerView
                                        .getLayoutManager().getPosition(view);
                            } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                                int[] lastPositions = ((StaggeredGridLayoutManager) recyclerView
                                        .getLayoutManager())
                                        .findLastVisibleItemPositions(null);
                                mLastVisibleItem = findMax(lastPositions);
                            }
                        }

                        @Override
                        public void onScrollStateChanged(
                                RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == RecyclerView.SCROLL_STATE_IDLE
                                    && mLastVisibleItem >= adapter
                                    .getItemCount() - 1
                                    && pager_size == temp.size()) {
                                pager++;
                                getListData();
                            }
                        }
                    });
        }
    }

    /**
     * 取出最大值
     *
     * @param positions
     * @return
     */
    private int findMax(int[] positions) {
        int max = positions[0];
        for (int value : positions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * 获取listView显示的数据
     */
    private void getListData() {
        getListDataInThread(pager, new HHCallBackImp<T>() {
            @Override
            public void onSuccess(T data) {
                temp = (List<T>) data;
                sendHandlerMessage(GET_LIST_DATA);
            }

            @Override
            public void onError(T data) {
                temp = null;
                sendHandlerMessage(GET_LIST_DATA);
            }
        });
    }

    /**
     * 在线程中获取listview显示的数据，该方法会在线程中调用
     *
     * @param pageIndex 当前获取的是第几页的数据
     * @return
     */
    protected abstract void getListDataInThread(int pageIndex, HHCallBackImp<T> callback);


    /**
     * 实例化一个Adapter
     *
     * @param list listView显示的数据的集合
     * @return
     */
    protected abstract HHBaseRecyclerViewAdapter<T> instanceAdapter(List<T> list);

    /**
     * 设置item装饰间距
     *
     * @return
     */
    protected abstract int setItemDecoration();

    /**
     * 获取当前页每页获取的数据的大小
     *
     * @return
     */
    protected abstract int setPageSize();

    /**
     * 设置LayoutManager类型，默认2 【0：LinearLayoutManager ，1：GridLayoutManager，
     * 2：StaggeredGridLayoutManager 】设置1、2时，需用setCount（）方法，设置列数，默认2
     *
     * @return
     */
    protected abstract int setLayoutManagerType();

    /**
     * 设置每行列数，默认2
     */
    protected abstract int setCount();

    /**
     * 设置是否下拉刷新
     */

    protected void setIsRefresh(Boolean refresh) {
        this.refresh = refresh;
    }

    /**
     * 设置是否加载更多
     *
     * @param load_more true是
     */
    public void setIsLoadMore(Boolean load_more) {
        this.load_more = load_more;
    }

    /**
     * 返回列表数据
     *
     * @return
     */
    public List<T> getPagerListData() {
        return list;
    }

    /**
     * 返回recyclerView
     *
     * @return
     */
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    /**
     * 返回adapter
     *
     * @return
     */
    public HHBaseRecyclerViewAdapter getRecyclerViewAdapter() {
        return adapter;
    }

    /**
     * 下载刷新的时候执行的代码
     */
    public void onRefresh() {
        pager = 1;
        getListData();
    }

    /**
     * 设置页码
     *
     * @param pageIndex
     */
    public void setPageIndex(int pageIndex) {
        this.pager = pageIndex;
    }

    /**
     * 当前页码
     *
     * @return
     */
    public int getPageIndex() {
        return this.pager;
    }

    /**
     * 设置数据
     */
    private void setData() {
        if ((temp == null || temp.size() != pager_size) && footView != null) {
            adapter.removeFooterView(0);
            adapter.notifyDataSetChanged();
        }
        if (temp == null) {
            changeLoadState(HHLoadState.FAILED);
        } else if (temp.size() == 0) {
            if (pager == 1) {
                if (list == null) {
                    list = new ArrayList<>();
                } else {
                    list.clear();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
                changeLoadState(HHLoadState.NODATA);
            } else {
                HHTipUtils.getInstance().showToast(getPageContext(),
                        R.string.hh_no_data);
            }
        } else {
            if (pager == 1) {
                if (list != null && list.size() > 0) {
                    list.clear();
                }
                list.addAll(temp);
                if (isSwipeRefresh && adapter != null) {
                    adapter.notifyDataSetChanged();
                } else {
                    changeLoadState(HHLoadState.SUCCESS);
                    switch (mark) {
                        case 0:
                            recyclerView.setLayoutManager(linearLayoutManager);
                            break;
                        case 1:
                            recyclerView.setLayoutManager(gridLayoutManager);
                            break;
                        case 2:
                            recyclerView
                                    .setLayoutManager(staggeredGridLayoutManager);
                            break;
                    }
                    adapter = instanceAdapter(list);
                    recyclerView.setAdapter(adapter);
                }
                if (temp.size() == pager_size && adapter.getFootersCount() == 0) {
                    footView = LayoutInflater.from(getPageContext())
                            .inflate(R.layout.hh_include_footer, recyclerView, false);
                    adapter.addFootView(footView);
                }
            } else {
                list.addAll(temp);
                adapter.notifyDataSetChanged();
            }
            isSwipeRefresh = false;
        }
    }

    @Override
    public void processHandlerMsg(Message msg) {
        // TODO Auto-generated method stub
        if (msg.what == GET_LIST_DATA) {
            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            setData();
        }
    }
}
