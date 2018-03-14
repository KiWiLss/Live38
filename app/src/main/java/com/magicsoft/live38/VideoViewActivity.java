package com.magicsoft.live38;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.magicsoft.live38.widget.MyVideoView;

/**
 * @author : Lss winding
 *         e-mail : kiwilss@163.com
 *         time   : 2018/3/9
 *         desc   : ${DESCRIPTION}
 *         version: ${VERSION}
 */


public class VideoViewActivity extends AppCompatActivity {
    private boolean fullscreen=false;
    private MyVideoView mVideoView01;
    private android.widget.Button audiostart;
    private android.widget.Button audio2video;
    private android.widget.Button entrylistaudio;
    private View mTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);
        this.entrylistaudio = (Button) findViewById(R.id.entry_list_audio);
        this.audio2video = (Button) findViewById(R.id.audio_2_video);
        this.audiostart = (Button) findViewById(R.id.audio_start);
        this.mVideoView01 = (MyVideoView) findViewById(R.id.audtoView);


        mTv = findViewById(R.id.tv);


    }

    public void changeScreen(View view) {
        /*if(!fullscreen){//设置RelativeLayout的全屏模式
            RelativeLayout.LayoutParams layoutParams=
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mVideoView01.setLayoutParams(layoutParams);

            fullscreen = true;//改变全屏/窗口的标记
        }else{//设置RelativeLayout的窗口模式
            RelativeLayout.LayoutParams lp=new  RelativeLayout.LayoutParams(320,240);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            mVideoView01.setLayoutParams(lp);
            fullscreen = false;//改变全屏/窗口的标记
        }*/

        //判断当前屏幕方向
        if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            //切换竖屏
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else{
            //切换横屏
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            FrameLayout.LayoutParams layoutParams=
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            layoutParams.height=FrameLayout.LayoutParams.MATCH_PARENT;
            mTv.setLayoutParams(layoutParams);
        }
    }


}
