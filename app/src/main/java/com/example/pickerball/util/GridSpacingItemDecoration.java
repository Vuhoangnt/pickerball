package com.example.pickerball.util;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Simple grid spacing for RecyclerView GridLayoutManager.
 * Keeps consistent outer padding + inner gaps.
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int spacingPx;
    private final boolean includeEdge;

    public GridSpacingItemDecoration(int spacingPx, boolean includeEdge) {
        this.spacingPx = Math.max(0, spacingPx);
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.LayoutManager lm = parent.getLayoutManager();
        if (!(lm instanceof GridLayoutManager)) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        int position = parent.getChildAdapterPosition(view);
        if (position == RecyclerView.NO_POSITION) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        GridLayoutManager glm = (GridLayoutManager) lm;
        int spanCount = glm.getSpanCount();
        int column = position % spanCount;

        if (includeEdge) {
            outRect.left = spacingPx - (column * spacingPx / spanCount);
            outRect.right = (column + 1) * spacingPx / spanCount;
            outRect.top = position < spanCount ? spacingPx : spacingPx / 2;
            outRect.bottom = spacingPx / 2;
        } else {
            outRect.left = column * spacingPx / spanCount;
            outRect.right = spacingPx - (column + 1) * spacingPx / spanCount;
            outRect.top = position >= spanCount ? spacingPx : 0;
            outRect.bottom = 0;
        }
    }
}

