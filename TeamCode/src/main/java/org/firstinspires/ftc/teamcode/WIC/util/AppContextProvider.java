package org.firstinspires.ftc.teamcode.WIC.util;

import android.content.Context;

public class AppContextProvider {
    private static Context appContext;

    public static Context getAppContext() {
        return appContext;
    }

    public static void setAppContext(Context appContext) {
        AppContextProvider.appContext = appContext;
    }
}