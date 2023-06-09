package me.driss.test1;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class HideClavier {

    public static void hideKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}
