package appewtc.masterung.myrestaurant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by masterUNG on 2/13/15 AD.
 */
public class MyAlertDialog {

    AlertDialog.Builder objAlert;

    public void errorDialog(Context context, String strTitle, String strMessage) {

        objAlert = new AlertDialog.Builder(context);
        objAlert.setIcon(R.drawable.icon_question);
        objAlert.setTitle(strTitle);
        objAlert.setMessage(strMessage);
        objAlert.setCancelable(false);
        objAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        objAlert.show();

    }   // errorDialog

}   // Main Class
