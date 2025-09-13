package com.example.pizzar11;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PaymentActivity extends AppCompatActivity {

    EditText etCardNumber, etExpiry, etCVV, etName;
    Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        etCardNumber = findViewById(R.id.et_card_number);
        etExpiry = findViewById(R.id.et_expiry);
        etCVV = findViewById(R.id.et_cvv);
        etName = findViewById(R.id.et_name);
        btnPay = findViewById(R.id.btn_pay);

        btnPay.setOnClickListener(v -> {
            String card = etCardNumber.getText().toString().trim();
            String expiry = etExpiry.getText().toString().trim();
            String cvv = etCVV.getText().toString().trim();
            String name = etName.getText().toString().trim();

            if(card.isEmpty() || expiry.isEmpty() || cvv.isEmpty() || name.isEmpty()){
                Toast.makeText(this, "Fill all details!", Toast.LENGTH_SHORT).show();
            } else if(card.length() < 16){
                Toast.makeText(this, "Invalid card number!", Toast.LENGTH_SHORT).show();
            } else {
                // Dummy payment success
                Toast.makeText(this, "Payment Successful 💳✅", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
