package com.magicsoft.live38.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.magicsoft.live38.R;
import com.magicsoft.mylibrary.BottomPushPopupWindow;

/**
 * @author : Lss winding
 *         e-mail : kiwilss@163.com
 *         time   : 2018/3/14
 *         desc   : ${DESCRIPTION}
 *         version: ${VERSION}
 */


public class CustomPw extends BottomPushPopupWindow<Object> {

    public CustomPw(Context context, Object o) {
        super(context, o);
    }

    @Override
    protected View generateCustomView(Object o) {
        //获取布局
        View contentView = LayoutInflater.from(context).inflate(R.layout.pw_normal, null);

        //获取控件
        Button btnClick = contentView.findViewById(R.id.btn_pw_normal_click);
        return contentView;
    }
}

