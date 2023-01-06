package com.example.turistapp;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class ColorDinamico extends Application {

    //Se establecera el Color Dinamico
    @Override
    public void onCreate() {
        super.onCreate();

        //Si el dispositivo es android 12 o superior se establecera tema dinamico, si no, la paleta de colores elegida

        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
