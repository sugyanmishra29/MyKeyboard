package com.mint.mykeyboard;

import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.Log;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import kotlin.jvm.internal.Intrinsics;

import static android.os.Build.*;

// declaring Service to run the keyboard
public class MyKeyboardService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private FloatingKeyboardView kv;
    private Keyboard keyboard;
    private boolean isCaps = false;
    private FloatingKeyboardView fkv;
    private String TAG = "Keyboard";
    private View mFloatingView;
    private WindowManager windowManager;
//    private WindowManager.LayoutParams windowParams;
//    private Context context;
//    private View rootView;
////    private WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
//    private LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//    private final DisplayMetrics getCurrentDisplayMetrics() {
//        DisplayMetrics dm = new DisplayMetrics();
//        this.windowManager.getDefaultDisplay().getMetrics(dm);
//        return dm;
//    }

//    private final void calculateSizeAndPosition(ViewGroup.LayoutParams params, int widthInDp, int heightInDp) {
//        DisplayMetrics dm = this.getCurrentDisplayMetrics();
////        params.gravity = 51;
//        params.width = (int)((float)widthInDp * dm.density);
//        params.height = (int)((float)heightInDp * dm.density);
////        params.x = (dm.widthPixels - params.width) / 2;
////        params.y = (dm.heightPixels - params.height) / 2;
//    }
//    private final void initWindowParams() {
//        this.calculateSizeAndPosition(this.windowParams, 300, 80);
//    }
//
//    private final void initWindow() {
//        this.rootView.findViewById(1000370).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
//            public final void onClick(View it) {
//                Window.this.close();
//            }
//        }));
//        this.rootView.findViewById(1000193).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
//            public final void onClick(View it) {
//                Toast.makeText(Window.this.context, (CharSequence)"Adding notes to be implemented.", 0).show();
//            }
//        }));
//    }
//
//    public final void open() {
//        try {
//            this.windowManager.addView(this.rootView, (android.view.ViewGroup.LayoutParams)this.windowParams);
//        } catch (Exception var2) {
//        }
//
//    }
//
//    public final void close() {
//        try {
//            this.windowManager.removeView(this.rootView);
//        } catch (Exception var2) {
//        }
//    }
//
//    public void Window(@NotNull Context context) {
////        super();
//        Intrinsics.checkNotNullParameter(context, "context");
//        this.context = context;
//        Object var10001 = this.context.getSystemService(Context.WINDOW_SERVICE);
//        if (var10001 == null) {
//            throw new NullPointerException("null cannot be cast to non-null type android.view.WindowManager");
//        } else {
//            this.windowManager = (WindowManager)var10001;
//            var10001 = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            if (var10001 == null) {
//                throw new NullPointerException("null cannot be cast to non-null type android.view.LayoutInflater");
//            } else {
//                this.layoutInflater = (LayoutInflater)var10001;
//                this.rootView = this.layoutInflater.inflate(1300109, (ViewGroup)null);
//                this.windowParams = new WindowManager.LayoutParams(0, 0, 0, 0, VERSION.SDK_INT >= 26 ? 2038 : 2002, 262696, -3);
//                this.initWindowParams();
//                this.initWindow();
//            }
//        }
//    }

    @Override
    public View onCreateInputView() {
        Log.d(TAG, ": onCreateInputView called");
        kv = (FloatingKeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this,R.xml.qwerty);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        Log.d(TAG,": Keyboard set");
        return kv;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatingView = FloatingKeyboardView.inflate(getApplicationContext(), R.layout.keyboard, null);

        //Add the view to the window.
        final WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        wmParams.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        wmParams.x = 0;
        wmParams.y = 100;

        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE); // app is crashing
        windowManager.addView(mFloatingView, wmParams);
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int i, int[] ints) {
        InputConnection ic = getCurrentInputConnection();
        if(ic!=null){
            playClick(i);
//            vb.vibrate(40);
            switch (i)
            {
                case Keyboard.KEYCODE_DELETE:
                    ic.deleteSurroundingText(1,0);
                    break;
                case Keyboard.KEYCODE_SHIFT:
                    isCaps = !isCaps;
                    keyboard.setShifted(isCaps);
                    kv.invalidateAllKeys();
                    break;
                case Keyboard.KEYCODE_DONE:
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_ENTER));
                    break;
                default:
                    char code = (char)i;
                    if(Character.isLetter(code) && isCaps)
                        code = Character.toUpperCase(code);
                    ic.commitText(String.valueOf(code),1);
            }
        }
        else
            stopSelf();
    }
    private void playClick(int i) {

        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch(i)
        {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default: am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }
    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}

