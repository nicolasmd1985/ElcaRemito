package seguridad.elca.elcaremito;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Pedidos  extends ActionBarActivity {


    //DB Class to perform DB related operations
    DBController controller = new DBController(this);
    //Progress Dialog Object
    ProgressDialog prgDialog;

    HashMap<String, String> queryValues;

    ListView lista;

    String idusuar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        idusuar = getIntent().getStringExtra("idusuario");
        System.out.println("idusu"+idusuar);
        cargabdl(idusuar);
        prgDialog.dismiss();


        final ListView lista=(ListView)findViewById(android.R.id.list);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "presiono " + i, Toast.LENGTH_SHORT).show();



                Map<String, Object> map = (Map<String, Object>)lista.getItemAtPosition(i);
                String idpedido = (String) map.get("idauxpedido");
                //System.out.println(lista.getItemAtPosition(i));
                //System.out.println(idpedido);






                //StringBuilder sb = new StringBuilder();
                //sb.append(i+1);
                Intent x = new Intent(Pedidos.this, Detalles_pedido.class);
                x.putExtra("idpedido",idpedido  );
                x.putExtra("idusuario",idusuar );
                //for (HashMap<String, String> hashMap : loginlist) {}
                //System.out.println(lista.);
                startActivity(x);





            }
        });



    }



    ////////////////////**************MENU ACTUALIZAR**********************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    ////////////////////////////////*************BOTON DE SINCRONIZACION DE BD*******************////////////////////


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        send_remito();
        int id = item.getItemId();
        //When Sync action button is clicked
        if (id == R.id.refresh) {
            //Sync SQLite DB data to remote MySQL DB
            syncSQLiteMySQLDB();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    ////////////////*****************CARGA BASE DE DATOS SQLITE************************
    public void cargabdl(String idusuar)
    {


        //inicia lista
        ArrayList<HashMap<String, String>> userList =  controller.get_auxped(idusuar);


        if(userList.size()!=0){
            //Set the User Array list in ListView
            ListAdapter adapter = new SimpleAdapter( Pedidos.this,userList, R.layout.view_pedidos, new String[] { "cliente","descripcion"}, new int[] {R.id.clieteid, R.id.detalleid});
            ListView myList=(ListView)findViewById(android.R.id.list);
            myList.setAdapter(adapter);
            //Display Sync status of SQLite DB
            // Toast.makeText(getApplicationContext(), controller.getSyncStatus(), Toast.LENGTH_LONG).show();
        }
        //Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Sincronizando Pedidos, espere un momento............");
        prgDialog.setCancelable(false);


    }




    //////////////************************SINCRONIZA BASE DE DATOS PHP************************


    public void syncSQLiteMySQLDB() {
        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        params.put("idusuar", idusuar);
        prgDialog.show();
        // Make Http call to getusers.php
        client.post("http://186.137.170.157:2122/nicolas/detalles_pedidov4/get_pedido.php", params, new AsyncHttpResponseHandler() {


            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Dispositivo Sin Conexión a Internet",
                            Toast.LENGTH_LONG).show();
                }
            }


/////////////////*************GO TO UPDATESQLITE********////////////////////

            @Override
            public void onSuccess(String response) {
                prgDialog.hide();
                // Update SQLite DB with response sent by getusers.php
                updateSQLite(response);
            }




        });
    }






