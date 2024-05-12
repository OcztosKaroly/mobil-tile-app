package com.example.tile_shop_application;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();

    private EditText emailET, passwordET, passwordConfirmET, phoneET, taxNumberET,
            shippingNameET, shippingCountryET, shippingPostnumberET, shippingCityET, shippingAddressET,
            billingNameET, billingCountryET, billingPostnumberET, billingCityET, billingAddressET;
    private RadioGroup customerTypeRG;
    private CheckBox sameAddressCB, privacyPolicyCB, termsOfUseCB;
    private LinearLayout billingDataLayout;

    private FirebaseAuth mAuth;
    private UserFirebase mUserFirebase;
    private ShippingFirebase mShippingFirebase;
    private BillingFirebase mBillingFirebase;

    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainRegister), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeData();

        mAuth = FirebaseAuth.getInstance();
        mUserFirebase = new UserFirebase();
        mShippingFirebase = new ShippingFirebase();
        mBillingFirebase = new BillingFirebase();

        mView = findViewById(R.id.scrollRegister);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade);
        mView.startAnimation(animation);
    }

    private void initializeData() {
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        passwordConfirmET = findViewById(R.id.passwordConfirmET);
        phoneET = findViewById(R.id.phoneET);
        taxNumberET = findViewById(R.id.taxNumET);
        customerTypeRG = findViewById(R.id.customerTypeRG);

        shippingNameET = findViewById(R.id.shippingNameET);
        shippingCountryET = findViewById(R.id.shippingCountryET);
        shippingPostnumberET = findViewById(R.id.shippingPostnumberET);
        shippingCityET = findViewById(R.id.shippingCityET);
        shippingAddressET = findViewById(R.id.shippingAddressET);

        sameAddressCB = findViewById(R.id.sameAddressCheckBox);
        billingDataLayout = findViewById(R.id.billingData);
        billingNameET = findViewById(R.id.billingNameET);
        billingCountryET = findViewById(R.id.billingCountryET);
        billingPostnumberET = findViewById(R.id.billingPostnumberET);
        billingCityET = findViewById(R.id.billingCityET);
        billingAddressET = findViewById(R.id.billingAddressET);

        privacyPolicyCB = findViewById(R.id.privacyPolicyCB);
        termsOfUseCB = findViewById(R.id.termsOfUseCB);

        customerTypeRG.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.privateCustomerRB) {
                taxNumberET.setVisibility(View.GONE);
            } else if (checkedId == R.id.companyRB) {
                taxNumberET.setVisibility(View.VISIBLE);
            }
        });

        sameAddressCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                billingDataLayout.setVisibility(View.GONE);
            } else {
                billingDataLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void performRegister(View view) {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        if (!isInputValid()) {
            Log.d(LOG_TAG, "Valami hiba lépett fel a regisztráció során!");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()) {
                // a továddi adatok beállítása
                String userId = Objects.requireNonNull(task.getResult().getUser()).getUid();
                registerData(userId);
            } else {
                Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció: " +
                        Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerData(String userId) {
        String email = emailET.getText().toString().trim();
        String phone = phoneET.getText().toString().trim();
        String customerType = ((RadioButton)findViewById(customerTypeRG.getCheckedRadioButtonId())).getText().toString();
        customerType = customerType.equals("Cég") ? "company" : "private";
        String taxNumber = customerType.equals("company") ? taxNumberET.getText().toString() : "";
        boolean isBillingSameAsShipping = sameAddressCB.isChecked();
        User user = new User(userId, email, phone, customerType, taxNumber, isBillingSameAsShipping);
        mUserFirebase.createUser(user, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Felhasználó sikeresen létrehozva!", Toast.LENGTH_SHORT).show();

                String shippingName = shippingNameET.getText().toString().trim();
                String shippingCountry = shippingCountryET.getText().toString().trim();
                String shippingPostnumber = shippingPostnumberET.getText().toString().trim();
                String shippingCity = shippingCityET.getText().toString().trim();
                String shippingAddress = shippingAddressET.getText().toString().trim();
                Shipping shipping = new Shipping(userId, shippingName, shippingCountry, shippingPostnumber, shippingCity, shippingAddress);
                mShippingFirebase.createShipping(shipping, shippingTask -> {
                    if (shippingTask.isSuccessful()) {
                        Toast.makeText(this, "Felhasználó szállítási címe sikeresen létrehozva!", Toast.LENGTH_SHORT).show();

                        if (!isBillingSameAsShipping) {
                            String billingName = billingNameET.getText().toString().trim();
                            String billingCountry = billingCountryET.getText().toString().trim();
                            String billingPostnumber = billingPostnumberET.getText().toString().trim();
                            String billingCity = billingCityET.getText().toString().trim();
                            String billingAddress = billingAddressET.getText().toString().trim();
                            Billing billing = new Billing(userId, billingName, billingCountry, billingPostnumber, billingCity, billingAddress);
                            mBillingFirebase.createBilling(billing, billingTask -> {
                                if (billingTask.isSuccessful()) {
                                    Toast.makeText(this, "Felhasználó számlázási címe sikeresen létrehozva!", Toast.LENGTH_SHORT).show();
                                    startShopping(userId);
                                } else {
                                    Toast.makeText(this, "Hiba történt a felhasználó számlázási címe létrehozása során!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            startShopping(userId);
                        }
                    } else {
                        Toast.makeText(this, "Hiba történt a felhasználó szállítási címe létrehozása során!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Hiba történt a felhasználó létrehozása során!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isInputValid() {
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String passwordConfirm = passwordConfirmET.getText().toString().trim();
        String phone = phoneET.getText().toString().trim();
        String customerTypeText = ((RadioButton)findViewById(customerTypeRG.getCheckedRadioButtonId())).getText().toString().trim();
        String taxNumber = taxNumberET.getText().toString().trim();

        String shippingName = shippingNameET.getText().toString().trim();
        String shippingCountry = shippingCountryET.getText().toString().trim();
        String shippingPostnumber = shippingPostnumberET.getText().toString().trim();
        String shippingCity = shippingCityET.getText().toString().trim();
        String shippingAddress = shippingAddressET.getText().toString().trim();

        boolean isBillingSameAsShipping = sameAddressCB.isChecked();
        String billingName = billingNameET.getText().toString().trim();
        String billingCountry = billingCountryET.getText().toString().trim();
        String billingPostnumber = billingPostnumberET.getText().toString().trim();
        String billingCity = billingCityET.getText().toString().trim();
        String billingAddress = billingAddressET.getText().toString().trim();

        boolean privacyPolicyChecked = privacyPolicyCB.isChecked();
        boolean termsOfUseChecked = termsOfUseCB.isChecked();

        if (email.isEmpty() || password.isEmpty() || phone.isEmpty() || customerTypeText.isEmpty()) {
            Toast.makeText(this, "Minden mező kitöltése kötelező!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Nem egyeznek a jelszavak!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (customerTypeText.equals("Cég") && taxNumber.isEmpty()) {
            Toast.makeText(this, "Cég esetén adószám megadása kötelező!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (shippingName.isEmpty() || shippingCountry.isEmpty() || shippingPostnumber.isEmpty()
                || shippingCity.isEmpty() || shippingAddress.isEmpty()) {
            Toast.makeText(this, "Szállítási adatok megadása kötelező!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isBillingSameAsShipping) {
            if (billingName.isEmpty() || billingCountry.isEmpty() || billingPostnumber.isEmpty()
                    || billingCity.isEmpty() || billingAddress.isEmpty()) {
                Toast.makeText(this, "Számlázási adatok megadása kötelező!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (!(privacyPolicyChecked && termsOfUseChecked)) {
            Toast.makeText(this, "Szabályzatok megértése és elfogadása kötelező!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void startShopping(String userId) {
        Intent intent = new Intent(this, ShopActivity.class);
        if (!Objects.equals(userId, "")) {
            intent.putExtra("userId", userId);
        }
        startActivity(intent);
    }

    public void redirectToLogin(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }
}