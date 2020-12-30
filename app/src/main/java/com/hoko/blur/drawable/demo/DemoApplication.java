package com.hoko.blur.drawable.demo;

import android.app.Application;
import android.content.Context;

import me.weishu.reflection.Reflection;

/**
 * Created by yuxiaofei on 2020/12/30
 */
public class DemoApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Reflection.unseal(base);
    }
}
