package com.fast_prog.dynate.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.RegisterUser;
import com.fast_prog.dynate.models.UploadFiles;
import com.fast_prog.dynate.utilities.ConnectionDetector;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.JsonParser;
import com.fast_prog.dynate.utilities.MyCircularProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class SocialMediaActivity extends AppCompatActivity {

    Button registerButton;
    Button buttonId;
    Button buttonCarForm;
    Button buttonCard;
    Button buttonOther;

    ImageView imageviewId;
    ImageView imageviewCarForm;
    ImageView imageviewCard;
    ImageView imageviewOther;

    RegisterUser registerUser;

    private static int RESULT_LOAD_IMAGE = 101;
    private static int TAKE_PHOTO_CODE = 102;

    CoordinatorLayout coordinatorLayout;

    Snackbar snackbar;

    Typeface face;

    MyCircularProgressDialog myCircularProgressDialog;

    SharedPreferences sharedPreferences;

    AlertDialog alertDialog;

    String selected;

    UploadFiles uploadFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home_up_icon));

        face = Typeface.createFromAsset(SocialMediaActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        registerUser = (RegisterUser) getIntent().getSerializableExtra("registerUser");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SocialMediaActivity.this);
                LayoutInflater inflater = SocialMediaActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.alert_dialog, null);
                builder.setView(view);
                TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
                txtAlert.setText(R.string.filled_data_will_be_lost);
                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
                Button btnOK = (Button) view.findViewById(R.id.btn_ok);
                btnOK.setTypeface(face);
                Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
                btnCancel.setTypeface(face);
                txtAlert.setTypeface(face);
                alertDialog.show();
            }
        });

        String title = getResources().getString(R.string.register_title);
        TextView titleTextView = new TextView(getApplicationContext());
        titleTextView.setText(title);
        titleTextView.setTextSize(16);
        titleTextView.setAllCaps(true);
        titleTextView.setTypeface(face, Typeface.BOLD);
        titleTextView.setTextColor(Color.WHITE);
        getSupportActionBar().setCustomView(titleTextView);

        registerButton = (Button) findViewById(R.id.btn_register);
        registerButton.setTypeface(face);

        buttonId = (Button) findViewById(R.id.button_id);
        buttonId.setTypeface(face);

        buttonCarForm = (Button) findViewById(R.id.button_carform);
        buttonCarForm.setTypeface(face);

        buttonCard = (Button) findViewById(R.id.button_card);
        buttonCard.setTypeface(face);

        buttonOther = (Button) findViewById(R.id.button_other);
        buttonOther.setTypeface(face);

        imageviewId = (ImageView) findViewById(R.id.imageview_id);
        imageviewCarForm = (ImageView) findViewById(R.id.imageview_carform);
        imageviewCard = (ImageView) findViewById(R.id.imageview_card);
        imageviewOther = (ImageView) findViewById(R.id.imageview_other);

        buttonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = "id";
                selectImage(SocialMediaActivity.this);
            }
        });

        buttonCarForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = "car_form";
                selectImage(SocialMediaActivity.this);
            }
        });

        buttonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = "card";
                selectImage(SocialMediaActivity.this);
            }
        });

        buttonOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = "other";
                selectImage(SocialMediaActivity.this);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    if (ConnectionDetector.isConnected(SocialMediaActivity.this)) {
                        new SendOTPDMBackground().execute();
                    } else {
                        ConnectionDetector.errorSnackbar(coordinatorLayout);
                    }
                }
            }
        });

        uploadFiles = new UploadFiles();
    }

    private class SendOTPDMBackground extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
                myCircularProgressDialog = new MyCircularProgressDialog(SocialMediaActivity.this);
                myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                myCircularProgressDialog.setCancelable(false);
                myCircularProgressDialog.show();
            }
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgMobNo", "966"+registerUser.mobile);
            params.put("ArgIsDB", "false");

            String BASE_URL = Constants.BASE_URL_EN + "SendOTPDM";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "SendOTPDM";
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params);
        }

        protected void onPostExecute(final JSONObject response) {
            myCircularProgressDialog.dismiss();

            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        VerifyOTPActivity.otpExtra = response.getJSONArray("data").getJSONObject(0).getString("OTP");
                        VerifyOTPActivity.registerUserExtra = registerUser;
                        VerifyOTPActivity.uploadFiles = uploadFiles;
                        startActivity(new Intent(SocialMediaActivity.this, VerifyOTPActivity.class));

                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(SocialMediaActivity.this);
                        LayoutInflater inflater1 = SocialMediaActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(response.getString("message"));
                        alertDialog = builder1.create();
                        alertDialog.setCancelable(false);
                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
                        btnOk.setText(R.string.ok);
                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                        btnOk.setTypeface(face);
                        txtAlert1.setTypeface(face);
                        alertDialog.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                snackbar = Snackbar.make(coordinatorLayout, R.string.network_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        });
                snackbar.show();
            }
        }
    }

    boolean validate() {

        if (uploadFiles.imageName1 == null || uploadFiles.imageName1.isEmpty()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(SocialMediaActivity.this);
            LayoutInflater inflater1 = SocialMediaActivity.this.getLayoutInflater();
            final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
            builder1.setView(view1);
            TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
            txtAlert1.setText(getResources().getString(R.string.id_not_selected));
            alertDialog = builder1.create();
            alertDialog.setCancelable(false);
            view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
            btnOk.setText(R.string.ok);
            view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            btnOk.setTypeface(face);
            txtAlert1.setTypeface(face);
            alertDialog.show();

            return false;
        }

        if (uploadFiles.imageName2 == null || uploadFiles.imageName2.isEmpty()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(SocialMediaActivity.this);
            LayoutInflater inflater1 = SocialMediaActivity.this.getLayoutInflater();
            final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
            builder1.setView(view1);
            TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
            txtAlert1.setText(getResources().getString(R.string.carform_not_selected));
            alertDialog = builder1.create();
            alertDialog.setCancelable(false);
            view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
            btnOk.setText(R.string.ok);
            view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            btnOk.setTypeface(face);
            txtAlert1.setTypeface(face);
            alertDialog.show();

            return false;
        }

        if (uploadFiles.imageName3 == null || uploadFiles.imageName3.isEmpty()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(SocialMediaActivity.this);
            LayoutInflater inflater1 = SocialMediaActivity.this.getLayoutInflater();
            final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
            builder1.setView(view1);
            TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
            txtAlert1.setText(getResources().getString(R.string.card_not_selected));
            alertDialog = builder1.create();
            alertDialog.setCancelable(false);
            view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
            btnOk.setText(R.string.ok);
            view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            btnOk.setTypeface(face);
            txtAlert1.setTypeface(face);
            alertDialog.show();

            return false;
        }

        if (uploadFiles.imageName4 == null || uploadFiles.imageName4.isEmpty()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(SocialMediaActivity.this);
            LayoutInflater inflater1 = SocialMediaActivity.this.getLayoutInflater();
            final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
            builder1.setView(view1);
            TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
            txtAlert1.setText(getResources().getString(R.string.other_not_selected));
            alertDialog = builder1.create();
            alertDialog.setCancelable(false);
            view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
            btnOk.setText(R.string.ok);
            view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            btnOk.setTypeface(face);
            txtAlert1.setTypeface(face);
            alertDialog.show();

            return false;
        }

        return true;
    }

    private void selectImage(final Context context) {
        final CharSequence[] options = {context.getResources().getString(R.string.take_photo), context.getResources().getString(R.string.choose_from_gallery), context.getResources().getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(context.getResources().getString(R.string.take_photo))) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);

                } else if (options[item].equals(context.getResources().getString(R.string.choose_from_gallery))) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);

                } else if (options[item].equals(context.getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            //Bitmap bm = BitmapFactory.decodeFile(picturePath);
            //bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            File f = new File(picturePath);
            Bitmap bm = decodeFile(f);
            bm = scaleDownBitmap(bm, 400);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] byteImage_photo = baos.toByteArray();

            addToClass(bm, byteImage_photo);

        } else if(requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK && null != data) {
            Bitmap bm = (Bitmap) data.getExtras().get("data");
            //ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //assert bm != null;
            //bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            //byte[] byteImage_photo = baos.toByteArray();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "dynate.jpg");

            try {
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(baos.toByteArray());

                Bitmap bm1 = decodeFile(f);
                bm1 = scaleDownBitmap(bm1, 400);
                bm1.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteImage_photo = baos.toByteArray();

                addToClass(bm, byteImage_photo);

                fo.flush();
                fo.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addToClass(Bitmap bm, byte[] byteImage_photo) {
        if (selected.equalsIgnoreCase("id")) {
            uploadFiles.bm1 = bm;
            uploadFiles.base64Encoded1 = Base64.encodeToString(byteImage_photo,Base64.DEFAULT);
            uploadFiles.imageName1 = selected;
            imageviewId.setImageBitmap(bm);

        } else if (selected.equalsIgnoreCase("car_form")) {
            uploadFiles.bm2 = bm;
            uploadFiles.base64Encoded2 = Base64.encodeToString(byteImage_photo, Base64.DEFAULT);
            uploadFiles.imageName2 = selected;
            imageviewCarForm.setImageBitmap(bm);

        } else if (selected.equalsIgnoreCase("card")) {
            uploadFiles.bm3 = bm;
            uploadFiles.base64Encoded3 = Base64.encodeToString(byteImage_photo, Base64.DEFAULT);
            uploadFiles.imageName3 = selected;
            imageviewCard.setImageBitmap(bm);

        } else {
            uploadFiles.bm4 = bm;
            uploadFiles.base64Encoded4 = Base64.encodeToString(byteImage_photo, Base64.DEFAULT);
            uploadFiles.imageName4 = selected;
            imageviewOther.setImageBitmap(bm);
        }
    }

    public Bitmap scaleDownBitmap(Bitmap photo, int newHeight) {
        final float densityMultiplier = getResources().getDisplayMetrics().density;
        int h = (int) (newHeight*densityMultiplier);
        int w = (int) (h * photo.getWidth()/((double) photo.getHeight()));
        photo = Bitmap.createScaledBitmap(photo, w, h, true);
        return photo;
    }

    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 500;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);

        } catch (FileNotFoundException ignored) {
        }
        return null;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (alertDialog != null && alertDialog.isShowing()){
            alertDialog.cancel();
        }

        if (myCircularProgressDialog != null && myCircularProgressDialog.isShowing()) {
            myCircularProgressDialog.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SocialMediaActivity.this);
        LayoutInflater inflater = SocialMediaActivity.this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(view);
        TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
        txtAlert.setText(R.string.filled_data_will_be_lost);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });
        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        btnOK.setTypeface(face);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setTypeface(face);
        txtAlert.setTypeface(face);
        alertDialog.show();
    }

}
