package com.hactiv8.e_commerceproject2.ui.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hactiv8.e_commerceproject2.Constants;
import com.hactiv8.e_commerceproject2.R;

import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {

    //ui views
    private ImageButton back;
    private ImageView logoProduct;
    private EditText title2, desc, quantity, harga, hargaDiskon, DiskonNote;
    private TextView category;
    private SwitchCompat diskon;
    private Button addBtn;
    //permission constants
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //image pick const
    private static final int IMAGE_PICK_GALERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    //permission arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;
    //permission url
    private Uri image_uri;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        //init ui views
        back = findViewById(R.id.back);
        logoProduct = findViewById(R.id.logoProduct);
        title2 = findViewById(R.id.title2);
        desc = findViewById(R.id.desc);
        quantity = findViewById(R.id.quantity);
        harga = findViewById(R.id.harga);
        hargaDiskon = findViewById(R.id.hargaDiskon);
        DiskonNote = findViewById(R.id.DiskonNote);
        category = findViewById(R.id.category);
        diskon = findViewById(R.id.diskon);
        addBtn =  findViewById(R.id.addProduct);

        hargaDiskon.setVisibility(View.GONE);
        DiskonNote.setVisibility(View.GONE);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //setup progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu Sebentar...");
        progressDialog.setCanceledOnTouchOutside(false);

        //init permission arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        diskon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //cek, show
                    hargaDiskon.setVisibility(View.VISIBLE);
                    DiskonNote.setVisibility(View.VISIBLE);
                }
                else{
                    hargaDiskon.setVisibility(View.GONE);
                    DiskonNote.setVisibility(View.GONE);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        logoProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show dialog to pick image
                showImagePickDialog();
            }
        });

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick category
                categoryDialog();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //flow
                inputData();
            }
        });
    }

    private String productTitle, productDesc, productCategory, productQuantity, originalPrice, discountPrice, discountNote;
    private boolean discountAvailable = false;
    private void inputData() {
        //input data
        productTitle = title2.getText().toString().trim();
        productDesc = desc.getText().toString().trim();
        productCategory = category.getText().toString().trim();
        productQuantity = quantity.getText().toString().trim();
        originalPrice = harga.getText().toString().trim();
        discountAvailable = diskon.isChecked(); //true /false


        //validate data
        if (TextUtils.isEmpty(productTitle)){
            Toast.makeText(this, "Title is required..", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productCategory)){
            Toast.makeText(this, "Category is required..", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(originalPrice)){
            Toast.makeText(this, "Price is required..", Toast.LENGTH_SHORT).show();
            return;
        }
        if (discountAvailable){
            //product diskon
            discountPrice = hargaDiskon.getText().toString().trim();
            discountNote = DiskonNote.getText().toString().trim();
            if (TextUtils.isEmpty(discountPrice)){
                Toast.makeText(this, "Discount Price is required..", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else{
            //product tidak diskon
            discountPrice = "0";
            discountNote = "";
        }

        addProduct();
    }

    private void addProduct() {
        progressDialog.setMessage("Add Product...");
        progressDialog.show();

        final String timestamp = "" +System.currentTimeMillis();

        if (image_uri ==  null){
            //up tanpa image/
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("productId", ""+timestamp);
            hashMap.put("productTitle", ""+productTitle);
            hashMap.put("productDesc", ""+productDesc);
            hashMap.put("productCategory", ""+productCategory);
            hashMap.put("productQuantity", ""+productQuantity);
            hashMap.put("logoProduct", ""); //set empty
            hashMap.put("originalPrice", ""+originalPrice);
            hashMap.put("discountPrice", ""+discountPrice);
            hashMap.put("discountNote", ""+discountNote);
            hashMap.put("discountAvailable", ""+discountAvailable);
            hashMap.put("timestamp", timestamp);
            hashMap.put("uid", ""+firebaseAuth.getUid());

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product");
            reference.child(""+timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //add to db
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, "Tambah Produk...", Toast.LENGTH_SHORT).show();
                            clearData();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            String filePathAndName = "product_images/" + "" + timestamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("productId", ""+timestamp);
                                hashMap.put("productTitle", ""+productTitle);
                                hashMap.put("productDesc", ""+productDesc);
                                hashMap.put("productCategory", ""+productCategory);
                                hashMap.put("productQuantity", ""+productQuantity);
                                hashMap.put("logoProduct", ""+downloadImageUri);
                                hashMap.put("originalPrice", ""+originalPrice);
                                hashMap.put("discountPrice", ""+discountPrice);
                                hashMap.put("discountNote", ""+discountNote);
                                hashMap.put("discountAvailable", ""+discountAvailable);
                                hashMap.put("timestamp", timestamp);
                                hashMap.put("uid", ""+firebaseAuth.getUid());

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product");
                                reference.child(""+timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //add to db
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this, "Tambah Produk...", Toast.LENGTH_SHORT).show();
                                                clearData();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void clearData() {
        title2.setText("");
        desc.setText("");
        category.setText("");
        quantity.setText("");
        harga.setText("");
        hargaDiskon.setText("");
        DiskonNote.setText("");
        logoProduct.setImageResource(R.drawable.addproduct);
        image_uri = null;

    }

    private void categoryDialog() {
        //dialog
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constants.productCategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get picked category
                        String categorys = Constants.productCategories[which];

                        // set picked categpry
                        category.setText(categorys);
                    }
                })
                .show();
    }

    private void showImagePickDialog() {
        //options to display in dialog
        String[] options = {"Camera", "Galery" };
        //dialog
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle item clik
                        if (which==0){
                            //camera
                            if (checkCameraPermission()){
                                pickFromCamera();
                            }
                            else {
                                requestCameraPermission();
                            }
                        }
                        else {
                            if (checkStoragePermission()){
                                pickFromGalery();
                            }
                            else {
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();

    }

    private void pickFromGalery(){
        //intent to pick image dari galery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALERY_CODE);
    }

    private void pickFromCamera(){
        //intent image to camera

        //using media store
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image_Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        //both permission granted
                        pickFromCamera();
                    }
                    else{
                        //both or one permissions denied
                        Toast.makeText(this, "Camera && Storage permission are required...", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        pickFromGalery();
                    }else{
                        Toast.makeText(this, "Storage permission is required...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //handle image pick result

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALERY_CODE){
                image_uri = data.getData();

                logoProduct.setImageURI(image_uri);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){

                logoProduct.setImageURI(image_uri);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}