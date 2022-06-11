package com.example.appalojate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText correo,clave;
    Button ingresar;
    TextView registrar;
    String name, email, iddoc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //crea variables y las relaciona con el layaut
        correo=findViewById(R.id.etcorreo);
        clave=findViewById(R.id.etclave);
        ingresar=findViewById(R.id.btningresar);
        registrar=findViewById(R.id.tvregistrar);


        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciar_session(correo.getText().toString(),clave.getText().toString());
            }
        });


        //crea el evento clic del botón registrar
        registrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //llama un nuevo layout forma larga permite enviar variables y forma corta no
                Intent intent = new Intent(MainActivity.this, register.class);
                startActivity(intent);
                //startActivity(new Intent(MainActivity.this,register.class));
            }
        });


    }

    private void iniciar_session(String correo, String clave)
    {
        //funcion buscar credenciales email y password en la tabla customer de firebase
        db.collection("customer")
                //si quiero usar and asi where id=x and name=y uso varios whereEqualTo de database google
                .whereEqualTo("email", correo)
                .whereEqualTo("password", clave)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    //task: si esa tarea fue ejucata correctamente
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            if (!task.getResult().isEmpty())
                            {
                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    //son variables globlaes
                                    iddoc = document.getId();//trae el id de google del customer a eliminar .
                                    name=document.getString("name");
                                    email=document.getString("email");
                                }
                                Toast.makeText(getApplicationContext(),"Bienvenido",Toast.LENGTH_SHORT).show();

                                //se envia el nombre como variable main.java o sea al activity_main2.xml (panel)
                                Intent intent = new Intent(MainActivity.this, prestamos.class);
                                intent.putExtra("intent_email", email);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"No existe el cliente",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

}