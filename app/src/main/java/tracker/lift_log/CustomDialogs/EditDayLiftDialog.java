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
 * Created by cvburnha on 10/26/2015.
 */
public class EditDayLiftDialog extends DialogFragment {



    String name;

    public interface EditDayLiftListener {
        public void onDialogPositiveClick(DialogFragment dialog, String newName);
    }


    public void setCallback(EditDayLiftListener mListener) {
        this.mListener = mListener;
    }

    EditDayLiftListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_editname, null);

        final EditText et_item_name = (EditText) view.findViewById(R.id.et_item_name);
        et_item_name.setText(this.name);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("FINISH", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /*TODO
                        complete this with a callback
                         */
                        String newName = et_item_name.getText().toString();
                        mListener.onDialogPositiveClick(EditDayLiftDialog.this, newName);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditDayLiftDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public void setName(String name) {
        this.name = name;
    }
}