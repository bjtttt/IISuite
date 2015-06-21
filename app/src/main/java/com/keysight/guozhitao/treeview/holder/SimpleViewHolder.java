package com.keysight.guozhitao.treeview.holder;

/**
 * Created by cn569363 on 6/8/2015.
 */

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.keysight.guozhitao.treeview.model.TreeNode;

//package com.unnamed.b.atv.holder;

/**
 * Created by Bogdan Melnychuk on 2/11/15.
 */
public class SimpleViewHolder extends TreeNode.BaseNodeViewHolder<Object> {

    public SimpleViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, Object value) {
        final TextView tv = new TextView(context);
        tv.setText(String.valueOf(value));
        return tv;
    }

    @Override
    public void toggle(boolean active) {

    }
}
