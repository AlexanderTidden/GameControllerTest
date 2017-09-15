package com.alexandertidden.gamecontrollertest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Alexander Tidden on 25.05.2017.
 */


public class GameControllerCheck extends View implements InputManager.InputDeviceListener {

    private final InputManager mInputManager;

    private long mLastStepTime;

    private String StringDeviceStatus;
    private String StringDpad;
    private String StringButtons;
    private String StringStick1X;
    private String StringStick1Y;
    private String StringStick2X;
    private String StringStick2Y;

    Paint paint1 = new Paint();

    public GameControllerCheck(Context context, AttributeSet attrs) {
        super(context);

        setFocusable(true);
        setFocusableInTouchMode(true);

        mInputManager = (InputManager)context.getSystemService(context.INPUT_SERVICE);
        mInputManager.registerInputDeviceListener(this,null);

        initStringsValues();
    }

    private void initStringsValues(){

        if (getGameControllerIds().isEmpty()) {
            StringDeviceStatus = "no Device connected";
        }
        else
        {
            StringDeviceStatus = "a Device is connected";
        }

        StringDpad = "init value";
        StringButtons = "init value";
        StringStick1X = "init value";
        StringStick1Y = "init value";
        StringStick2X = "init value";
        StringStick2Y = "init value";
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Update the animation
        animateFrame();

        // Write the text
        updateText(canvas);
    }

    private void animateFrame() {
        long currentStepTime = SystemClock.uptimeMillis();
        step(currentStepTime);
        invalidate();
    }

    private void step(long currentStepTime) {
        float tau = (currentStepTime - mLastStepTime) * 0.001f;
        mLastStepTime = currentStepTime;
    }

    private void updateText(Canvas canvas) {
        paint1.setStyle(Paint.Style.FILL);
        paint1.setColor(Color.BLUE);
        paint1.setTextSize(40);
        canvas.drawText("Device Status: " + StringDeviceStatus , 20, 50, paint1);
        canvas.drawText("D Pad: " + StringDpad , 20, 100, paint1);
        canvas.drawText("Buttons: " + StringButtons , 20, 150, paint1);
        canvas.drawText("Stick1: " + StringStick1X + " " + StringStick1Y , 20, 200, paint1);
        canvas.drawText("Stick2: " + StringStick2X + " " + StringStick2Y , 20, 250, paint1);
    }

    @Override
    public void onInputDeviceAdded(int deviceId) {
        StringDeviceStatus = "Input Device added";
    }

    @Override
    public void onInputDeviceRemoved(int deviceId) {
        StringDeviceStatus = "Input Device removed";
    }

    @Override
    public void onInputDeviceChanged(int deviceId) {
        StringDeviceStatus = "Input Device changed";
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        CheckKeyDown(keyCode, event);

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        CheckKeyUp(keyCode, event);

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {

        // Check that the event came from a joystick or gamepad since a generic
        // motion event could be almost anything. API level 18 adds the useful
        // event.isFromSource() helper function.
        int eventSource = event.getSource();
        if ((((eventSource & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) ||
                ((eventSource & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK))
                && event.getAction() == MotionEvent.ACTION_MOVE) {

            // Process all historical movement samples in the batch.
            final int historySize = event.getHistorySize();
            for (int i = 0; i < historySize; i++) {
                processJoystickInput(event, i);
            }

            // Process the current movement sample in the batch.
            processJoystickInput(event, -1);
        }
        return super.onGenericMotionEvent(event);
    }


    public ArrayList getGameControllerIds() {

        ArrayList gameControllerDeviceIds = new ArrayList();
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            int sources = dev.getSources();

            // Verify that the device has gamepad buttons, control sticks, or both.
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                    || ((sources & InputDevice.SOURCE_JOYSTICK)
                    == InputDevice.SOURCE_JOYSTICK)) {
                // This device is a game controller. Store its device ID.
                if (!gameControllerDeviceIds.contains(deviceId)) {
                    gameControllerDeviceIds.add(deviceId);
                }
            }
        }
        return gameControllerDeviceIds;
    }

    public void CheckKeyDown(int keyCode, KeyEvent event) {
        // Handle DPad keys and fire button on initial down but not on
        // auto-repeat.
        if (event.getRepeatCount() == 0) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    StringDpad = "left";
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    StringDpad = "right";
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    StringDpad = "up";
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    StringDpad = "down";
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    StringDpad = "center";
                    break;
                case KeyEvent.KEYCODE_SPACE:
                    StringButtons = "space";
                    break;
                case KeyEvent.KEYCODE_BUTTON_A:
                    StringButtons = "A";
                    break;
                case KeyEvent.KEYCODE_BUTTON_B:
                    StringButtons = "B";
                    break;
                case KeyEvent.KEYCODE_BUTTON_X:
                    StringButtons = "X";
                    break;
                case KeyEvent.KEYCODE_BUTTON_Y:
                    StringButtons = "Y";
                    break;
                case KeyEvent.KEYCODE_BUTTON_L1:
                    StringButtons = "L1";
                    break;
                case KeyEvent.KEYCODE_BUTTON_L2:
                    StringButtons = "L2";
                    break;
                case KeyEvent.KEYCODE_BUTTON_R1:
                    StringButtons = "R1";
                    break;
                case KeyEvent.KEYCODE_BUTTON_R2:
                    StringButtons = "R2";
                    break;
                default:
                    break;
            }
        }
    }

     public void CheckKeyUp(int keyCode, KeyEvent event) {
        // Handle DPad keys and fire button on initial down but not on
        // auto-repeat.
        if (event.getRepeatCount() == 0) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    StringDpad = "o";
                    break;
                case KeyEvent.KEYCODE_SPACE:
                case KeyEvent.KEYCODE_BUTTON_A:
                case KeyEvent.KEYCODE_BUTTON_B:
                case KeyEvent.KEYCODE_BUTTON_X:
                case KeyEvent.KEYCODE_BUTTON_Y:
                case KeyEvent.KEYCODE_BUTTON_L1:
                case KeyEvent.KEYCODE_BUTTON_L2:
                case KeyEvent.KEYCODE_BUTTON_R1:
                case KeyEvent.KEYCODE_BUTTON_R2:
                    StringButtons = "o";
                    break;
                default:
                    break;
            }
        }
    }
    private void processJoystickInput(MotionEvent event, int historyPos) {
        // Get joystick position.
        // Many game pads with two joysticks report the position of the
        // second
        // joystick
        // using the Z and RZ axes so we also handle those.
        // In a real game, we would allow the user to configure the axes
        // manually.
        InputDevice mInputDevice;
        mInputDevice = event.getDevice();

        float x1 = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_X, historyPos);
        float x2 = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_Z, historyPos);

        float y1 = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_Y, historyPos);
        float y2 = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_RZ, historyPos);

        StringStick1X = Float.toString(x1);
        StringStick1Y = Float.toString(y1);
        StringStick2X = Float.toString(x2);
        StringStick2Y = Float.toString(y2);
    }

    private static float getCenteredAxis(MotionEvent event, InputDevice device,
                                         int axis, int historyPos) {
        final InputDevice.MotionRange range = device.getMotionRange(axis, event.getSource());
        if (range != null) {
            final float flat = range.getFlat();
            final float value = historyPos < 0 ? event.getAxisValue(axis)
                    : event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            // A joystick at rest does not always report an absolute position of
            // (0,0).
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

}