/////////////////////////////*******************ACTUALIZA SQLITE*********************////////////////////


    public void updateSQLite(String response){
        ArrayList<HashMap<String, String>> usersynclist;
        usersynclist = new ArrayList<HashMap<String, String>>();
        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            System.out.println(response);
            // If no of array elements is not zero
            if(arr.length() != 0){
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);

                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();
                    // Add userID extracted from Object
                    queryValues.put("idauxpedido", obj.get("idauxpedido").toString());
                    // Add userName extracted from Object
                    queryValues.put("idtecnico", obj.get("idtecnico").toString());
                    // Add userName extracted from Object
                    queryValues.put("descripcion", obj.get("descripcion").toString());
                    // Insert User into SQLite DB
                    queryValues.put("idnumsoporte", obj.get("idnumsoporte").toString());
                    // Insert User into SQLite DB
                    queryValues.put("cliente", obj.get("cliente").toString());
                    // Insert User into SQLite DB
                    queryValues.put("calle", obj.get("calle").toString());
                    // Insert User into SQLite DB
                    queryValues.put("numero", obj.get("numero").toString());
                    // Insert User into SQLite DB
                    queryValues.put("ciudad", obj.get("ciudad").toString());
                    // Insert User into SQLite DB
                    queryValues.put("provincia", obj.get("provincia").toString());
                    // Insert User into SQLite DB
                    queryValues.put("fechacr", obj.get("fechacr").toString());
                    // Insert User into SQLite DB
                    queryValues.put("fechack", obj.get("fechack").toString());
                    // Insert User into SQLite DB
                    controller.inser_auxped(queryValues);
                    HashMap<String, String> map = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    map.put("Id", obj.get("idauxpedido").toString());
                    map.put("status", "1");
                    usersynclist.add(map);
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                updateMySQLSyncSts(gson.toJson(usersynclist));
                // Reload the Main Activity
                reloadActivity();
            }else {Toast.makeText(getApplicationContext(), "No Tiene Pedidos Para Sincronizar",
                    Toast.LENGTH_LONG).show();}
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }







///////////////////////////********RECARGA ACTIVIDAD//////////////////


    // Reload MainActivity
    public void reloadActivity() {
        Intent objIntent = new Intent(getApplicationContext(), Pedidos.class);
        objIntent.putExtra("idusuario",idusuar );
        startActivity(objIntent);
    }



    ////////////////////******************AGREGA PEDIDO******************//////////////////


    //Add User method getting called on clicking (+) button
    public void addPedidos(View view) {
       Intent objIntent = new Intent(getApplicationContext(), Nuevo_pedido.class);
        objIntent.putExtra("idusuario",idusuar );
       startActivity(objIntent);
    }




    //****************ESTO ES PARA DEVOLVERSE*****************

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            Intent i = new Intent(Pedidos.this, Login.class);
            i.putExtra("idusuario",idusuar );
            startActivity(i);
            //return true;
        }
        return super.onKeyDown(keyCode, event);
    }





    //////////////***********actualiza status*************************//////////////


    // Method to inform remote MySQL DB about completion of Sync activity
    public void updateMySQLSyncSts(String json) {
        System.out.println(json);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        ArrayList<HashMap<String, String>> userList = controller.get_auxped(idusuar);
        if (userList.size() != 0) {

            prgDialog.show();

            params.put("estado", json);

            // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Users
            client.post("http://186.137.170.157:2122/nicolas/detalles_pedidov3/updatesyncsts.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Toast.makeText(getApplicationContext(), "Se ha informado al supervisor de la sincronización", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
                    Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();

                }


            });
        } else {
            Toast.makeText(getApplicationContext(), "No tiene Pedidos pendientes", Toast.LENGTH_LONG).show();
        }

    }




    //////////////////////////////******************ENVIO DE REMITOS PENDIENTES*************///////////////////////////


    public void send_remito()
    {

        ArrayList<HashMap<String, String>> pendiente= controller.consulrem();
        // Create GSON object
        Gson gson = new GsonBuilder().create();


        if(pendiente.size()!=0) {


            for (HashMap<String, String> hashMap : pendiente) {
                System.out.println(hashMap.get("fkidauxpedido"));


                ArrayList<HashMap<String, String>> dispList = controller.getdisp(hashMap.get("fkidauxpedido"));
                ArrayList<HashMap<String, String>> remlist = controller.getremito(hashMap.get("fkidauxpedido"));
                dispList.addAll(remlist);
                //controller.elim_aux(idped);
                String nn = gson.toJson(dispList);
                //System.out.println(nn);
                // System.out.println(userList);
                send_remito(nn);
            }

        }else System.out.println("no tiene");


    }




    //////////////***********actualiza status*************************//////////////


    // Method to inform remote MySQL DB about completion of Sync activity
    public void send_remito(String json) {
        System.out.println(json);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        // prgDialog.show();

        params.put("remito", json);
        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Users
        client.post("http://192.168.5.51:2122/nicolas/detalles_pedidov4/recep_remito.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_LONG).show();

                System.out.println(response);
            }

            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();

            }


        });
    }



}



