package com.magicsoft.live38.popup;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.magicsoft.live38.R;
import com.magicsoft.live38.widget.CustomPw;
import com.magicsoft.mylibrary.PopupOOBtn;
import com.magicsoft.mylibrary.PopupOTBtn;
import com.magicsoft.mylibrary.PopupTOBtn;
import com.magicsoft.mylibrary.PopupTTBtn;
import com.magicsoft.mylibrary.PopupUtils;
import com.magicsoft.mylibrary.PopupWindowUtils;

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

    /**通用一个标题,一个按钮
     * @param view
     */
    public void oneOneListener(View view) {
        PopupOOBtn popupOOBtn = new PopupOOBtn(this, new PopupOOBtn.BtnClick() {
            @Override
            public void sureClickListener(PopupOOBtn popupOOBtn) {
                //点击回调
                Toast.makeText(PopupActivity.this, "click", Toast.LENGTH_SHORT).show();
                popupOOBtn.dismiss();
            }
        });
        //设置标题,标题颜色(0默认为黑色);字体是否加粗
        popupOOBtn.setTitleAndColor("设置标题",0,false)
                .setRadius(2)//设置对话框圆角弧度
                //设置按钮文字内容,文字颜色,背景颜色(0都是默认颜色);
        .setSureTextAndColor("按钮",0,0)
                //.setAnimationStyle(R.style.contextMenuAnim);//设置对话框进出动画
        //设置展示在中间
        .showCenter(this);
        //popupOOBtn.showBottom(this);设置在底部等

    }

    /**通用两个标题,一个按钮
     * @param view
     */
    public void twoOneListener(View view) {
        new PopupTOBtn(this, new PopupTOBtn.BtnClick() {
            @Override
            public void sureClickListener(PopupTOBtn popupTOBtn) {
                Toast.makeText(PopupActivity.this, "确定点击", Toast.LENGTH_SHORT).show();
                popupTOBtn.dismiss();
            }
        })
                .setHint("设置提示标题",0,true)
                .setTitle("标题内容内容内容内容",R.color.colorAccent,false)
                .setSureTextAndColor("确定点击",R.color.colorAccent,R.color.colorPrimaryDark)
                .showCenter(this);
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

    /**通用一个标题,两个按钮
     * @param view
     */
    public void oneTwoListener(View view) {
        new PopupOTBtn(this, new PopupOTBtn.ContentClickListener() {
            @Override
            public void sureClickListener(PopupOTBtn popupOTBtn) {
                Toast.makeText(PopupActivity.this, "确定监听", Toast.LENGTH_SHORT).show();
                popupOTBtn.dismiss();
            }

            @Override
            public void cancelClickListener() {
                Toast.makeText(PopupActivity.this, "取消监听", Toast.LENGTH_SHORT).show();
            }
        })
                .setTitleAndColor("设置标题",0,true)
                .showCenter(this);
    }

    /**两个标题,两个按钮
     * @param view
     */
    public void twoTwoListener(View view) {
        new PopupTTBtn(this, new PopupTTBtn.ContentTClickListener() {
            @Override
            public void sureClickListener(PopupTTBtn popupTTBtn) {
                Toast.makeText(PopupActivity.this, "确定", Toast.LENGTH_SHORT).show();
                popupTTBtn.dismiss();
            }

            @Override
            public void cancelClickListener() {
                Toast.makeText(PopupActivity.this, "取消", Toast.LENGTH_SHORT).show();
            }
        }).setTitle("标题",0,false)
                .showCenter(this);
    }

    /**自定义界面
     * @param view
     */
    public void customUiListener(View view) {
        PopupUtils popupUtils = new PopupUtils(this, R.layout.pw_normal);
        //获取控件
        View btnClick = popupUtils.getItemView(R.id.btn_pw_normal_click);
        popupUtils.showCenter(this);

    }


    /**自定义加复用
     * @param view
     */
    public void dialogUseListener(View view) {
        CustomPw customPw = new CustomPw(this, null);
        customPw.showCenter(this);
    }

    /**库的带背景的菜单
     * @param view
     */
    public void menuPopupListener(View view) {
        CustomPw customPw = new CustomPw(this, null);
        //在最后展示时,调用此方法可作为菜单样式使用
        customPw.showAsDropDown(view);
    }

    /**按键左侧菜单进入
     * 类似的还有右侧进入的等
     * @param view
     */
    public void leftMenuListener(View view) {
        PopupWindowUtils popupWindowUtils = new PopupWindowUtils(view);

        //设置展示的菜单
        popupWindowUtils.setContentView(R.layout.pw_normal2);
        //展示,左侧进入
        popupWindowUtils.showLikePopDownLeftMenu();
        //获取控件
        View btnClick = popupWindowUtils.findId(R.id.btn_pw_normal_click);
    }
}
