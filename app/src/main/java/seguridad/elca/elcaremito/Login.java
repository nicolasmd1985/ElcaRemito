package seguridad.elca.elcaremito;

import android.app.ProgressDialog;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class Login extends AppCompatActivity implements View.OnClickListener {


    DBController controller = new DBController(this);

    private EditText user, pass;
    //boton para ingresar;
    private Button mSubmit;

    ProgressDialog prgDialog;

    HashMap<String, String> queryValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // setup input fields
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);

        // setup buttons
        mSubmit = (Button) findViewById(R.id.login);
        // mRegister = (Button) findViewById(R.id.register);

        // register listeners
        mSubmit.setOnClickListener(this);
        //mRegister.setOnClickListener(this);


        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Conectando......");
        prgDialog.setCancelable(false);



    }






    ////////////**********************FUNCION DE INGRESO**********************/////////
    public void registro()
    {

        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        //prgDialog.show();
        // Make Http call to getusers.php


        String username = user.getText().toString();
        String password = pass.getText().toString();


        //List params = new ArrayList();
        // params.add(new BasicNameValuePair("username", username));
        // params.add(new BasicNameValuePair("password", password));

        prgDialog.show();
        params.add("username", username);
        params.add("password", password);
        client.post("http://186.137.170.157:2122/nicolas/logintecnicosV2/logintec.php", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                prgDialog.hide();
                try {
                    int success;
                    JSONArray arr = new JSONArray(response);
                    System.out.println(arr.length());
                    int z=0;
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = (JSONObject) arr.get(i);
                        //System.out.println(obj.get("Nombre"));
                        success = Integer.parseInt(obj.get("success").toString());


                        if (success==1)
                        {

                            ArrayList<HashMap<String, String>> userList =  controller.getUsers();

                            if(userList.size()!=0) {
                                //System.out.println("Entro");

                                for (HashMap<String, String> hashMap : userList) {


                                    System.out.println(obj.get("Nombre"));
                                    System.out.println(hashMap.get("nombre"));

                                    System.out.println(obj.get("Apellido"));
                                    System.out.println(hashMap.get("apellido"));

                                    if ((obj.get("Nombre").toString().equals(hashMap.get("nombre"))) && (obj.get("Apellido").toString().equals(hashMap.get("apellido")))) {

                                        bdregistro();
                                        z = z + 1;
                                    }

                                }

                            }

                            System.out.println("zeta es "+z);


                            if(z!=1)
                            {

                                queryValues = new HashMap<String, String>();
                                // Add userID extracted from Object
                                queryValues.put("nombre", obj.get("Nombre").toString());
                                queryValues.put("apellido", obj.get("Apellido").toString());
                                queryValues.put("usuario", obj.get("Usuario").toString());
                                queryValues.put("pass", obj.get("pass").toString());
                                queryValues.put("tecnico", obj.get("tecnico").toString());
                                controller.insertUsers(queryValues);

                                Intent x = new Intent(Login.this, Pedidos.class);
                                Toast.makeText(Login.this, obj.get("message").toString(), Toast.LENGTH_LONG).show();
                                x.putExtra("idusuario", obj.get("tecnico").toString() );
                                prgDialog.dismiss();
                                startActivity(x);
                            }




                        } else {

                            Toast.makeText(Login.this, obj.get("message").toString(), Toast.LENGTH_LONG).show();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {

                //prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Dispositivo Sin Conexión a Internet",
                            Toast.LENGTH_LONG).show();
                }
                prgDialog.hide();

                bdregistro();






            }



        });
    }


    @Override
    public void onClick(View view) {

            registro();

        //    bdregistro();
       // ArrayList<HashMap<String, String>> userList =  controller.getUsers();

    }

    private void bdregistro() {

        String username = user.getText().toString();
        String password = pass.getText().toString();

        //Object[] array = loginlist.toArray();
        //System.out.println(loginlist.get(1));
        //loginlist.get(0);
        ArrayList<HashMap<String, String>> userList =  controller.getUsers();
        if(userList.size()!=0) {
            ArrayList<HashMap<String, String>> loginlist = controller.login();
            int i=0;
            for (HashMap<String, String> hashMap : loginlist) {

                //System.out.println(hashMap.get("usuario"));
                //System.out.println(hashMap.get("pass"));
                // System.out.println(username);


                if (username.equals(hashMap.get("usuario"))&&password.equals(hashMap.get("pass"))) {



                        //System.out.println("hola");

                        Intent x = new Intent(Login.this, Pedidos.class);
                        Toast.makeText(getApplicationContext(), "Login Correcto", Toast.LENGTH_LONG).show();
                        x.putExtra("idusuario",hashMap.get("idusuario")  );
                        startActivity(x);




                }else{i++;}
                if(userList.size()==i){Toast.makeText(getApplicationContext(), "Usuario ó Contraseña Incorrecta", Toast.LENGTH_LONG).show();}

            }
        }else{Toast.makeText(getApplicationContext(), "No existe ningun usuario registrado", Toast.LENGTH_LONG).show();}
       // finish();
    }
}



