import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.dispatcher.VoidDispatchService;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.lang.reflect.Field;

// See: https://github.com/kwhat/jnativehook/blob/2.2/doc/ConsumingEvents.md

public class ConsumeEvent implements NativeKeyListener {
    public ConsumeEvent() throws NativeHookException {

        // Basically saying to not queue this anywhere - return native hook right now
        // Important if wa want to mutate the event before JNativeHook/OS does anything with it
        GlobalScreen.setEventDispatcher(new VoidDispatchService());

        // Register with the OS
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(this);
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        if(e.getKeyCode() == NativeKeyEvent.VC_B) {
            System.out.println("Trying to consume B event");
            try {
                Field f = NativeInputEvent.class.getDeclaredField("reserved");
                f.setAccessible(true);
                f.setShort(e, (short) 0x01);
                System.out.println("Native key successfully captured");
            } catch (Exception ex) {
                System.out.println("FAIL nativeKeyPressed");
                ex.printStackTrace();
            }
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
        if(e.getKeyCode() == NativeKeyEvent.VC_B) {
            try {
                Field f = NativeInputEvent.class.getDeclaredField("reserved");
                f.setAccessible(true);
                f.setShort(e, (short) 0x01);
                System.out.println("Native key successfully released");
            } catch (Exception ex) {
                System.out.println("FAIL nativeKeyReleased");
                ex.printStackTrace();
            }
        }
    }

    public void nativeKeyTyped(NativeKeyEvent e) { /* Unimplemented */ }
}
