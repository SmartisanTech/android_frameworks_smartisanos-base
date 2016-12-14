package smartisanos.api;

import android.content.IntentSmtImpl;

public class IntentSmt {

    public static final String EXTRA_SMARTISAN_ANIM_RESOURCE_ID = IntentSmtImpl.EXTRA_SMARTISAN_ANIM_RESOURCE_ID;

    public static final String ACTION_SM_SQUEEZE_SHORTCUTS = IntentSmtImpl.ACTION_SM_SQUEEZE_SHORTCUTS;

    public static final int FLAG_RECEIVER_SM_USER_AWARE = IntentSmtImpl.FLAG_RECEIVER_SM_USER_AWARE;

    public static final String EXTRA_SM_SQUEEZE_SHORTCUTS = IntentSmtImpl.EXTRA_SM_SQUEEZE_SHORTCUTS;

    public static final String EXTRA_SMARTISAN_KEYGUARD_LAUNCH_CAMERA = IntentSmtImpl.EXTRA_SMARTISAN_KEYGUARD_LAUNCH_CAMERA;

    public static final String ACTION_STATUS_BAR_CLICKED = IntentSmtImpl.ACTION_STATUS_BAR_CLICKED;

    /** start: 临时方法，各应用先调用以下方法，不要直接访问上面的常量，等smartisanos共线后再通知大家修改 */
    public static final String get_EXTRA_SMARTISAN_ANIM_RESOURCE_ID(){
        return EXTRA_SMARTISAN_ANIM_RESOURCE_ID;
    }

    public static final String get_ACTION_SM_SQUEEZE_SHORTCUTS(){
        return ACTION_SM_SQUEEZE_SHORTCUTS;
    }

    public static final int get_FLAG_RECEIVER_SM_USER_AWARE(){
        return FLAG_RECEIVER_SM_USER_AWARE;
    }

    public static final String get_EXTRA_SM_SQUEEZE_SHORTCUTS(){
        return EXTRA_SM_SQUEEZE_SHORTCUTS;
    }

    public static final String get_EXTRA_SMARTISAN_KEYGUARD_LAUNCH_CAMERA(){
        return EXTRA_SMARTISAN_KEYGUARD_LAUNCH_CAMERA;
    }

    public static final String get_ACTION_STATUS_BAR_CLICKED(){
        return ACTION_STATUS_BAR_CLICKED;
    }
    /** end: 临时方法，各应用先调用以下方法，不要直接访问上面的常量，等smartisanos共线后再通知大家修改 */
}
