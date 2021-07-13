package com.example.sunshine;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExcecuters {

    // using Executor to fill the database in a background thread.
    private static Executor picassoIO; // recipe Input Output
    private final static Object LOCK = new Object();
    private static  AppExcecuters sInstance;
    private AppExcecuters(Executor _picassoIO){
        this.picassoIO = _picassoIO;
    }
    public Executor picassoIO(){
        return picassoIO;
    }
    public static AppExcecuters getsInstance(){
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = new AppExcecuters(Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }
}
