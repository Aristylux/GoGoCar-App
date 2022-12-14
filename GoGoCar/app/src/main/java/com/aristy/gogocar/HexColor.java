package com.aristy.gogocar;

import static com.aristy.gogocar.CodesTAG.TAG_Web;

import android.util.Log;

public class HexColor {

    public static final long TRANSPARENT = 0;

    String color;

    HexColor(String color){
        this.color = color;
    }

    public void convertToAndroidColor(){
        // Get Transparency
        String transparency = color.substring(Math.max(color.length() - 2, 0));

        // Delete Transparency at the end and '#'
        StringBuilder stringBuilder = new StringBuilder(color);
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        stringBuilder.deleteCharAt(0);

        // Return Color for android
        color = "#" + transparency + stringBuilder;
    }

    public long getDecSigned(){
        // Verification if format : #XXXXXXXX -> #TTRRGGBB
        String formattedHex = checkHex(color);
        Log.d(TAG_Web, "formattedHex : " + formattedHex);

        // Convert Decimal (for binary)
        long dec = Long.parseLong(formattedHex, 16);
        Log.d(TAG_Web, "dec : " + dec);

        // Convert Binary
        String bin = Long.toBinaryString(dec);
        Log.d(TAG_Web, "bin : " + bin );

        // Swipe Bits
        String binSwipe = swipeBits(bin);
        Log.d(TAG_Web, "bin : " + binSwipe);

        // Convert to Dec
        long decConvert = Long.parseLong(binSwipe, 2);
        Log.d(TAG_Web, "dec : " + (decConvert + 1) * -1);
        return (decConvert + 1) * -1;
    }

    private String checkHex(String hex){
        StringBuilder formattedHex = new StringBuilder(hex);
        if(formattedHex.charAt(0) == '#')
            formattedHex.deleteCharAt(0);

        final String values = "0123456789AaBbCcDdEeFf";
        for (int index = 0; index < formattedHex.length(); index++){
            boolean valCorrect = false;
            for (int val = 0; val < values.length(); val++){
                if(formattedHex.charAt(index) == values.charAt(val))
                    valCorrect = true;
            }
            if (!valCorrect)
                formattedHex.setCharAt(index, 'F');
        }

        return String.valueOf(formattedHex);
    }

    private String swipeBits(String bits){
        StringBuilder bitsInverted = new StringBuilder(bits);
        for (int index = 0; index < bits.length(); index++){
            if(bits.charAt(index) == '0')
                bitsInverted.setCharAt(index, '1');
            else
                bitsInverted.setCharAt(index, '0');
        }
        return String.valueOf(bitsInverted);
    }
}
