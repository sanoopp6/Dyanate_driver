package com.fast_prog.dynate.views;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.fast_prog.dynate.R;
import com.fast_prog.dynate.models.RegisterUser;
import com.fast_prog.dynate.utilities.ConnectionDetector;
import com.fast_prog.dynate.utilities.Constants;
import com.fast_prog.dynate.utilities.GPSTracker;
import com.fast_prog.dynate.utilities.JsonParser;
import com.fast_prog.dynate.utilities.MyCircularProgressDialog;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    EditText nameEditText;
    EditText mailEditText;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText confPasswordEditText;
    EditText mobileEditText;

    CheckBox iAgreeCheckBox;

    Button addressEditText;

    ToggleButton toggleWithGlass;
    ToggleButton toggleWithCompany;

    Spinner vehicleSizeSpinner;
    Spinner vehicleModelSpinner;
    Spinner vehicleTypeSpinner;
    Spinner vehicleCompanySpinner;

    TextView termsConditionsTextView;
    TextView nameTitleText;
    TextView mobileTitleText;
    TextView emailTitleText;
    TextView usernameTitleText;
    TextView passwordTitleText;
    TextView confPasswordTitleText;
    TextView vSizeTitleText;
    TextView vModelTitleText;
    TextView vTypeTitleText;
    TextView withGlassText1;
    TextView withGlassText2;
    TextView withCompanyText;
    TextView pickAddressButton;

    Button registerButton;
    //Button attachPicButton;

    RegisterUser registerUser;

    String name;
    String mobile;
    String mail;
    String address;
    String username;
    String password;
    String vSizeName;
    String vModelName;
    String vTypeName;
    String vCompName;

    boolean withGlass;
    boolean withCompany;

    int vModelId;
    int vTypeId;
    int vCompId;
    int vSizeId;

    JSONArray vehicleSizeArray;
    JSONArray vehicleModelArray;
    JSONArray vehicleTypeArray;
    JSONArray vehicleCompanyArray;

    List<String> vehicleSizeDataList;
    List<String> vehicleSizeIdList;
    List<String> vehicleModelDataList;
    List<String> vehicleModelIdList;
    List<String> vehicleTypeDataList;
    List<String> vehicleTypeIdList;
    List<String> vehicleCompanyDataList;
    List<String> vehicleCompanyIdList;

    ArrayAdapter<String> vehicleSizeAdapter;
    ArrayAdapter<String> vehicleModelAdapter;
    ArrayAdapter<String> vehicleTypeAdapter;
    ArrayAdapter<String> vehicleCompanyAdapter;

    double longitude;
    double latitude;

    GPSTracker gpsTracker;

    int REQUEST_CODE = 1122;

//    private static int RESULT_LOAD_IMAGE = 101;
//    private static int TAKE_PHOTO_CODE = 102;

    CoordinatorLayout coordinatorLayout;

    Snackbar snackbar;

    static Typeface face;

//    List<UploadFiles> uploadFiles;
//    RecyclerView filesRecyclerView;
//    LinearLayoutManager filesLayoutManager;
//    RecyclerView.Adapter mFilesAdapter;

    LinearLayout companySpinnerLayout;

    private PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.getInstance();

    boolean isFilled = false;

    MyCircularProgressDialog myCircularProgressDialog;

    SharedPreferences sharedPreferences;

    AlertDialog alertDialog;

