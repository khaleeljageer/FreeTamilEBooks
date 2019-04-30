package org.geometerplus.android.fbreader.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.options.ColorProfile;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.util.ZLAndroidColorUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * desc:书签fragment<br>
 * author : yuanbin<br>
 * date : 2018/10/13 11:32
 */
public class FBReaderBookMarkFragment extends Fragment {

    private Context mContext;
    FBReaderApp myFBReaderApp;
    private View mRootView;

    private RecyclerView mRecyclerView;
    private View mViewCatalogEmptyContainer;
    private ImageView mIvCatalogEmptyIv;
    private TextView mTvCatalogEmptyDesc;
    private Adapter mAdapter;

    private int grayColor;
    private int blackColor;
    private int selectColor;
    private int lineColor;


    public static FBReaderBookMarkFragment newInstance() {
        FBReaderBookMarkFragment fragment = new FBReaderBookMarkFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.layout_fragment_fbreader_catalog, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }


    private void initView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.id_fragment_fbreader_catalog);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mViewCatalogEmptyContainer = mRootView.findViewById(R.id.id_fragment_fbreader_catalog_empty);
        mIvCatalogEmptyIv = (ImageView) mRootView.findViewById(R.id.id_fragment_fbreader_catalog_empty_img);
        mTvCatalogEmptyDesc = (TextView) mRootView.findViewById(R.id.id_fragment_fbreader_catalog_empty_desc);
    }

    private void initData() {
        mAdapter = new Adapter(mContext);
        mRecyclerView.setAdapter(mAdapter);
        myFBReaderApp = (FBReaderApp) ZLApplication.Instance();
        grayColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().GrayTextOption.getValue());
        blackColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().BlackTextOption.getValue());
        selectColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().MenuSelectedTextOption.getValue());
        lineColor = ZLAndroidColorUtil.rgb(myFBReaderApp.ViewOptions.getColorProfile().ThinLineOption.getValue());
        List<Bookmark> bookmarks = new ArrayList<Bookmark>();
        if (myFBReaderApp.bookmarks != null) {
            for (Bookmark bookmark : myFBReaderApp.bookmarks) {
                if (bookmark != null && !bookmark.Highlight && bookmark.IsVisible) {
                    bookmarks.add(bookmark);
                }
            }
        }
        if (bookmarks.size() <= 0) {
            showEmptyBookMarkView();
        } else {
            mAdapter.addData(bookmarks);
            mRecyclerView.setVisibility(View.VISIBLE);
            mViewCatalogEmptyContainer.setVisibility(View.GONE);
        }
    }

    //region 书签相关
    private void showEmptyBookMarkView() {
        mTvCatalogEmptyDesc.setTextColor(grayColor);
        mTvCatalogEmptyDesc.setText(R.string.fbreader_bookmark_empty);
        boolean isDay = ColorProfile.DAY.equals(myFBReaderApp.ViewOptions.ColorProfileName.getValue());
        if (isDay) {
            //日间模式
            mIvCatalogEmptyIv.setImageResource(R.drawable.ic_fbreader_catalog_empty_day);
        } else {
            //夜间模式
            mIvCatalogEmptyIv.setImageResource(R.drawable.ic_fbreader_catalog_empty_night);
        }
        mRecyclerView.setVisibility(View.GONE);
        mViewCatalogEmptyContainer.setVisibility(View.VISIBLE);
    }
    //endregion

    private void dismissDialog() {
        Fragment parent = getParentFragment();
        if (parent != null && parent instanceof DialogFragment) {
            if (parent.isAdded()) {
                ((DialogFragment) parent).dismissAllowingStateLoss();
            }
        }
    }

    private void initListener() {

    }

    //region adapter
    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        Context context;
        List<Bookmark> list = new ArrayList<Bookmark>();

        public void addData(List<Bookmark> list) {
            if (list == null || list.size() <= 0) {
                return;
            }
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        public Adapter(Context context) {
            this.context = context;
        }

        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fbreader_catalog, parent, false));
        }

        @Override
        public void onBindViewHolder(@NotNull ViewHolder holder, final int position) {
            final Bookmark bean = getItem(position);
            if (bean != null) {
                holder.mTvContent.setText(bean.getText());
                holder.mTvContent.setPadding(dp2px(17), 0, 0, 0);
                holder.mTvPage.setText("" + bean.getParagraphIndex());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myFBReaderApp.gotoBookmark(bean, true);
                        dismissDialog();
                    }
                });
            }
        }

        private int dp2px(int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public Bookmark getItem(int position) {
            if (position >= getItemCount()) {
                return null;
            }
            return list.get(position);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mTvContent;
            private TextView mTvPage;
            private View mViewBottomLine;

            public ViewHolder(View itemView) {
                super(itemView);
                mTvContent = (TextView) itemView.findViewById(R.id.id_item_fbreader_catalog_name);
                mTvPage = (TextView) itemView.findViewById(R.id.id_item_fbreader_catalog_page);
                mTvPage.setTextColor(grayColor);
                mTvPage.setVisibility(View.INVISIBLE);
                mTvContent.setTextColor(blackColor);
                mViewBottomLine = itemView.findViewById(R.id.id_item_fbreader_catalog_line);
                mViewBottomLine.setBackgroundColor(lineColor);
            }
        }

    }
    //endregion
}
