package com.example.bt3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {
    int resource;
    private List<Contact> contacts;

    public ContactAdapter(Context context, int resource, List<Contact> contacts) {
        super(context, resource, contacts);
        this.contacts = contacts;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(this.getContext());
            v = vi.inflate(this.resource, null);
        }
        Contact s = getItem(position);

        if (s != null) {
            TextView contactID = (TextView) v.findViewById(R.id.contactId);
            TextView contactName = (TextView) v.findViewById(R.id.contactName);
            TextView contactNumber = (TextView) v.findViewById(R.id.contactNumber);
            CheckBox contactCheckbox = (CheckBox) v.findViewById(R.id.contactCheckbox);

            if (contactID != null) {
                contactID.setText(s.getId());
            }
            if (contactNumber != null) {
                contactNumber.setText(s.getNumber());
            }
            if (contactName != null) {
                contactName.setText(s.getName());
            }
            if (MainActivity.isSelectManyMode) { // Assuming isSelectManyMode is a static variable
                contactCheckbox.setVisibility(View.VISIBLE);
                contactCheckbox.setChecked(s.isSelected());
                contactCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    s.setSelected(isChecked); // Update the selection state
                });
            } else {
                contactCheckbox.setVisibility(View.GONE);
                contactCheckbox.setChecked(false); // Clear selection if not in select mode
            }

            contactCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                s.setSelected(isChecked);
            });

        }
        return v;
    }

}