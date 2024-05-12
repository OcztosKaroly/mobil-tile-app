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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    private static final String LOG_TAG = SettingsActivity.class.getName();

    private EditText emailET, phoneET, taxNumberET,
            shippingNameET, shippingCountryET, shippingPostnumberET, shippingCityET, shippingAddressET,
            billingNameET, billingCountryET, billingPostnumberET, billingCityET, billingAddressET;
    private RadioGroup customerTypeRG;
    private CheckBox sameAddressCB;
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
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainSettings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mView = findViewById(R.id.mainSettings);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_row);
        mView.startAnimation(animation);

        mAuth = FirebaseAuth.getInstance();
        mUserFirebase = new UserFirebase();
        mShippingFirebase = new ShippingFirebase();
        mBillingFirebase = new BillingFirebase();

        initializeData();
    }

    private void initializeData() {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        emailET = findViewById(R.id.emailSettingsET);
        phoneET = findViewById(R.id.phoneSettingsET);
        taxNumberET = findViewById(R.id.taxNumSettingsET);
        customerTypeRG = findViewById(R.id.customerTypeSettingsRG);
        sameAddressCB = findViewById(R.id.sameAddressSettingsCheckBox);
        mUserFirebase.getUserById(userId).addOnCompleteListener(taskUser -> {
            if (taskUser.isSuccessful()) {
                User user = taskUser.getResult();
                if (user != null) {
                    sameAddressCB.setChecked(user.getIsBillingSameAsShipping());
                    emailET.setText(String.valueOf(user.getEmail()));
                    phoneET.setText(String.valueOf(user.getPhone()));
                    if (Objects.equals(user.getCustomerType(), "private")) {
                        customerTypeRG.check(R.id.privateCustomerSettingsRB);

                    } else if (Objects.equals(user.getCustomerType(), "company")) {
                        customerTypeRG.check(R.id.companySettingsRB);
                        taxNumberET.setText(String.valueOf(user.getTaxNumber()));
                    }
                    taxNumberET.setVisibility(Objects.equals(user.getCustomerType(), "company") ? View.VISIBLE : View.GONE);

                    customerTypeRG.setOnCheckedChangeListener((group, checkedId) -> {
                        if (checkedId == R.id.privateCustomerSettingsRB) {
                            taxNumberET.setVisibility(View.GONE);
                        } else if (checkedId == R.id.companySettingsRB) {
                            taxNumberET.setVisibility(View.VISIBLE);
                        }
                    });

                    shippingNameET = findViewById(R.id.shippingNameSettingsET);
                    shippingCountryET = findViewById(R.id.shippingCountrySettingsET);
                    shippingPostnumberET = findViewById(R.id.shippingPostnumberSettingsET);
                    shippingCityET = findViewById(R.id.shippingCitySettingsET);
                    shippingAddressET = findViewById(R.id.shippingAddressSettingsET);
                    mShippingFirebase.getShippingByUserId(userId).addOnCompleteListener(taskShipping -> {
                        if (taskShipping.isSuccessful()) {
                            Shipping shipping = taskShipping.getResult();
                            if (shipping != null) {
                                shippingNameET.setText(String.valueOf(shipping.getName()));
                                shippingCountryET.setText(String.valueOf(shipping.getCountry()));
                                shippingPostnumberET.setText(String.valueOf(shipping.getPostnumber()));
                                shippingCityET.setText(String.valueOf(shipping.getCity()));
                                shippingAddressET.setText(String.valueOf(shipping.getAddress()));
                            }
                        } else {
                            Exception e = taskShipping.getException();
                            Log.e(LOG_TAG, "Hiba történt a felhasználó szállítási adata lekérése közben ", e);
                        }
                    });

                    billingDataLayout = findViewById(R.id.billingDataSettingsLayout);
                    billingNameET = findViewById(R.id.billingNameSettingsET);
                    billingCountryET = findViewById(R.id.billingCountrySettingsET);
                    billingPostnumberET = findViewById(R.id.billingPostnumberSettingsET);
                    billingCityET = findViewById(R.id.billingCitySettingsET);
                    billingAddressET = findViewById(R.id.billingAddressSettingsET);
                    if (!sameAddressCB.isChecked()) {
                        mBillingFirebase.getBillingByUserId(userId).addOnCompleteListener(taskBilling -> {
                            if (taskBilling.isSuccessful()) {
                                Billing billing = taskBilling.getResult();
                                if (billing != null) {
                                    billingDataLayout.setVisibility(View.VISIBLE);
                                    billingNameET.setText(String.valueOf(billing.getName()));
                                    billingCountryET.setText(String.valueOf(billing.getCountry()));
                                    billingPostnumberET.setText(String.valueOf(billing.getPostnumber()));
                                    billingCityET.setText(String.valueOf(billing.getCity()));
                                    billingAddressET.setText(String.valueOf(billing.getAddress()));
                                }
                            } else {
                                Exception e = taskBilling.getException();
                                Log.e(LOG_TAG, "Hiba történt a felhasználó számlázási adata lekérése közben ", e);
                            }
                        });
                    } else {
                        billingDataLayout.setVisibility(View.GONE);
                    }
                    sameAddressCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            billingDataLayout.setVisibility(View.GONE);
                        } else {
                            billingDataLayout.setVisibility(View.VISIBLE);
                        }
                    });
                }
            } else {
                Exception e = taskUser.getException();
                Log.e(LOG_TAG, "Hiba történt a felhasználó lekérése közben ", e);
            }
        });
    }

    public void performModifications(View view) {
        if (!isInputValid()) {
            Log.e(LOG_TAG, "Valami hiba lépett fel a módosítások során!");
            return;
        }

        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        modifyData(userId);
    }

    private void modifyData(String userId) {
        String email = emailET.getText().toString().trim();
        String phone = phoneET.getText().toString().trim();
        String customerType = ((RadioButton)findViewById(customerTypeRG.getCheckedRadioButtonId())).getText().toString().trim();
        customerType = customerType.equals("Cég") ? "company" : "private";
        String taxNumber = customerType.equals("company") ? taxNumberET.getText().toString() : "";
        boolean isBillingSameAsShipping = sameAddressCB.isChecked();
        User user = new User(userId, email, phone, customerType, taxNumber, isBillingSameAsShipping);
        mUserFirebase.createUser(user, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Felhasználó sikeresen módosítva!", Toast.LENGTH_SHORT).show();

                String shippingName = shippingNameET.getText().toString().trim();
                String shippingCountry = shippingCountryET.getText().toString().trim();
                String shippingPostnumber = shippingPostnumberET.getText().toString().trim();
                String shippingCity = shippingCityET.getText().toString().trim();
                String shippingAddress = shippingAddressET.getText().toString().trim();
                Shipping shipping = new Shipping(userId, shippingName, shippingCountry, shippingPostnumber, shippingCity, shippingAddress);
                mShippingFirebase.createShipping(shipping, shippingTask -> {
                    if (shippingTask.isSuccessful()) {
                        Toast.makeText(this, "Felhasználó szállítási címe sikeresen módosítva!", Toast.LENGTH_SHORT).show();

                        if (!isBillingSameAsShipping) {
                            String billingName = billingNameET.getText().toString().trim();
                            String billingCountry = billingCountryET.getText().toString().trim();
                            String billingPostnumber = billingPostnumberET.getText().toString().trim();
                            String billingCity = billingCityET.getText().toString().trim();
                            String billingAddress = billingAddressET.getText().toString().trim();
                            Billing billing = new Billing(userId, billingName, billingCountry, billingPostnumber, billingCity, billingAddress);
                            mBillingFirebase.createBilling(billing, billingTask -> {
                                if (billingTask.isSuccessful()) {
                                    Toast.makeText(this, "Felhasználó számlázási címe sikeresen módosítva!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Hiba történt a felhasználó számlázási címe módosítása során!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            mBillingFirebase.deleteBilling(userId, billingDeletionTask -> {
                                if (billingDeletionTask.isSuccessful()) {
                                    Toast.makeText(SettingsActivity.this, "Felhasználó számlázási címe sikeresen módosítva!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SettingsActivity.this, "Hiba történt a felhasználó számlázási címe törlése során!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, "Hiba történt a felhasználó szállítási címe módosítása során!", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(this, "Sikeres profil módosítás.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Hiba történt a felhasználó módosítása során!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isInputValid() {
        String email = emailET.getText().toString().trim();
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

        if (email.isEmpty() || phone.isEmpty() || customerTypeText.isEmpty()) {
            Toast.makeText(this, "Minden mező kitöltése kötelező!", Toast.LENGTH_SHORT).show();
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

        return true;
    }

    public void deleteProfile(View view) {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mUserFirebase.deleteUser(userId, taskUser -> {
            if (taskUser.isSuccessful()) {
                mShippingFirebase.deleteShipping(userId, taskShipping -> {
                    if (!taskShipping.isSuccessful()) {
                        Exception exception = taskShipping.getException();
                        if (exception != null) {
                            Log.e(LOG_TAG, "Hiba a szállítási adatok törlésekor: " + exception.getMessage());
                        }
                    }
                });

                mBillingFirebase.deleteBilling(userId, taskBilling -> {
                    if (!taskBilling.isSuccessful()) {
                        Exception exception = taskBilling.getException();
                        if (exception != null) {
                            Log.e(LOG_TAG, "Hiba a számlázási adatok törlésekor: " + exception.getMessage());
                        }
                    }
                });

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    currentUser.delete().addOnSuccessListener(aVoid -> {
                        Log.d(LOG_TAG, "Felhasználó sikeresen törölve az Authentication rendszerből");
                    });
                }

                mAuth.signOut();
                Toast.makeText(this, "Profil sikeresen törölve.", Toast.LENGTH_SHORT).show();
            } else {
                Exception exception = taskUser.getException();
                if (exception != null) {
                    Log.e(LOG_TAG, "Hiba a felhasználó törlésekor: " + exception.getMessage());
                }
            }
        });
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}