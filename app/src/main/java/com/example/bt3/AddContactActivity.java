package com.example.bt3;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddContactActivity extends AppCompatActivity {
    private EditText contactName;
    private EditText contactNumber;

    private Button addButton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_create);

        contactName = findViewById(R.id.contactName);
        contactNumber = findViewById(R.id.contactNumber);

        addButton = findViewById(R.id.submitButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contactName!=null&&contactNumber!=null) {
                    String newContactName = contactName.getText().toString();
                    String newContactNumber = contactNumber.getText().toString();

                    if(newContactName.isEmpty()||newContactNumber.isEmpty()) {
                        Toast.makeText(getApplicationContext(),"please fill all the field before submitting",Toast.LENGTH_LONG).show();
                    } else {
                        addContact(newContactName, newContactNumber);
                        finish(); // Close this activity
                    }
                }
            }
        });
    }
    private void addContact(String name, String number) {
        // Create a new contact
        ContentValues values = new ContentValues();
        values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, (String) null);
        values.put(ContactsContract.RawContacts.ACCOUNT_NAME, (String) null);

        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);

        // Insert the contact's name
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

        // Insert the contact's phone number
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

        Toast.makeText(this, "Contact added successfully!", Toast.LENGTH_SHORT).show();

    }
}
