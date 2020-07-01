package com.sust.kinblooddemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialoguePassword extends AppCompatDialogFragment {

    private EditText password;
    private DialoguePasswordListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogue_layout, null);

        builder.setView(view)
                .setTitle("Enter password to confirm")
                .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss()).setPositiveButton("Update", (dialog, which) -> {
                    String password_ = password.getText().toString();
                    listener.applyText(password_);
                });

        password = view.findViewById(R.id.password);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialoguePasswordListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must impliment DialoguePasswordListener");
        }
    }

    public interface DialoguePasswordListener{
        void applyText(String password);
    }
}