//    ImageView imgWorkWithGlass;
//    String nameAr;
//    EditText nameArEditText;
//    EditText licenseEditText;
//    EditText licenseArEditText;
//    RadioButton radioButtonYes;
//    RadioButton radioButtonNo;
//    RadioGroup radioGroupWorkWithGlass;
//    TextView titleText;
//    TextView nameArTitleText;
//    TextView licenseTitleText;
//    TextView licenseArTitleText;
//    IntlPhoneInput phoneInputView;
//    String license;
//    String licenseAr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.home_up_icon));

        face = Typeface.createFromAsset(RegisterActivity.this.getAssets(), Constants.FONT_URL);
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEditText.getText().toString().trim();
                mail = mailEditText.getText().toString().trim();
                address = addressEditText.getText().toString().trim();
                username = usernameEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();
                mobile = mobileEditText.getText().toString().trim();
                String conf = confPasswordEditText.getText().toString().trim();

                isFilled = isFilled || name.length() != 0 || mobile.length() != 0 || mail.length() != 0 ||
                        address.length() != 0 || username.length() != 0 || password.length() != 0 ||
                        iAgreeCheckBox.isChecked() || conf.length() != 0;

                if (isFilled) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    LayoutInflater inflater = RegisterActivity.this.getLayoutInflater();
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

                } else {
                    finish();
                }
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

        nameEditText = (EditText) findViewById(R.id.edit_name);
        nameEditText.setTypeface(face);

        mailEditText = (EditText) findViewById(R.id.edit_mail);
        mailEditText.setTypeface(face);

        addressEditText = (Button) findViewById(R.id.edit_address);
        addressEditText.setTypeface(face);

        usernameEditText = (EditText) findViewById(R.id.edit_username);
        usernameEditText.setTypeface(face);

        passwordEditText = (EditText) findViewById(R.id.edit_password);
        passwordEditText.setTypeface(face);

        confPasswordEditText = (EditText) findViewById(R.id.edit_conf_password);
        confPasswordEditText.setTypeface(face);

        iAgreeCheckBox = (CheckBox) findViewById(R.id.chk_i_agree);
        iAgreeCheckBox.setTypeface(face);

        TextView t1 = (TextView) findViewById(R.id.txt_i_agree);
        t1.setTypeface(face);

        nameTitleText = (TextView) findViewById(R.id.name_title);
        nameTitleText.setTypeface(face);

        mobileTitleText = (TextView) findViewById(R.id.mobile_title);
        mobileTitleText.setTypeface(face);

        emailTitleText = (TextView) findViewById(R.id.email_title);
        emailTitleText.setTypeface(face);

        usernameTitleText = (TextView) findViewById(R.id.username_title);
        usernameTitleText.setTypeface(face);

        confPasswordTitleText = (TextView) findViewById(R.id.conf_password_title);
        confPasswordTitleText.setTypeface(face);

        passwordTitleText = (TextView) findViewById(R.id.password_title);
        passwordTitleText.setTypeface(face);

        vSizeTitleText = (TextView) findViewById(R.id.size_title);
        vSizeTitleText.setTypeface(face);

        vModelTitleText = (TextView) findViewById(R.id.model_title);
        vModelTitleText.setTypeface(face);

        vTypeTitleText = (TextView) findViewById(R.id.type_title);
        vTypeTitleText.setTypeface(face);

        withGlassText1 = (TextView) findViewById(R.id.with_glass_label1);
        withGlassText1.setTypeface(face);

        withGlassText2 = (TextView) findViewById(R.id.with_glass_label2);
        withGlassText2.setTypeface(face);
        withGlassText2.setPaintFlags(withGlassText2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        withCompanyText = (TextView) findViewById(R.id.with_company_label);
        withCompanyText.setTypeface(face);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        termsConditionsTextView = (TextView) findViewById(R.id.txt_terms_conditions);
        termsConditionsTextView.setTypeface(face);
        termsConditionsTextView.setPaintFlags(termsConditionsTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        gpsTracker = new GPSTracker(RegisterActivity.this);

        mobileEditText = (EditText) findViewById(R.id.edit_mobile);
        mobileEditText.setTypeface(face);

        registerButton = (Button) findViewById(R.id.btn_register);
        registerButton.setTypeface(face);

        pickAddressButton = (TextView) findViewById(R.id.btn_pick_address);
        pickAddressButton.setTypeface(face);

//        attachPicButton = (Button) findViewById(R.id.btn_attach_pic);
//        attachPicButton.setTypeface(face);

        vehicleSizeSpinner = (Spinner) findViewById(R.id.spnr_veh_size);
        vehicleModelSpinner = (Spinner) findViewById(R.id.spnr_veh_model);
        vehicleTypeSpinner = (Spinner) findViewById(R.id.spnr_veh_type);
        vehicleCompanySpinner = (Spinner) findViewById(R.id.spnr_veh_company);

//        filesRecyclerView = (RecyclerView) findViewById(R.id.recycler_upload_files);

        toggleWithCompany = (ToggleButton) findViewById(R.id.with_company_switch);
        toggleWithCompany.setTypeface(face);

        toggleWithGlass = (ToggleButton) findViewById(R.id.with_glass_switch);
        toggleWithGlass.setTypeface(face);

        companySpinnerLayout = (LinearLayout) findViewById(R.id.company_spinner_layout);

        withGlass = true;
        withCompany = false;
        companySpinnerLayout.setVisibility(View.GONE);

//        filesRecyclerView.setHasFixedSize(true);
//        filesLayoutManager = new LinearLayoutManager(RegisterActivity.this, LinearLayoutManager.HORIZONTAL, false);
//        filesRecyclerView.setLayoutManager(filesLayoutManager);
//        mFilesAdapter = new UploadFilesAdapter();
//        filesRecyclerView.setAdapter(mFilesAdapter);

        addressEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsTracker.getLocation();

                if (gpsTracker.canGetLocation()) {
                    latitude = gpsTracker.getLatitude();
                    longitude = gpsTracker.getLongitude();

                    Intent intent = new Intent(RegisterActivity.this, MapLocationPickerActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);

                } else {
                    gpsTracker.showSettingsAlert();
                }
            }
        });

        termsConditionsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTermsAndConditions(true);
            }
        });

        withGlassText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                LayoutInflater inflater = RegisterActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.alert_dialog, null);
                builder.setView(view);
                TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
                txtAlert.setText(R.string.about_glass1);
                TextView txtAlert2 = (TextView) view.findViewById(R.id.txt_alert2);
                txtAlert2.setText(R.string.about_glass2);
                txtAlert2.setVisibility(View.VISIBLE);
                TextView txtAlert3 = (TextView) view.findViewById(R.id.txt_alert3);
                txtAlert3.setText(R.string.about_glass3);
                txtAlert3.setVisibility(View.VISIBLE);
                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                Button btnOK = (Button) view.findViewById(R.id.btn_ok);
                btnOK.setTypeface(face);
                btnOK.setText(getResources().getString(R.string.ok));
                Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
                btnCancel.setTypeface(face);
                txtAlert.setTypeface(face);
                alertDialog.show();

                //getTermsAndConditions(false);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsTracker.getLocation();

                if (gpsTracker.canGetLocation()) {
                    latitude = gpsTracker.getLatitude();
                    longitude = gpsTracker.getLongitude();

                    if (validate()) {
                        if (ConnectionDetector.isConnected(RegisterActivity.this)) {
                            new CheckUserIDOrMobileNoExistBackground(true).execute();
                        } else {
                            ConnectionDetector.errorSnackbar(coordinatorLayout);
                        }
                    }

                } else {
                    gpsTracker.showSettingsAlert();
                }
            }
        });

        vehicleSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = parent.getSelectedItemPosition();

                vSizeId = Integer.parseInt(vehicleSizeIdList.get(index));
                vSizeName = vehicleSizeDataList.get(index);

                if (vSizeId > 0) {
                    isFilled = true;
                }

                new ListVehicleModelBackground().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        vehicleModelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = parent.getSelectedItemPosition();

                vModelId = Integer.parseInt(vehicleModelIdList.get(index));
                vModelName = vehicleModelDataList.get(index);

                if (vModelId > 0) {
                    isFilled = true;
                }

                new ListVehicleModelBackground().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        vehicleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = parent.getSelectedItemPosition();

                vTypeId = Integer.parseInt(vehicleTypeIdList.get(index));
                vTypeName = vehicleTypeDataList.get(index);

                if (vTypeId > 0) {
                    isFilled = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        vehicleCompanySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = parent.getSelectedItemPosition();

                vCompId = Integer.parseInt(vehicleCompanyIdList.get(index));
                vCompName = vehicleCompanyDataList.get(index);

                if (vCompId > 0) {
                    isFilled = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (ConnectionDetector.isConnected(getApplicationContext())) {
            new ListVehicleMakeListVehicleCompanyBackground().execute();
        } else {
            ConnectionDetector.errorSnackbar(coordinatorLayout);
        }

        vehicleSizeSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });

        vehicleCompanySpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });

        vehicleTypeSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });

        vehicleModelSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });

