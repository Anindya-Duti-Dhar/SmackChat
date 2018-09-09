package base.droidtool.dtlib;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;


import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.DefaultSort;

import java.util.ArrayList;

import anindya.sample.smackchat.R;
import base.droidtool.DroidTool;
import base.droidtool.adapters.LinkAdapter;


public class ItemList {

    boolean mOnceAnimate = false;
    DroidTool dt;

    public ItemList(DroidTool droidTool) {
        dt = droidTool;
    }

    public interface onRecyclerViewItemDelete {
        void onItemDeleteClick(Object o, int pos);
    }

    public interface onRecyclerViewItemClick {
        void onItemRowClick(Object o, int pos);
    }

    public interface onRecyclerViewItemSwipe {
        boolean onItemRowClick(int pos);
    }

    private ArrayList<?> dataList;
    public RecyclerView recyclerView;
    public LinkAdapter adapter = null;
    private onRecyclerViewItemClick customListenerClick = null;
    private onRecyclerViewItemDelete customListenerDelete = null;
    private onRecyclerViewItemSwipe customListenerSwipe = null;

    public void setRecyclerViewItemClickListener(onRecyclerViewItemClick listener) {
        this.customListenerClick = listener;
    }

    public void setRecyclerViewItemDeleteListener(onRecyclerViewItemDelete listener) {
        this.customListenerDelete = listener;
    }

    public void setRecyclerViewItemSwipeListener(onRecyclerViewItemSwipe listener) {
        this.customListenerSwipe = listener;
    }

