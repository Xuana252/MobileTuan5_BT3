package com.example.bt3;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditContactActivity extends AppCompatActivity {
    private EditText contactName;
    private EditText contactNumber;
    private Button editButton;

    private Contact selectedContact;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_detail);


        Intent intent = getIntent();
        selectedContact = (Contact) intent.getSerializableExtra("CONTACT");

        contactName = findViewById(R.id.contactName);
        contactNumber = findViewById(R.id.contactNumber);

        contactName.setText(selectedContact.getName());
        contactNumber.setText(selectedContact.getNumber());


        editButton = findViewById(R.id.submitButton);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contactName!=null&&contactNumber!=null) {
                    String newContactName = contactName.getText().toString();
                    String newContactNumber = contactNumber.getText().toString();

                    if(newContactName.isEmpty()||newContactNumber.isEmpty()) {
                        Toast.makeText(getApplicationContext(),"please fill all the field before submitting",Toast.LENGTH_LONG).show();
                    } else {
                        editContact(selectedContact.getId(),newContactName, newContactNumber);
                        finish(); // Close this activity
                    }
                }
            }
        });
    }
    private void editContact(String id,String name, String number) {
        // Create a new contact
        ContentValues nameValues = new ContentValues();
        nameValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,name);

        ContentValues phoneValues = new ContentValues();
        phoneValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER,number);



        Uri nameUri = ContactsContract.Data.CONTENT_URI;
        String nameSelection = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " +
                ContactsContract.Data.MIMETYPE + " = ?";
        String[] nameSelectionArgs = new String[]{
                id,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
        };

        int nameUpdateCount = getContentResolver().update(nameUri, nameValues, nameSelection, nameSelectionArgs);

        String phoneSelection = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " +
                ContactsContract.Data.MIMETYPE + " = ?";
        String[] phoneSelectionArgs = new String[]{
                id,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
        };

        // Update the contact's phone number
        int phoneUpdateCount = getContentResolver().update(nameUri, phoneValues, phoneSelection, phoneSelectionArgs);

        if (nameUpdateCount > 0 || phoneUpdateCount > 0) {
            Toast.makeText(this, "Contact updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update contact.", Toast.LENGTH_SHORT).show();
        }

    }
}