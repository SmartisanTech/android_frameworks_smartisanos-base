/**
 * Copyright (c) 2016, The Smartisan Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package smartisanos.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.onestep.OneStepDragUtils;
import android.view.onestep.OneStepManager;

import java.io.File;
/**
 * The util class of smartisanos. for apps to do drag when in onestep mode.
 *
 * @deprecated use {@link android.view.onestep.OneStepDragUtils} and
 *  {@link android.view.onestep.OneStepManager}
 */
@Deprecated
public class SidebarUtils {
    public static boolean isSidebarShowing(Context context){
        OneStepManager oneStepManager = (OneStepManager) context.getSystemService("onestep");
        if (oneStepManager != null) {
            return oneStepManager.isInOneStepMode();
        }
        return false;
    }

    public static void dragText(View view, Context context, CharSequence text) {
        OneStepDragUtils.dragText(view, context, text);
    }

    public static void dragText(View view, Context context, CharSequence text, Bitmap background, Bitmap content, Bitmap avatar) {
        OneStepDragUtils.dragText(view, context, text, background, content, avatar);
    }

    public static void dragLink(View view, Context context, CharSequence link) {
        OneStepDragUtils.dragLink(view, context, link);
    }


    public static void dragFile(View view, Context context, File file, String mimeType, Bitmap background, Bitmap content, Bitmap avatar) {
        OneStepDragUtils.dragFile(view, context, file, mimeType, background, content, avatar);
    }

    public static void dragFile(View view, Context context, File file, String mimeType, String displayname){
        OneStepDragUtils.dragFile(view, context, file, mimeType, displayname);
    }

    public static void dragImage(View view, Context context, File file, String mimeType) {
        OneStepDragUtils.dragImage(view, context, file, mimeType);
    }

    public static void dragImage(View view, Context context, Bitmap content, File file, String mimeType) {
        OneStepDragUtils.dragImage(view, context, content, file, mimeType);
    }

    public static void dragMultipleImage(View view, Context context, File[] files, String[] mimeTypes) {
        OneStepDragUtils.dragMultipleImage(view, context, files, mimeTypes);
    }
}
