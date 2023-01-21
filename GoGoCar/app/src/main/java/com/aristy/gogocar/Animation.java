package com.aristy.gogocar;

import androidx.fragment.app.FragmentTransaction;

public class Animation {

    public static final int ANIMATE_SLIDE_RIGHT = 1;
    public static final int ANIMATE_SLIDE_LEFT = 2;
    public static final int ANIMATE_SLIDE_DOWN = 3;
    public static final int ANIMATE_SLIDE_UP = 4;

    public static void animate(FragmentTransaction fragmentTransaction, int animation){
        switch (animation){
            case ANIMATE_SLIDE_RIGHT:
                animateSlideRight(fragmentTransaction);
                break;
            case ANIMATE_SLIDE_LEFT:
                animateSlideLeft(fragmentTransaction);
                break;
            case ANIMATE_SLIDE_DOWN:
                animateSlideDown(fragmentTransaction);
                break;
            case ANIMATE_SLIDE_UP:
                animateSlideUp(fragmentTransaction);
                break;
        }
    }

    private static void animateSlideRight(FragmentTransaction fragmentTransaction){
        fragmentTransaction.setCustomAnimations(
                R.anim.from_right,
                R.anim.to_left
        );
    }

    private static void animateSlideLeft(FragmentTransaction fragmentTransaction){
        fragmentTransaction.setCustomAnimations(
                R.anim.from_left,
                R.anim.to_right
        );
    }

    private static void animateSlideDown(FragmentTransaction fragmentTransaction){
        fragmentTransaction.setCustomAnimations(
                R.anim.animate_slide_down_enter,
                R.anim.animate_slide_down_exit
        );
    }

    private static void animateSlideUp(FragmentTransaction fragmentTransaction){
        fragmentTransaction.setCustomAnimations(
                R.anim.animate_slide_up_enter,
                R.anim.animate_slide_up_exit
        );
    }

}
