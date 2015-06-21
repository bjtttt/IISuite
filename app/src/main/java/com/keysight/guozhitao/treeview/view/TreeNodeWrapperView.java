package com.keysight.guozhitao.treeview.view;

/**
 * Created by cn569363 on 6/8/2015.
 */

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.keysight.guozhitao.iisuite.R;

//package com.unnamed.b.atv.view;
//import com.unnamed.b.atv.R;

/**
 * Created by Bogdan Melnychuk on 2/10/15.
 */
public class TreeNodeWrapperView extends LinearLayout {
    private LinearLayout nodeItemsContainer;
    private ViewGroup nodeContainer;
    private final int containerStyle;

    public TreeNodeWrapperView(Context context, int containerStyle) {
        super(context);
        this.containerStyle = containerStyle;
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);

        nodeContainer = new RelativeLayout(getContext());
        nodeContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        nodeContainer.setId(R.id.node_header);

        ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), containerStyle);
        nodeItemsContainer = new LinearLayout(newContext, null, containerStyle);
        nodeItemsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        nodeItemsContainer.setId(R.id.node_items);
        nodeItemsContainer.setOrientation(LinearLayout.VERTICAL);
        nodeItemsContainer.setVisibility(View.GONE);

        addView(nodeContainer);
        addView(nodeItemsContainer);
    }


    public void insertNodeView(View nodeView) {
        nodeContainer.addView(nodeView);
    }

}