//        attachPicButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectImage(RegisterActivity.this);
//            }
//        });
//
//        uploadFiles = new ArrayList<>();

        usernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String pattern= "^[0-9]*$";
                    username = usernameEditText.getText().toString().trim();

                    if (username.length() == 0) {
                        usernameEditText.setError(RegisterActivity.this.getResources().getText(R.string.required));

                    } else if (username.length() < 4) {
                        usernameEditText.setError(RegisterActivity.this.getResources().getText(R.string.username_4_char));

                    } else if (username.length() > 10) {
                        usernameEditText.setError(RegisterActivity.this.getResources().getText(R.string.username_10_char));

                    } else if (username.matches(pattern)) {
                        usernameEditText.setError(RegisterActivity.this.getResources().getText(R.string.alphanumeric_username));
                    }
                }
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    password = passwordEditText.getText().toString().trim();

                    if (password.length() == 0) {
                        passwordEditText.setError(RegisterActivity.this.getResources().getText(R.string.required));

                    } else if (password.length() < 6) {
                        passwordEditText.setError(RegisterActivity.this.getResources().getText(R.string.password_min_6_char));
                    }
                }
            }
        });

        confPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (password != null && !password.equals(confPasswordEditText.getText().toString().trim())) {
                        confPasswordEditText.setError(RegisterActivity.this.getResources().getText(R.string.password_does_not_match));
                    }
                }
            }
        });

        mobileEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mobile = mobileEditText.getText().toString().trim().replaceFirst("^0+(?!$)", "");

                    if (!isValidMobile(mobile)) {
                        mobileEditText.setError(RegisterActivity.this.getResources().getText(R.string.invalid_mobile));
                    }
                }
            }
        });

        toggleWithCompany.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                withCompany = isChecked;

                if (isChecked) {
                    companySpinnerLayout.setVisibility(View.VISIBLE);
                } else {
                    companySpinnerLayout.setVisibility(View.GONE);
                }
            }
        });

        toggleWithGlass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                withGlass = isChecked;
            }
        });
//        nameArEditText = (EditText) findViewById(R.id.edit_name_ar);
//        nameArEditText.setTypeface(face);
//        licenseEditText = (EditText) findViewById(R.id.edit_license);
//        licenseEditText.setTypeface(face);
//        licenseArEditText = (EditText) findViewById(R.id.edit_license_ar);
//        licenseArEditText.setTypeface(face);
//        titleText = (TextView) findViewById(R.id.txt_title);
//        titleText.setTypeface(face);
//        nameArTitleText = (TextView) findViewById(R.id.name_ar_title);
//        nameArTitleText.setTypeface(face);
//        licenseTitleText = (TextView) findViewById(R.id.license_title);
//        licenseTitleText.setTypeface(face);
//        licenseArTitleText = (TextView) findViewById(R.id.license_ar_title);
//        licenseArTitleText.setTypeface(face);
//        phoneInputView = (IntlPhoneInput) findViewById(R.id.edit_mobile);
//        radioButtonYes = (RadioButton) findViewById(R.id.radio_yes);
//        radioButtonYes.setTypeface(face);
//        radioButtonNo = (RadioButton) findViewById(R.id.radio_no);
//        radioButtonNo.setTypeface(face);
//        radioGroupWorkWithGlass = (RadioGroup) findViewById(R.id.with_glass_switch);
//        imgWorkWithGlass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                withGlass = false;
//            }
//        });
//        imgWorkWithGlass = (ImageView) findViewById(R.id.with_glass_switch);
//        imgWorkWithGlass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (withGlass) {
//                    withGlass = false;
//                    imgWorkWithGlass.setImageResource(R.drawable.off_big);
//
//                } else {
//                    withGlass = true;
//                    imgWorkWithGlass.setImageResource(R.drawable.on_big);
//                }
//            }
//        });
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

