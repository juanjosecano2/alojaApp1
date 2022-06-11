package com.example.appalojate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class prestamos extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText monto,tipocredi,cuotas;
    TextView bienvenida,valorcuota,totaldeuda;
    Button btncalcular, btnlimpiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestamos);
        bienvenida = findViewById(R.id.tvbienvenida);
        bienvenida.setText(" Bienvenid@ "+getIntent().getStringExtra("intent_email"));
        monto = findViewById(R.id.etmonto);
        tipocredi = findViewById(R.id.ettipo);
        cuotas = findViewById(R.id.etnrocuotas);
        valorcuota = findViewById(R.id.etvalorcuota);
        totaldeuda = findViewById(R.id.ettotaldeuda);
        btncalcular = findViewById(R.id.btncalcular);
        btnlimpiar=findViewById(R.id.btnlimpiar);

        btncalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vmonto = monto.getText().toString().trim();
                String vtipocredi = tipocredi.getText().toString().trim();
                String vcuotas = cuotas.getText().toString().trim();

                calcular(vmonto,vtipocredi,vcuotas);

            }
        });

        btnlimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monto.setText(" ");
                tipocredi.setText(" ");
                cuotas.setText(" ");
                valorcuota.setText(" ");
                totaldeuda.setText(" ");
            }

        });


    }

    private void calcular(String vmonto, String vtipocredi, String vcuotas) {
        int xmonto,xcuotas,xValorcuota,xTotaldeuda;
        xmonto = Integer.parseInt(vmonto);
        xcuotas = Integer.parseInt(vcuotas);
        double fvalorcuota = 0;
        double ftotaldeuda = 0;

        if (vmonto.isEmpty() && vtipocredi.isEmpty() && vcuotas.isEmpty()){
            Toast.makeText(getApplicationContext(),"Llenar bien los campos",Toast.LENGTH_SHORT).show();
        }
        else {
            if (xmonto >= 1000000 && xmonto <= 100000000){
                if (xcuotas >=1 && xcuotas <=64){
                    Map<String, Object> cprestamo = new HashMap<>();
                    cprestamo.put("monto", xmonto);
                    cprestamo.put("tipo", vtipocredi);
                    cprestamo.put("cuotas", xcuotas);
                    cprestamo.put("email", getIntent().getStringExtra("intent_email"));

                    db.collection("prestamo")
                            .add(cprestamo)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                            {
                                @Override
                                public void onSuccess(DocumentReference documentReference)
                                {
                                    Toast.makeText(getApplicationContext(),"Prestamo agregado correctamente...",
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Prestamo NO agregado...",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                    switch (vtipocredi){
                        case "vivienda": ftotaldeuda = (xmonto * 0.01 * xcuotas) + xmonto ;
                        fvalorcuota = ftotaldeuda / xcuotas;
                        Toast.makeText(getApplicationContext(),"Vivienda",Toast.LENGTH_SHORT).show();
                        break;

                        case "educacion": ftotaldeuda = (xmonto * 0.005 * xcuotas) + xmonto;
                            fvalorcuota = ftotaldeuda / xcuotas;
                            Toast.makeText(getApplicationContext(),"Educacion",Toast.LENGTH_SHORT).show();
                        break;

                        case "libre invercion": ftotaldeuda = (xmonto * 0.015 * xcuotas) + xmonto;
                            fvalorcuota = ftotaldeuda / xcuotas;
                            Toast.makeText(getApplicationContext(),"libre",Toast.LENGTH_SHORT).show();
                        break;

                        default: Toast.makeText(getApplicationContext(),"Los prestamos son de tipo vivienda,educacion o libre invercion",Toast.LENGTH_SHORT).show();

                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Las cuotas van desde 1 a 64",Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Monto entre entre 1 millón y 100 millones",Toast.LENGTH_SHORT).show();
            }
        }
        DecimalFormat formato = new DecimalFormat("###,###,###,###");
        valorcuota.setText(formato.format(fvalorcuota));
        totaldeuda.setText(formato.format(ftotaldeuda));
    }
}