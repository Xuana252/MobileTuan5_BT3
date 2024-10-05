package com.example.bt3;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int READ_CONTACTS_REQUEST_CODE = 100;
    private static final int ADD_CONTACT_REQUEST_CODE = 200;

    private static final int CONTACT_LOADER = 1;  // Add this constant for the Loader

    private boolean isASC = true;
    private ContactAdapter contactAdapter;

    private ActivityResultLauncher<Intent> addContactLauncher;

    private ActivityResultLauncher<Intent> editContactLauncher;
    private FloatingActionButton addButton;

    private Toolbar toolbar;

    static public boolean isSelectManyMode;

    List<Contact> contactList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, READ_CONTACTS_REQUEST_CODE);


        addButton = findViewById(R.id.addButton);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addButton.setEnabled(false);

        ListView contactListview = (ListView) findViewById(R.id.contactList);
        contactAdapter = new ContactAdapter(MainActivity.this, R.layout.contactitem, contactList);
        contactListview.setAdapter(contactAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getApplicationContext(), AddContactActivity.class);
                startActivity(newIntent);
            }
        });

        contactListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact selectedContact = contactList.get(position); // Get the selected class
                if (selectedContact != null) {
                    Intent intent = new Intent(MainActivity.this, EditContactActivity.class);
                    intent.putExtra("CONTACT", selectedContact); // Pass the selected contact object
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Error: Selected contact is null", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CONTACTS_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addButton.setEnabled(true);
                LoaderManager.getInstance(this).initLoader(CONTACT_LOADER, null, this);
                invalidateOptionsMenu();
            } else {
                addButton.setEnabled(false);
                Toast.makeText(this, "Permissions denied to read and write contacts, you might want to turn it on manually", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                item.setEnabled(false);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.ascending) {
            isASC = true;
            restartLoader();
        } else if (itemId == R.id.descending) {
            isASC = false;
            restartLoader();
        } else if (itemId == R.id.selectMany) {
            isSelectManyMode = !isSelectManyMode;
            contactAdapter.notifyDataSetChanged();
        } else if (itemId == R.id.deleteSelected) {
            deleteSelectedContacts();
            restartLoader();
        }
        return super.onOptionsItemSelected(item);
    }


    private void deleteSelectedContacts() {
        int deletedCount = 0;
        for (Contact contact : contactList) {
            if (contact.isSelected()) {
                deletedCount += deleteContact(contact.getId());
                // Optionally: Delete contact from the database
            }
        }
        Toast.makeText(this, deletedCount + " contacts deleted successfully", Toast.LENGTH_LONG).show();

    }

    private int deleteContact(String contactId) {
        // Prepare the URI for the contact to be deleted
        Uri contactUri = ContactsContract.Contacts.CONTENT_URI.buildUpon()
                .appendPath(contactId)
                .build();

        // Perform the deletion
        int rowsDeleted = getContentResolver().delete(contactUri, null, null);

        if (rowsDeleted > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Only restart loader if permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            restartLoader();
        }
    }

    private void restartLoader() {
        LoaderManager.getInstance(this).restartLoader(CONTACT_LOADER, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == CONTACT_LOADER) {
            String[] SELECTED_FIELDS = new String[]
                    {
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    };
            return new CursorLoader(this, ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    SELECTED_FIELDS,
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " " + (isASC ? "ASC" : "DESC"));
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == CONTACT_LOADER) {
            contactList = new ArrayList<>();
            if (data != null) {
                while (!data.isClosed() && data.moveToNext()) {
                    String id = data.getString(0);
                    String phone = data.getString(1);
                    String name = data.getString(2);
                    contactList.add(new Contact(id, phone, name));
                }
                contactAdapter.clear();
                contactAdapter.addAll(contactList);
                contactAdapter.notifyDataSetChanged();
                data.close();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        loader = null;
    }
}
