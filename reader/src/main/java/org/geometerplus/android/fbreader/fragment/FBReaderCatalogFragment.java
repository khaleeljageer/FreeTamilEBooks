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
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.options.ColorProfile;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.util.ZLAndroidColorUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * desc:目录fragment<br>
 * author : yuanbin<br>
 * date : 2018/10/13 11:32
 */
public class FBReaderCatalogFragment extends Fragment {

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


    public static FBReaderCatalogFragment newInstance() {
        FBReaderCatalogFragment fragment = new FBReaderCatalogFragment();
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

        final TOCTree root = myFBReaderApp.Model.TOCTree;
        TOCTree selectTree = myFBReaderApp.getCurrentTOCElement();
        List<DataBean> data = new ArrayList<DataBean>();
        insertToc(data, root, selectTree);
        if (data.size() <= 0) {
            showEmptyTocView();
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mViewCatalogEmptyContainer.setVisibility(View.GONE);
            mAdapter.setData(data);
        }
    }

    //region 目录相关
    private void showEmptyTocView() {
        mTvCatalogEmptyDesc.setTextColor(grayColor);
        mTvCatalogEmptyDesc.setText(R.string.fbreader_catalogue_empty);
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

    private void insertToc(List<DataBean> data, TOCTree root, TOCTree selectTree) {
        if (root == null || !root.hasChildren()) {
            return;
        } else {
            List<TOCTree> trees = root.subtrees();
            for (TOCTree tree : trees) {
                if (tree != null && tree.getReference() != null) {
                    data.add(new DataBean(tree.getText(), tree.getReference().ParagraphIndex, tree.Level, tree == selectTree));
                    if (tree.hasChildren()) {
                        insertToc(data, tree, selectTree);
                    }
                }
            }
        }
    }

    //endregion

    private void initListener() {
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, DataBean bean) {
                final FBReaderApp fbreader = (FBReaderApp) ZLApplication.Instance();
                fbreader.addInvisibleBookmark();
                fbreader.BookTextView.gotoPosition(bean.getPage(), 0, 0);
                fbreader.showBookTextView();
                fbreader.storePosition();
                dismissDialog();
            }
        });
    }

    private void dismissDialog(){
        Fragment parent = getParentFragment();
        if (parent != null && parent instanceof DialogFragment) {
            if (parent.isAdded()) {
                ((DialogFragment) parent).dismissAllowingStateLoss();
            }
        }
    }

    //region adapter
    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        Context context;
        List<DataBean> list;

        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public void setData(List<DataBean> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public Adapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fbreader_catalog, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final DataBean bean = getItem(position);
            if (bean != null) {
                holder.mTvContent.setText(bean.getName());
                holder.mTvContent.setPadding(bean.level * dp2px(17), 0, 0, 0);
                holder.mTvPage.setText("" + bean.getPage());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(v, position, bean);
                        }
                    }
                });
                if (bean.isSelected) {
                    holder.mTvContent.setTextColor(selectColor);
                } else {
                    holder.mTvContent.setTextColor(blackColor);
                }
            }
        }

        private int dp2px(int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public DataBean getItem(int position) {
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
                mTvPage.setVisibility(View.INVISIBLE);
                mTvPage.setTextColor(grayColor);
                mViewBottomLine = itemView.findViewById(R.id.id_item_fbreader_catalog_line);
                mViewBottomLine.setBackgroundColor(lineColor);
            }
        }

    }

    private interface OnItemClickListener {
        void onItemClick(View view, int position, DataBean bean);
    }

    private class DataBean {
        private String name;
        private int page;
        private int level;
        private boolean isSelected;

        public DataBean(String name, int page, int level, boolean isSelected) {
            this.name = name;
            this.page = page;
            this.level = level;
            this.isSelected = isSelected;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }
    }
    //endregion
}