    public RecyclerView set(int recyclerResId, ArrayList<?> data, int styleResId,
                            int deleteImageViewResId, int orientation, int drawableIcon) {
        RecyclerView recyclerView = (RecyclerView) ((Activity) dt.c).findViewById(recyclerResId);
        recyclerView.setLayoutManager(new LinearLayoutManager(dt.c, orientation, false));
        adapter = new LinkAdapter(dt, data, styleResId, deleteImageViewResId, drawableIcon);
        recyclerView.setAdapter(adapter);

        adapter.setClickResponseListener(new LinkAdapter.onAdapterItemClick() {
            @Override
            public void onItemRowClick(Object o, int pos) {
                if (customListenerClick != null) customListenerClick.onItemRowClick(o, pos);
            }
        });

        adapter.setDeleteResponseListener(new LinkAdapter.onAdapterItemDelete() {
            @Override
            public void onItemDeleteClick(Object o, int pos) {
                if (customListenerDelete != null) customListenerDelete.onItemDeleteClick(o, pos);
            }
        });

        dataList = data;
        this.recyclerView = recyclerView;
        this.recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mOnceAnimate) {
                    mOnceAnimate = true;
                    animate();
                }
            }
        });

        // init swipe to delete
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe
                showAlert(position);//show alert dialog
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(this.recyclerView); //bind swipe to recylcerview

        return this.recyclerView;
    }

    public RecyclerView set(int recyclerResId, ArrayList<?> data, int styleResId,
                            int deleteImageViewResId, int columnCount, int orientation, int drawableIcon) {
        RecyclerView recyclerView = (RecyclerView) ((Activity) dt.c).findViewById(recyclerResId);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(dt.c, columnCount, orientation, false));
        adapter = new LinkAdapter(dt, data, styleResId, deleteImageViewResId, drawableIcon);
        recyclerView.setAdapter(adapter);
        adapter.setClickResponseListener(new LinkAdapter.onAdapterItemClick() {
            @Override
            public void onItemRowClick(Object o, int pos) {
                if (customListenerClick != null) customListenerClick.onItemRowClick(o, pos);
            }
        });
        adapter.setDeleteResponseListener(new LinkAdapter.onAdapterItemDelete() {
            @Override
            public void onItemDeleteClick(Object o, int pos) {
                if (customListenerDelete != null) customListenerDelete.onItemDeleteClick(o, pos);
            }
        });

        dataList = data;
        this.recyclerView = recyclerView;
        this.recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mOnceAnimate) {
                    mOnceAnimate = true;
                    animate();
                }
            }
        });

        return this.recyclerView;
    }

    public RecyclerView set(RecyclerView recyclerView, ArrayList<?> data, int styleResId,
                            int deleteImageViewResId, int columnCount, int orientation, int drawableIcon) {

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(dt.c, columnCount, orientation, false));
        adapter = new LinkAdapter(dt, data, styleResId, deleteImageViewResId, drawableIcon);
        recyclerView.setAdapter(adapter);
        adapter.setClickResponseListener(new LinkAdapter.onAdapterItemClick() {
            @Override
            public void onItemRowClick(Object o, int pos) {
                customListenerClick.onItemRowClick(o, pos);
            }
        });
        adapter.setDeleteResponseListener(new LinkAdapter.onAdapterItemDelete() {
            @Override
            public void onItemDeleteClick(Object o, int pos) {
                customListenerDelete.onItemDeleteClick(o, pos);
            }
        });

        dataList = data;
        this.recyclerView = recyclerView;
        this.recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mOnceAnimate) {
                    mOnceAnimate = true;
                    animate();
                }
            }
        });

        return this.recyclerView;
    }


    public void animate() {
        new Spruce.SpruceBuilder(recyclerView)
                .sortWith(new DefaultSort(100))
                .animateWith(DefaultAnimations.shrinkAnimator(recyclerView, 500),
                        ObjectAnimator.ofFloat(recyclerView, "translationY", recyclerView.getWidth(), 0f).setDuration(500))
                .start();
    }



    /************************ popup dialog recycler region start *********************/

    boolean mPopupOnceAnimate = false;

    public interface onPopupRecyclerViewItemDelete {
        void onItemDeleteClick(Object o, int pos);
    }

    public interface onPopupRecyclerViewItemClick {
        void onItemRowClick(Object o, int pos);
    }

    public interface onPopupRecyclerViewItemSwipe {
        boolean onItemRowClick(int pos);
    }

    private ArrayList<?> popupDataList;
    public RecyclerView popupRecyclerView;
    public LinkAdapter popupAdapter = null;
    private onPopupRecyclerViewItemClick popupCustomListenerClick = null;
    private onPopupRecyclerViewItemDelete popupCustomListenerDelete = null;
    private onPopupRecyclerViewItemSwipe popupCustomListenerSwipe = null;

    public void initList(RecyclerView recyclerView, ArrayList<?> data, int adapterLayoutResId,
                         int drawableDeleteIcon, int columnCount, int orientation, int drawableListIcon, onPopupRecyclerViewItemClick itemClickListener
            , onPopupRecyclerViewItemDelete itemDeleteListener, final onPopupRecyclerViewItemSwipe itemSwipeListener, final String deleteErrorMessage) {

        popupDataList = data;
        popupRecyclerView = recyclerView;

        popupRecyclerView.setHasFixedSize(false);
        popupRecyclerView.setLayoutManager(new GridLayoutManager(dt.c, columnCount, orientation, false));
        popupAdapter = new LinkAdapter(dt, data, adapterLayoutResId, drawableDeleteIcon, drawableListIcon);
        popupRecyclerView.setAdapter(popupAdapter);

        // item click listener
        popupCustomListenerClick = itemClickListener;

        popupAdapter.setClickResponseListener(new LinkAdapter.onAdapterItemClick() {
            @Override
            public void onItemRowClick(Object o, int pos) {
                if (popupCustomListenerClick != null)
                    popupCustomListenerClick.onItemRowClick(o, pos);
            }
        });

        // item delete listener
        popupCustomListenerDelete = itemDeleteListener;

        popupAdapter.setDeleteResponseListener(new LinkAdapter.onAdapterItemDelete() {
            @Override
            public void onItemDeleteClick(Object o, int pos) {
                if (popupCustomListenerDelete != null)
                    popupCustomListenerDelete.onItemDeleteClick(o, pos);
            }
        });

        // item swipe listener
        popupCustomListenerSwipe = itemSwipeListener;

        if (popupCustomListenerSwipe != null) {
            // init swipe to delete
            ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                    final int position = viewHolder.getAdapterPosition(); //get position which is swipe
                    showPopupListItemDeleteAlert(position, deleteErrorMessage);//show alert dialog
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(popupRecyclerView); //bind swipe to recylcer view
        }

        popupRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mPopupOnceAnimate) {
                    mPopupOnceAnimate = true;
                    popupAnimate();
                }
            }
        });
    }

    public void popupAnimate() {
        new Spruce.SpruceBuilder(popupRecyclerView)
                .sortWith(new DefaultSort(100))
                .animateWith(DefaultAnimations.shrinkAnimator(popupRecyclerView, 500),
                        ObjectAnimator.ofFloat(popupRecyclerView, "translationY", popupRecyclerView.getWidth(), 0f).setDuration(500))
                .start();
    }

    // alert for confirmation to delete
    public void showPopupListItemDeleteAlert(final int position, final String deleteErrorMessage) {

        dt.alert.showWarning(dt.gStr(R.string.want_to_delete));
        dt.alert.setAlertListener(new SweetAlert.AlertListener() {
            @Override
            public void onAlertClick(boolean isCancel) {
                // item swipe listener
                if (popupCustomListenerSwipe != null) {
                    if (!isCancel) {
                        if (!popupCustomListenerSwipe.onItemRowClick(position)) {
                            dt.alert.showError(dt.gStr(R.string.common_warning_title), deleteErrorMessage, dt.gStr(R.string.ok));
                            popupAdapter.notifyItemRemoved(position + 1);
                            popupAdapter.notifyItemRangeChanged(position, popupAdapter.getItemCount());
                        } else {
                            popupAdapter.notifyItemRemoved(position + 1);
                            popupAdapter.notifyItemRangeChanged(position, popupAdapter.getItemCount());
                        }
                    } else {
                        popupAdapter.notifyItemRemoved(position + 1);
                        popupAdapter.notifyItemRangeChanged(position, popupAdapter.getItemCount());
                    }
                }
            }
        });
    }


    /************************ popup dialog recycler region end *********************/


    // alert for confirmation to update
    public void showAlert(final int position) {
        dt.alert.showWarning(dt.gStr(R.string.want_to_delete));
        dt.alert.setAlertListener(new SweetAlert.AlertListener() {
            @Override
            public void onAlertClick(boolean isCancel) {
                if (!isCancel) {
                    if (customListenerSwipe != null) {
                        if (!customListenerSwipe.onItemRowClick(position)) {
                            dt.alert.showError(dt.gStr(R.string.common_warning_title), dt.gStr(R.string.already_synced), dt.gStr(R.string.ok));
                            adapter.notifyItemRemoved(position + 1);
                            adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                        } else {
                            adapter.notifyItemRemoved(position + 1);
                            adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                        }
                    }
                } else {
                    adapter.notifyItemRemoved(position + 1);
                    adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                }
            }
        });
    }

    public void showHideNoRecordMessage(Context context, ArrayList<?> data, int recyclerResID, int resId) {
        TextView mNoDataMessage = dt.ui.textView.getObject(resId);
        RecyclerView mRecyclerView = dt.ui.recyclerView.getRes(recyclerResID);
        if (data.size() > 0) {
            if (mNoDataMessage.getVisibility() == View.VISIBLE) {
                mNoDataMessage.setVisibility(View.GONE);
            }
            if (mRecyclerView.getVisibility() == View.GONE) {
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            if (mNoDataMessage.getVisibility() == View.GONE) {
                mNoDataMessage.setVisibility(View.VISIBLE);
            }
            if (mRecyclerView.getVisibility() == View.VISIBLE) {
                mRecyclerView.setVisibility(View.GONE);
            }
        }
    }


}
