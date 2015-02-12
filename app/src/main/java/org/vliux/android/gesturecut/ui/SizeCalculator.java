package org.vliux.android.gesturecut.ui;

import android.content.res.Resources;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 2/12/15.
 */
public class SizeCalculator {

    /**
     * Return the width of gesture icon in GestureListView, with its left and right margins.
     * @param res
     * @return
     */
    public static int gestureIconWidth(Resources res){
        return (int)(res.getDimension(R.dimen.gesture_thumbnail_width) +
                res.getDimension(R.dimen.gesture_list_outter_margin) + // marginLeft of ImageView in item_gesture
                res.getDimension(R.dimen.gesture_list_item_vertical_divider_margin_horiz)); // marginRight of ImageView in item_gesture
    }

}
