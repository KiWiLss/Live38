package com.magicsoft.live38.popup;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.magicsoft.live38.R;

/**
 * @author : Lss winding
 *         e-mail : kiwilss@163.com
 *         time   : 2018/3/14
 *         desc   : ${DESCRIPTION}
 *         version: ${VERSION}
 */


public class PopupActivity extends AppCompatActivity {

    private Button mBtnMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        mBtnMenu = (Button) findViewById(R.id.btn_popup_menu);


    }

    public void oneOneListener(View view) {

    }

    public void twoOneListener(View view) {

    }

    /**一般样式对话框点击
     * @param view
     */
    public void popupListener(View view) {
        //背景变暗
        screenDarken();

        //获取要引入的布局
        View contentView = getLayoutInflater().inflate(R.layout.pw_normal, null);
        View footerView = getLayoutInflater().inflate(R.layout.activity_popup, null);
        //获取控件
        Button btnClick = contentView.findViewById(R.id.btn_pw_normal_click);
        //初始化对话框
        final PopupWindow popupWindow = new PopupWindow(contentView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        //设置点击空白处是否消失
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(true);
        //对对话框设置动画
        popupWindow.setAnimationStyle(R.style.contextMenuAnim);
        //设置展示位置,居中
        //popupWindow.showAtLocation(footerView, Gravity.CENTER,0,0);

        //作为菜单显示
        popupWindow.showAsDropDown(footerView);

        //对对话框消失时的监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                screenLight();
            }
        });

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏对话框
                popupWindow.dismiss();
            }
        });


    }

    public void screenDarken(){
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha=0.5f;
        getWindow().setAttributes(attributes);

    }
    public void screenLight(){
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha=1f;
        getWindow().setAttributes(attributes);

    }

    /**菜单样式对话框点击
     * @param view
     */
    public void menuListener(View view) {
        View contentView = getLayoutInflater().inflate(R.layout.pw_normal, null);
        //View footerView = getLayoutInflater().inflate(R.layout.activity_popup, null);

        //初始化对话框
        final PopupWindow popupWindow = new PopupWindow(contentView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        //设置点击空白处是否消失
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(true);

        //作为菜单显示
        popupWindow.showAsDropDown(mBtnMenu);

    }
}
