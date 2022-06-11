package com.example.appalojate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText etidcust,etname, etemail, etpassword;
    Button btnsave, btnsearch, btnedit, btndelete, btnlist;
    //esta variable guarda en la funcion search el valor a buscar
    String iddoc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etidcust=findViewById(R.id.etidcust);
        etname=findViewById(R.id.etname);
        etemail=findViewById(R.id.etemail);
        etpassword=findViewById(R.id.etpasswd);

        btnsave=findViewById(R.id.btnadd);
        btnsearch=findViewById(R.id.btnsearch);
        btnedit=findViewById(R.id.btnupdate);
        btndelete=findViewById(R.id.btndelete);
        btnlist=findViewById(R.id.btnlist);

        //boton guardar
        btnsave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                customerSave(etidcust.getText().toString(),etname.getText().toString(),
                        etemail.getText().toString(),etpassword.getText().toString());
            }
        });

        //boton buscar
        btnsearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                customerSearch(etidcust.getText().toString());
            }
        });

        //boton editar o actualizar
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCustomer(etname.getText().toString(), etemail.getText().toString(), etpassword.getText().toString());
            }
        });

        //boton eliminar
        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletCustomer();
            }
        });

        //bonton listar
        btnlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listCustumer();
            }
        });

    }

    //metodos de la clase
    //metodo guardar en firebase
    private void customerSave(String xidcust, String xname, String xemail, String xpassword)
    {
        // Create a new user with a first and last name
        Map<String, Object> ccustomer = new HashMap<>();
        ccustomer.put("idcust", xidcust);
        ccustomer.put("name", xname);
        ccustomer.put("email", xemail);
        ccustomer.put("password", xpassword);

        //salvar en bd online recuerda colecciones son las tablas y los documentos son los registros
        db.collection("customer")
                .add(ccustomer)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                {
                    @Override
                    public void onSuccess(DocumentReference documentReference)
                    {
                        Toast.makeText(getApplicationContext(),"Usuario agregado correctamente...",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Usuario NO agregado...",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //funcion buscar en firebase
    private void customerSearch(String fidcust)
    {
        db.collection("customer")
                //si quiero usar and asi where id=x and name=y uso varios whereEqualTo de database google
                .whereEqualTo("idcust", fidcust)
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
                                    //iddoc es una variable global
                                    iddoc = document.getId();//trae el id de google del customer a eliminar .
                                    etname.setText(document.getString("name"));
                                    etemail.setText(document.getString("email"));
                                    //por seguridad no traemos el password
                                    //etpassword.setText(document.getString("password"));
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"No existe el cliente",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    //metodo editar o actualizar
    private void editCustomer(String uname, String uemail, String upassword)
    {
        // Create a new user with a first and last name
        Map<String, Object> ccustomer = new HashMap<>();
        ccustomer.put("idcust", iddoc);
        ccustomer.put("name", uname);
        ccustomer.put("email", uemail);
        ccustomer.put("password", upassword);

        db.collection("customer").document(iddoc)
                .set(ccustomer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Cliente actualizado correctmente...",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"No se ha actualizado el cliente ...",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deletCustomer()
    {
        db.collection("customer").document(iddoc)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(register.this,"Cliente eliminado correctamente...",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Error, no se ha eliminado el Cliente...",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void listCustumer()
    {
        //no se hace nada
    }
}