//    class UploadFilesAdapter extends RecyclerView.Adapter<UploadFilesAdapter.ViewHolder> {
//
//        class ViewHolder extends RecyclerView.ViewHolder {
//            // each data item is just a string in this case
//            ImageView uploadImageFile;
//            TextView textPicNo;
//            TextView textPicName;
//            TextView textRemovePic;
//
//            ViewHolder(View v) {
//                super(v);
//                uploadImageFile = (ImageView) v.findViewById(R.id.img_upload_file);
//                textPicNo = (TextView) v.findViewById(R.id.txt_pic_no);
//                textPicName = (TextView) v.findViewById(R.id.txt_pic_name);
//                textRemovePic = (TextView) v.findViewById(R.id.txt_remove_pic);
//            }
//        }
//
//        // Create new views (invoked by the layout manager)
//        @Override
//        public UploadFilesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_upload_file, parent, false);
//            return new ViewHolder(v);
//        }
//
//        @Override
//        public void onBindViewHolder(final ViewHolder holder, int position) {
//            // - get element from your dataset at this position
//            // - replace the contents of the view with that element
//            //View view = holder.itemView;
//            final int positionPlace = position;
//            holder.setIsRecyclable(false);
//
//            holder.textPicNo.setTypeface(face);
//            holder.textPicName.setTypeface(face);
//            holder.textRemovePic.setTypeface(face);
//
//            holder.textPicNo.setText(String.format("%s", positionPlace + 1));
//            holder.textPicName.setText(uploadFiles.get(positionPlace).imageName);
//            holder.uploadImageFile.setImageBitmap(uploadFiles.get(positionPlace).bm);
//
//            holder.textRemovePic.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
//                    LayoutInflater inflater1 = RegisterActivity.this.getLayoutInflater();
//                    final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
//                    builder1.setView(view1);
//                    TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
//                    txtAlert1.setText(getResources().getString(R.string.are_you_sure));
//                    alertDialog = builder1.create();
//                    alertDialog.setCancelable(false);
//                    view1.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            alertDialog.dismiss();
//                        }
//                    });
//                    view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            alertDialog.dismiss();
//                            uploadFiles.remove(positionPlace);
//                            mFilesAdapter.notifyDataSetChanged();
//                        }
//                    });
//                    Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
//                    Button btnCancel = (Button) view1.findViewById(R.id.btn_cancel);
//                    btnCancel.setTypeface(face);
//                    btnOk.setTypeface(face);
//                    txtAlert1.setTypeface(face);
//                    alertDialog.show();
//                }
//            });
//        }
//
//        // Return the size of your dataset (invoked by the layout manager)
//        @Override
//        public int getItemCount() {
//            if (uploadFiles != null)
//                return uploadFiles.size();
//            else
//                return 0;
//        }
//
//    }

    private void getTermsAndConditions(boolean isTermCond) {
        AssetManager assetManager = getAssets();
        InputStream in = null;
        OutputStream out = null;
        File file = new File(getFilesDir(), "terms_conditions.pdf");

        try {
            if (isTermCond) {
                in = assetManager.open("terms_conditions.pdf");
            } else {
                in = assetManager.open("terms_conditions.pdf");
            }
            out = openFileOutput (file.getName(), MODE_PRIVATE);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            //Log.e("tag_", e.getMessage());
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            Uri apkURI = FileProvider.getUriForFile( RegisterActivity.this, getApplicationContext().getPackageName() + ".provider", file);

            intent.setDataAndType(apkURI, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), R.string.no_app_pdf, Toast.LENGTH_SHORT).show();
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//
//        MenuItem menuLogout = menu.findItem(R.id.exit_option);
//        menuLogout.setVisible(false);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.back_option) {
//            name = nameEditText.getText().toString().trim();
//            mail = mailEditText.getText().toString().trim();
//            address = addressEditText.getText().toString().trim();
//            username = usernameEditText.getText().toString().trim();
//            password = passwordEditText.getText().toString().trim();
//            mobile = mobileEditText.getText().toString().trim();
//            String conf = confPasswordEditText.getText().toString().trim();
//
//            isFilled = isFilled || name.length() != 0 || mobile.length() != 0 || mail.length() != 0 ||
//                    address.length() != 0 || username.length() != 0 || password.length() != 0 ||
//                    iAgreeCheckBox.isChecked() || conf.length() != 0;
//
//            if (isFilled) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
//                LayoutInflater inflater = RegisterActivity.this.getLayoutInflater();
//                final View view = inflater.inflate(R.layout.alert_dialog, null);
//                builder.setView(view);
//                TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
//                txtAlert.setText(R.string.filled_data_will_be_lost);
//                alertDialog = builder.create();
//                alertDialog.setCancelable(false);
//                view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        alertDialog.dismiss();
//                    }
//                });
//                view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        alertDialog.dismiss();
//                        finish();
//                    }
//                });
//                Button btnOK = (Button) view.findViewById(R.id.btn_ok);
//                btnOK.setTypeface(face);
//                Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
//                btnCancel.setTypeface(face);
//                txtAlert.setTypeface(face);
//                alertDialog.show();
//
//            } else {
//                finish();
//            }
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onBackPressed() {
        name = nameEditText.getText().toString().trim();
        mail = mailEditText.getText().toString().trim();
        address = addressEditText.getText().toString().trim();
        username = usernameEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        mobile = mobileEditText.getText().toString().trim();
        String conf = confPasswordEditText.getText().toString().trim();

        isFilled = isFilled || name.length() != 0 || mobile.length() != 0 || mail.length() != 0 ||
                address.length() != 0 || username.length() != 0 || password.length() != 0 ||
                iAgreeCheckBox.isChecked() || conf.length() != 0;

        if (isFilled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            LayoutInflater inflater = RegisterActivity.this.getLayoutInflater();
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

        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            isFilled = true;

            addressEditText.setText(data.getStringExtra("location"));
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
        }

//        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//            Cursor cursor = getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//            //Bitmap bm = BitmapFactory.decodeFile(picturePath);
//            //bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            File f = new File(picturePath);
//            Bitmap bm = decodeFile(f);
//            bm = scaleDownBitmap(bm, 400);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            byte[] byteImage_photo = baos.toByteArray();
//
//            final UploadFiles uploadFile = new UploadFiles();
//            uploadFile.bm = bm;
//            uploadFile.base64Encoded = Base64.encodeToString(byteImage_photo, Base64.DEFAULT);
//
//            AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
//            LayoutInflater inflater1 = RegisterActivity.this.getLayoutInflater();
//            final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
//            builder1.setView(view1);
//            final EditText timeInput = (EditText) view1.findViewById(R.id.text_input);
//            timeInput.setVisibility(View.VISIBLE);
//            alertDialog = builder1.create();
//            alertDialog.setCancelable(false);
//            final Button btnCancel = (Button) view1.findViewById(R.id.btn_cancel);
//            btnCancel.setVisibility(View.GONE);
//            final Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
//            btnOk.setEnabled(false);
//            timeInput.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                }
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    if(s.toString().trim().length() > 0) {
//                        btnOk.setEnabled(true);
//                    }
//                }
//                @Override
//                public void afterTextChanged(Editable s) {
//                }
//            });
//            view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                    uploadFile.imageName = timeInput.getText().toString().trim();
//
//                    boolean itemAdded = false;
//                    isFilled = true;
//
//                    for (UploadFiles item: uploadFiles ){
//                        if (uploadFile.imageName.equals(item.imageName)) {
//                            itemAdded = true;
//                        }
//                    }
//
//                    if (itemAdded) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
//                        LayoutInflater inflater = RegisterActivity.this.getLayoutInflater();
//                        final View view = inflater.inflate(R.layout.alert_dialog, null);
//                        builder.setView(view);
//                        TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
//                        txtAlert.setText(getResources().getString(R.string.item_exist));
//                        alertDialog = builder.create();
//                        alertDialog.setCancelable(false);
//                        view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
//                        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
//                        btnOK.setText(R.string.ok);
//                        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                alertDialog.dismiss();
//                            }
//                        });
//                        btnOK.setTypeface(face);
//                        txtAlert.setTypeface(face);
//                        alertDialog.show();
//                    } else {
//                        uploadFiles.add(uploadFile);
//                        mFilesAdapter.notifyDataSetChanged();
//                    }
//                }
//            });
//            TextView t1 = (TextView) view1.findViewById(R.id.txt_alert);
//            t1.setText(getResources().getString(R.string.enter_file_name));
//            t1.setTypeface(face);
//            btnOk.setTypeface(face);
//            timeInput.setTypeface(face);
//            alertDialog.show();
//
//        } else if(requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK && null != data) {
//            Bitmap bm = (Bitmap) data.getExtras().get("data");
//            //ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            //assert bm != null;
//            //bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            //byte[] byteImage_photo = baos.toByteArray();
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "dynate.jpg");
//
//            try {
//                f.createNewFile();
//                FileOutputStream fo = new FileOutputStream(f);
//                fo.write(baos.toByteArray());
//
//                Bitmap bm1 = decodeFile(f);
//                bm1 = scaleDownBitmap(bm1, 400);
//                bm1.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                byte[] byteImage_photo = baos.toByteArray();
//
//                final UploadFiles uploadFile = new UploadFiles();
//                uploadFile.bm = bm1;
//                uploadFile.base64Encoded = Base64.encodeToString(byteImage_photo,Base64.DEFAULT);
//
//                fo.flush();
//                fo.close();
//
//                AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
//                LayoutInflater inflater1 = RegisterActivity.this.getLayoutInflater();
//                final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
//                builder1.setView(view1);
//                final EditText timeInput = (EditText) view1.findViewById(R.id.text_input);
//                timeInput.setVisibility(View.VISIBLE);
//                alertDialog = builder1.create();
//                alertDialog.setCancelable(false);
//                final Button btnCancel = (Button) view1.findViewById(R.id.btn_cancel);
//                btnCancel.setVisibility(View.GONE);
//                final Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
//                btnOk.setEnabled(false);
//                timeInput.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                    }
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        if(s.toString().trim().length() > 0) {
//                            btnOk.setEnabled(true);
//                        }
//                    }
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                    }
//                });
//                view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        alertDialog.dismiss();
//                        uploadFile.imageName = timeInput.getText().toString().trim();
//
//                        boolean itemAdded = false;
//                        isFilled = true;
//
//                        for (UploadFiles item: uploadFiles ){
//                            if (uploadFile.imageName.equals(item.imageName)) {
//                                itemAdded = true;
//                            }
//                        }
//
//                        if (itemAdded) {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
//                            LayoutInflater inflater = RegisterActivity.this.getLayoutInflater();
//                            final View view = inflater.inflate(R.layout.alert_dialog, null);
//                            builder.setView(view);
//                            TextView txtAlert = (TextView) view.findViewById(R.id.txt_alert);
//                            txtAlert.setText(getResources().getString(R.string.item_exist));
//                            alertDialog = builder.create();
//                            alertDialog.setCancelable(false);
//                            view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
//                            Button btnOK = (Button) view.findViewById(R.id.btn_ok);
//                            btnOK.setText(R.string.ok);
//                            view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    alertDialog.dismiss();
//                                }
//                            });
//                            btnOK.setTypeface(face);
//                            txtAlert.setTypeface(face);
//                            alertDialog.show();
//                        } else {
//                            uploadFiles.add(uploadFile);
//                            mFilesAdapter.notifyDataSetChanged();
//                        }
//                    }
//                });
//                TextView t1 = (TextView) view1.findViewById(R.id.txt_alert);
//                t1.setText(getResources().getString(R.string.enter_file_name));
//                t1.setTypeface(face);
//                btnOk.setTypeface(face);
//                timeInput.setTypeface(face);
//                alertDialog.show();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

//    public Bitmap scaleDownBitmap(Bitmap photo, int newHeight) {
//        final float densityMultiplier = getResources().getDisplayMetrics().density;
//        int h = (int) (newHeight*densityMultiplier);
//        int w = (int) (h * photo.getWidth()/((double) photo.getHeight()));
//        photo = Bitmap.createScaledBitmap(photo, w, h, true);
//        return photo;
//    }
//
//    private Bitmap decodeFile(File f) {
//        try {
//            // decode image size
//            BitmapFactory.Options o = new BitmapFactory.Options();
//            o.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
//
//            // Find the correct scale value. It should be the power of 2.
//            final int REQUIRED_SIZE = 500;
//            int width_tmp = o.outWidth, height_tmp = o.outHeight;
//            int scale = 1;
//            while (true) {
//                if (width_tmp / 2 < REQUIRED_SIZE
//                        || height_tmp / 2 < REQUIRED_SIZE)
//                    break;
//                width_tmp /= 2;
//                height_tmp /= 2;
//                scale *= 2;
//            }
//
//            // decode with inSampleSize
//            BitmapFactory.Options o2 = new BitmapFactory.Options();
//            o2.inSampleSize = scale;
//            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
//
//        } catch (FileNotFoundException ignored) {
//        }
//        return null;
//    }

    public boolean isValidMobile(String mobile) {
        String iso = "SA";
        Phonenumber.PhoneNumber phoneNumber = null;

        try {
            phoneNumber = mPhoneUtil.parse(mobile, iso);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }

        return phoneNumber != null && mPhoneUtil.isValidNumber(phoneNumber);
    }

    boolean validate() {
        name = nameEditText.getText().toString().trim();
        mail = mailEditText.getText().toString().trim();
        address = addressEditText.getText().toString().trim();
        username = usernameEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        mobile = mobileEditText.getText().toString().trim().replaceFirst("^0+(?!$)", "");
        String conf = confPasswordEditText.getText().toString().trim();

        String pattern= "^[0-9]*$";

        if (name.length() == 0) {
            nameEditText.setError(RegisterActivity.this.getResources().getText(R.string.required));
            nameEditText.requestFocus();
            return  false;
        } else {
            nameEditText.setError(null);
        }

        if (isValidMobile(mobile)) {
            mobileEditText.setError(null);
        } else {
            mobileEditText.setError(RegisterActivity.this.getResources().getText(R.string.invalid_mobile));
            mobileEditText.requestFocus();
            return  false;
        }

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            mailEditText.setError(null);
        } else {
            mailEditText.setError(RegisterActivity.this.getResources().getText(R.string.invalid_email));
            mailEditText.requestFocus();
            return  false;
        }

        if (address.length() == 0){
            addressEditText.setError(RegisterActivity.this.getResources().getText(R.string.required));
            addressEditText.requestFocus();
            return  false;
        } else {
            addressEditText.setError(null);
        }

        if (username.length() == 0) {
            usernameEditText.setError(RegisterActivity.this.getResources().getText(R.string.required));
            usernameEditText.requestFocus();
            return  false;
        } else if (username.length() < 4) {
            usernameEditText.setError(RegisterActivity.this.getResources().getText(R.string.username_4_char));
            usernameEditText.requestFocus();
            return false;
        } else if (username.length() > 10) {
            usernameEditText.setError(RegisterActivity.this.getResources().getText(R.string.username_10_char));
            usernameEditText.requestFocus();
            return false;
        } else if (username.matches(pattern)) {
            usernameEditText.setError(RegisterActivity.this.getResources().getText(R.string.alphanumeric_username));
            usernameEditText.requestFocus();
            return false;
        } else {
            usernameEditText.setError(null);
        }

        if (password.length() == 0) {
            passwordEditText.setError(RegisterActivity.this.getResources().getText(R.string.required));
            passwordEditText.requestFocus();
            return false;
        } else if (password.length() < 6) {
            passwordEditText.setError(RegisterActivity.this.getResources().getText(R.string.password_min_6_char));
            passwordEditText.requestFocus();
            return false;
        } else if (!conf.equals(password)) {
            confPasswordEditText.setError(RegisterActivity.this.getResources().getString(R.string.password_does_not_match));
            return false;
        } else {
            confPasswordEditText.setError(null);
            passwordEditText.setError(null);
        }

        if (vSizeId == 0) {
            ((TextView)vehicleSizeSpinner.getChildAt(0)).setError(RegisterActivity.this.getResources().getText(R.string.required));
            vehicleSizeSpinner.requestFocus();
            return false;
        }

        if(vModelId == 0) {
            ((TextView)vehicleModelSpinner.getChildAt(0)).setError(RegisterActivity.this.getResources().getText(R.string.required));
            vehicleModelSpinner.requestFocus();
            return false;
        }

        if(vTypeId == 0) {
            ((TextView)vehicleTypeSpinner.getChildAt(0)).setError(RegisterActivity.this.getResources().getText(R.string.required));
            vehicleTypeSpinner.requestFocus();
            return false;
        }

//        if (uploadFiles == null || uploadFiles.size() == 0) {
//            AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
//            LayoutInflater inflater1 = RegisterActivity.this.getLayoutInflater();
//            final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
//            builder1.setView(view1);
//            TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
//            txtAlert1.setText(getResources().getString(R.string.upload_files));
//            alertDialog = builder1.create();
//            alertDialog.setCancelable(false);
//            view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
//            Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
//            btnOk.setText(R.string.ok);
//            view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                }
//            });
//            btnOk.setTypeface(face);
//            txtAlert1.setTypeface(face);
//            alertDialog.show();
//            return false;
//        }

        if (withCompany && vCompId <= 1) {
            vehicleCompanySpinner.requestFocus();

            AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
            LayoutInflater inflater1 = RegisterActivity.this.getLayoutInflater();
            final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
            builder1.setView(view1);
            TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
            txtAlert1.setText(getResources().getString(R.string.you_must_select_company));
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

        if (!iAgreeCheckBox.isChecked()) {
            iAgreeCheckBox.setError("");
            iAgreeCheckBox.requestFocus();
            return  false;
        } else {
            iAgreeCheckBox.setError(null);
        }

        //nameAr = nameArEditText.getText().toString().trim();
        //license = licenseEditText.getText().toString().trim();
        //licenseAr = licenseArEditText.getText().toString().trim();
        //if (nameAr.length() == 0) {
        //    nameArEditText.setError(RegisterActivity.this.getResources().getText(R.string.required));
        //    nameArEditText.requestFocus();
        //    return  false;
        //} else {
        //    nameArEditText.setError(null);
        //}
        //if(phoneInputView.isValid()) {
        //    mobile = phoneInputView.getNumber();
        //} else {
        //    phoneInputView.setDefault();
        //    phoneInputView.requestFocus();
        //    return  false;
        //}
        //if (license.length() == 0){
        //    licenseEditText.setError(RegisterActivity.this.getResources().getText(R.string.required));
        //    licenseEditText.requestFocus();
        //    return  false;
        //} else {
        //    licenseEditText.setError(null);
        //}
        //
        //if (licenseAr.length() == 0){
        //    licenseArEditText.setError(RegisterActivity.this.getResources().getText(R.string.required));
        //    licenseArEditText.requestFocus();
        //    return  false;
        //} else {
        //    licenseArEditText.setError(null);
        //}

        return true;
    }

//    private class SendOTPDMBackground extends AsyncTask<Void, Void, JSONObject> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
//                myCircularProgressDialog = new MyCircularProgressDialog(RegisterActivity.this);
//                myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                myCircularProgressDialog.setCancelable(false);
//                myCircularProgressDialog.show();
//            }
//        }
//
//        protected JSONObject doInBackground(Void... param) {
//            JsonParser jsonParser = new JsonParser();
//
//            HashMap<String, String> params = new HashMap<>();
//
//            params.put("ArgMobNo", "966"+mobile);
//            params.put("ArgIsDB", "false");
//
//            String BASE_URL = Constants.BASE_URL_EN + "SendOTPDM";
//
//            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
//                BASE_URL = Constants.BASE_URL_AR + "SendOTPDM";
//            }
//
//            return jsonParser.makeHttpRequest(BASE_URL, "POST", params);
//        }
//
//        protected void onPostExecute(final JSONObject response) {
//            myCircularProgressDialog.dismiss();
//
//            if (response != null) {
//                try {
//                    // Parsing json object response
//                    // response will be a json object
//                    if (response.getBoolean("status")) {
//                        Intent intent = new Intent(RegisterActivity.this, VerifyOTPActivity.class);
//                        registerUser = new RegisterUser();
//                        registerUser.name = name;
//                        registerUser.nameArabic = name;
//                        registerUser.mobile = mobile;
//                        registerUser.mail = mail;
//                        registerUser.address = address;
//                        registerUser.username = username;
//                        registerUser.password = password;
//                        registerUser.latitude = String.valueOf(latitude);
//                        registerUser.longitude = String.valueOf(longitude);
//                        registerUser.loginMethod = Constants.LOG_CONST_NORMAL;
//                        registerUser.vModelId = String.valueOf(vTypeId);
//                        registerUser.vModelName = vTypeName;
//
//                        if (withCompany) {
//                            registerUser.vCompId = String.valueOf(vCompId);
//                            registerUser.vCompName = vCompName;
//                        } else {
//                            registerUser.vCompId = "1";
//                            registerUser.vCompName = getResources().getString(R.string.select_company);
//                        }
//
//                        registerUser.licenseNo = "";
//                        registerUser.licenseNoArabic = "";
//                        registerUser.withGlass = withGlass;
//
////                        for (UploadFiles file : uploadFiles) {
////                            file.bm = null;
////                        }
////
////                        try {
////                            VerifyOTPActivity.otpExtra = response.getJSONArray("data").getJSONObject(0).getString("OTP");
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
////                        VerifyOTPActivity.registerUserExtra = registerUser;
////                        VerifyOTPActivity.uploadFiles = uploadFiles;
////
////                        startActivity(intent);
//
////                        AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
////                        final LayoutInflater inflater1 = RegisterActivity.this.getLayoutInflater();
////                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
////                        builder1.setView(view1);
////                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
////                        txtAlert1.setText(R.string.password_send);
////                        alertDialog = builder1.create();
////                        alertDialog.setCancelable(false);
////                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
////                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
////                        btnOk.setText(R.string.ok);
////                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
////                            @Override
////                            public void onClick(View v) {
////                                alertDialog.dismiss();
////                            }
////                        });
////                        btnOk.setTypeface(face);
////                        txtAlert1.setTypeface(face);
////                        alertDialog.show();
//
//                    } else {
//                        AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
//                        LayoutInflater inflater1 = RegisterActivity.this.getLayoutInflater();
//                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
//                        builder1.setView(view1);
//                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
//                        txtAlert1.setText(response.getString("message"));
//                        alertDialog = builder1.create();
//                        alertDialog.setCancelable(false);
//                        view1.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
//                        Button btnOk = (Button) view1.findViewById(R.id.btn_ok);
//                        btnOk.setText(R.string.ok);
//                        view1.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                alertDialog.dismiss();
//                            }
//                        });
//                        btnOk.setTypeface(face);
//                        txtAlert1.setTypeface(face);
//                        alertDialog.show();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                snackbar = Snackbar.make(coordinatorLayout, R.string.network_error, Snackbar.LENGTH_LONG)
//                        .setAction(R.string.ok, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                            }
//                        });
//                snackbar.show();
//            }
//        }
//    }

    private class ListVehicleMakeListVehicleCompanyBackground extends AsyncTask<Void, Void, JSONObject> {
        JSONObject jsonObjectVehicleSize;
        JSONObject jsonObjectVehicleModel;
        JSONObject jsonObjectVehicleCompany;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
                myCircularProgressDialog = new MyCircularProgressDialog(RegisterActivity.this);
                myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                myCircularProgressDialog.setCancelable(false);
                myCircularProgressDialog.show();
            }
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParserVehicleSize = new JsonParser();
            JsonParser jsonParserVehicleModel = new JsonParser();
            JsonParser jsonParserVehicleCompany = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                jsonObjectVehicleSize = jsonParserVehicleSize.makeHttpRequest(Constants.BASE_URL_AR + "ListVehicleSize", "POST", params);
                jsonObjectVehicleModel = jsonParserVehicleModel.makeHttpRequest(Constants.BASE_URL_AR + "ListVehicleMake", "POST", params);
                jsonObjectVehicleCompany = jsonParserVehicleCompany.makeHttpRequest(Constants.BASE_URL_AR + "ListVehicleCompany", "POST", params);

            } else {
                jsonObjectVehicleSize = jsonParserVehicleSize.makeHttpRequest(Constants.BASE_URL_EN + "ListVehicleSize", "POST", params);
                jsonObjectVehicleModel = jsonParserVehicleModel.makeHttpRequest(Constants.BASE_URL_EN + "ListVehicleMake", "POST", params);
                jsonObjectVehicleCompany = jsonParserVehicleCompany.makeHttpRequest(Constants.BASE_URL_EN + "ListVehicleCompany", "POST", params);
            }

            return jsonObjectVehicleCompany;
        }

        protected void onPostExecute(JSONObject jsonObject) {
            myCircularProgressDialog.dismiss();

            if (jsonObjectVehicleSize != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (jsonObjectVehicleSize.getBoolean("status")) {
                        vehicleSizeArray = jsonObjectVehicleSize.getJSONArray("data");

                        if (vehicleSizeArray.length() > 0) {
                            vehicleSizeIdList = new ArrayList<>();
                            vehicleSizeDataList = new ArrayList<>();

                            for (int i = 0; i < vehicleSizeArray.length(); i++) {
                                if (Integer.parseInt(vehicleSizeArray.getJSONObject(i).getString("VsId").trim()) > 1) {
                                    vehicleSizeIdList.add(vehicleSizeArray.getJSONObject(i).getString("VsId"));
                                    vehicleSizeDataList.add(vehicleSizeArray.getJSONObject(i).getString("VsName"));
                                }
                            }
                            vehicleSizeAdapter = new MySpinnerAdapter(RegisterActivity.this, android.R.layout.select_dialog_item, vehicleSizeDataList);
                            vehicleSizeSpinner.setAdapter(vehicleSizeAdapter);
                        }

                        vSizeId = Integer.parseInt(vehicleSizeArray.getJSONObject(0).getString("VsId"));
                        vSizeName = vehicleSizeArray.getJSONObject(0).getString("VsName");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (jsonObjectVehicleModel != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (jsonObjectVehicleModel.getBoolean("status")) {
                        vehicleModelArray = jsonObjectVehicleModel.getJSONArray("data");

                        if (vehicleModelArray.length() > 0) {
                            vehicleModelIdList = new ArrayList<>();
                            vehicleModelDataList = new ArrayList<>();

                            for (int i = 0; i < vehicleModelArray.length(); i++) {
                                if (Integer.parseInt(vehicleModelArray.getJSONObject(i).getString("VMId").trim()) > 1) {
                                    vehicleModelIdList.add(vehicleModelArray.getJSONObject(i).getString("VMId"));
                                    vehicleModelDataList.add(vehicleModelArray.getJSONObject(i).getString("VMName"));
                                }
                            }
                            vehicleModelAdapter = new MySpinnerAdapter(RegisterActivity.this, android.R.layout.select_dialog_item, vehicleModelDataList);
                            vehicleModelSpinner.setAdapter(vehicleModelAdapter);
                        }

                        vModelId = Integer.parseInt(vehicleModelArray.getJSONObject(0).getString("VMId"));
                        vModelName = vehicleModelArray.getJSONObject(0).getString("VMName");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (jsonObjectVehicleCompany != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (jsonObjectVehicleCompany.getBoolean("status")) {
                        vehicleCompanyArray = jsonObjectVehicleCompany.getJSONArray("data");

                        if (vehicleCompanyArray.length() > 0) {
                            vehicleCompanyIdList = new ArrayList<>();
                            vehicleCompanyDataList = new ArrayList<>();

                            vehicleCompanyIdList.add("0");
                            vehicleCompanyDataList.add(getResources().getString(R.string.select_company));

                            for (int i = 0; i < vehicleCompanyArray.length(); i++) {
                                if (Integer.parseInt(vehicleCompanyArray.getJSONObject(i).getString("VcId").trim()) > 1) {
                                    vehicleCompanyIdList.add(vehicleCompanyArray.getJSONObject(i).getString("VcId"));
                                    vehicleCompanyDataList.add(vehicleCompanyArray.getJSONObject(i).getString("VcName"));
                                }
                            }
                            vehicleCompanyAdapter = new MySpinnerAdapter(RegisterActivity.this, android.R.layout.select_dialog_item, vehicleCompanyDataList);
                            vehicleCompanySpinner.setAdapter(vehicleCompanyAdapter);
                        }

                        vCompId = 0;
                        vCompName = "";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class MySpinnerAdapter extends ArrayAdapter<String> {

        private MySpinnerAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTypeface(face);
            view.setTextSize(15);
            return view;
        }

        // Affects opened state of the spinner
        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(face);
            view.setTextSize(15);
            return view;
        }
    }

    private class ListVehicleModelBackground extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
                myCircularProgressDialog = new MyCircularProgressDialog(RegisterActivity.this);
                myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                myCircularProgressDialog.setCancelable(false);
                myCircularProgressDialog.show();
            }
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            params.put("ArgVmoVMId", vModelId+"");
            params.put("ArgVmoVsId", vSizeId+"");

            String BASE_URL = Constants.BASE_URL_EN + "ListVehicleModel";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "ListVehicleModel";
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params);
        }

        protected void onPostExecute(JSONObject response) {
            myCircularProgressDialog.dismiss();

            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        vehicleTypeArray = response.getJSONArray("data");

                        if (vehicleTypeArray.length() > 0) {
                            vehicleTypeDataList = new ArrayList<>();
                            vehicleTypeIdList = new ArrayList<>();

                            for (int i = 0; i < vehicleTypeArray.length(); i++) {
                                if (Integer.parseInt(vehicleTypeArray.getJSONObject(i).getString("VmoId").trim()) > 1) {
                                    vehicleTypeIdList.add(vehicleTypeArray.getJSONObject(i).getString("VmoId"));
                                    vehicleTypeDataList.add(vehicleTypeArray.getJSONObject(i).getString("VmoName"));
                                }
                            }
                            vehicleTypeAdapter = new MySpinnerAdapter(RegisterActivity.this, android.R.layout.select_dialog_item, vehicleTypeDataList);
                            vehicleTypeSpinner.setAdapter(vehicleTypeAdapter);
                        }

                        vTypeId = Integer.parseInt(vehicleTypeArray.getJSONObject(0).getString("VmoId"));
                        vTypeName = vehicleTypeArray.getJSONObject(0).getString("VmoName");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    private void selectImage(final Context context) {
//        final CharSequence[] options = {context.getResources().getString(R.string.take_photo), context.getResources().getString(R.string.choose_from_gallery), context.getResources().getString(R.string.cancel)};
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("");
//        builder.setItems(options, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//
//                if (options[item].equals(context.getResources().getString(R.string.take_photo))) {
//                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
//
//                } else if (options[item].equals(context.getResources().getString(R.string.choose_from_gallery))) {
//                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(i, RESULT_LOAD_IMAGE);
//
//                } else if (options[item].equals(context.getResources().getString(R.string.cancel))) {
//                    dialog.dismiss();
//                }
//            }
//        });
//        builder.show();
//    }

    private class CheckUserIDOrMobileNoExistBackground extends AsyncTask<Void, Void, JSONObject> {
        Boolean isUsername;

        CheckUserIDOrMobileNoExistBackground(Boolean isUsername) {
            this.isUsername = isUsername;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (isUsername) {
                if (myCircularProgressDialog == null || !myCircularProgressDialog.isShowing()) {
                    myCircularProgressDialog = new MyCircularProgressDialog(RegisterActivity.this);
                    myCircularProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    myCircularProgressDialog.setCancelable(false);
                    myCircularProgressDialog.show();
                }
            }
        }

        protected JSONObject doInBackground(Void... param) {
            JsonParser jsonParser = new JsonParser();

            HashMap<String, String> params = new HashMap<>();

            if (isUsername) {
                params.put("ArgUserName", username);
            } else {
                params.put("ArgUserName", "966"+mobile);
            }

            String BASE_URL = Constants.BASE_URL_EN + "CheckUserIDOrMobileNoExist";

            if (sharedPreferences.getString(Constants.PREFS_LANG, "en").equalsIgnoreCase("ar")) {
                BASE_URL = Constants.BASE_URL_AR + "CheckUserIDOrMobileNoExist";
            }

            return jsonParser.makeHttpRequest(BASE_URL, "POST", params);
        }

        protected void onPostExecute(JSONObject response) {
            if (!isUsername) {
                myCircularProgressDialog.dismiss();
            }

            if (response != null) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    if (response.getBoolean("status")) {
                        myCircularProgressDialog.dismiss();

                        String msg = getResources().getString(R.string.duplicate_mobile);

                        if (isUsername) {
                            msg = getResources().getString(R.string.duplicate_username);
                        }

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                        LayoutInflater inflater1 = RegisterActivity.this.getLayoutInflater();
                        final View view1 = inflater1.inflate(R.layout.alert_dialog, null);
                        builder1.setView(view1);
                        TextView txtAlert1 = (TextView) view1.findViewById(R.id.txt_alert);
                        txtAlert1.setText(msg);
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

                    } else {
                        if (isUsername) {
                            new CheckUserIDOrMobileNoExistBackground(false).execute();
                        } else {
                            Intent intent = new Intent(RegisterActivity.this, SocialMediaActivity.class);
                            registerUser = new RegisterUser();
                            registerUser.name = name;
                            registerUser.nameArabic = name;
                            registerUser.mobile = mobile;
                            registerUser.mail = mail;
                            registerUser.address = address;
                            registerUser.username = username;
                            registerUser.password = password;
                            registerUser.latitude = String.valueOf(latitude);
                            registerUser.longitude = String.valueOf(longitude);
                            registerUser.loginMethod = Constants.LOG_CONST_NORMAL;
                            registerUser.vModelId = String.valueOf(vTypeId);
                            registerUser.vModelName = vTypeName;

                            if (withCompany) {
                                registerUser.vCompId = String.valueOf(vCompId);
                                registerUser.vCompName = vCompName;
                            } else {
                                registerUser.vCompId = "1";
                                registerUser.vCompName = getResources().getString(R.string.select_company);
                            }

                            registerUser.licenseNo = "";
                            registerUser.licenseNoArabic = "";
                            registerUser.withGlass = withGlass;

                            intent.putExtra("registerUser", registerUser);
                            startActivity(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
