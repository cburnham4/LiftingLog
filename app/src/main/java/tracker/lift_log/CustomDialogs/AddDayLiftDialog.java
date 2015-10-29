package tracker.lift_log.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import tracker.lift_log.R;

/**
 * Created by cvburnha on 10/29/2015.
 */
public class AddDayLiftDialog extends DialogFragment {


    public interface AddDayLiftListener {
        public void onDialogPositiveClick(String newName);
    }


    public void setCallback(AddDayLiftListener mListener) {
        this.mListener = mListener;
    }

    AddDayLiftListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_addmuscle, null);

        final EditText et_item_name = (EditText) view.findViewById(R.id.et_item_name);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /*TODO
                        complete this with a callback
                         */
                        String newName = et_item_name.getText().toString();
                        mListener.onDialogPositiveClick(newName);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddDayLiftDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
