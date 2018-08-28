package ccoderad.bnds.shiyiquanevent.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.db.annotation.NotNull;

/**
 * Created by CCoderAD on 2016/12/26.
 */

public class ViewTools {

    public static View Inflate(Context context, int id, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(id, parent, false);
    }

    public static void ToastInfo(Context context, String msg, boolean islong) {
        Toast.makeText(context, msg, islong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static Toast MakeToast(Context context, String msg, boolean islong) {
        return Toast.makeText(context, msg, islong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
    }

    public AlertDialog createDefaultAlert(Context context, @Nullable View header
            , View content, String positiveText, String negativeText
            , DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (header != null) {
            builder.setCustomTitle(header);
        }
        if(content != null){
            builder.setView(content);
        }
        builder.setPositiveButton(positiveText, positiveListener);
        builder.setNegativeButton(negativeText, negativeListener);
        return builder.create();
    }
}
