package com.example.a0lambj41.poiassignment;


import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;

public class AddPOIActivity extends Activity implements View.OnClickListener{

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_poi);

        Button marker = (Button)findViewById(R.id.addMarkerBtn);
        marker.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        EditText name = (EditText) findViewById(R.id.nameEditText);
        EditText type = (EditText) findViewById(R.id.typeEditText);
        EditText description = (EditText) findViewById(R.id.descriptionEditText);

        String newname = name.getText().toString();
        String newtype = type.getText().toString();
        String newdescription = description.getText().toString();

        bundle.putString("com.example.pointofinterestapp.name",newname);
        bundle.putString("com.example.pointofinterestapp.type",newtype);
        bundle.putString("com.example.pointofinterestapp.desc",newdescription);

        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
    }
}