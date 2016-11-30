package seguridad.elca.elcaremito;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
    String idusuar;
    TextView contador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        idusuar = getIntent().getStringExtra("idusuario");
        cargabdl(idusuar);
        contador=(TextView)findViewById(R.id.contador);
        contadores();

        final ListView lista=(ListView)findViewById(android.R.id.list);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int i, long l) {

                Map<String, Object> map = (Map<String, Object>)lista.getItemAtPosition(i);
                String idpedido = (String) map.get("idauxpedido");
                Intent x = new Intent(Pedidos.this, Detalles_pedido.class);
                x.putExtra("idpedido",idpedido  );
                x.putExtra("idusuario",idusuar );
                startActivity(x);

            }
        });


        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "presiono" + i, Toast.LENGTH_SHORT).show();

                Map<String, Object> map = (Map<String, Object>)lista.getItemAtPosition(i);
                String idpedido = (String) map.get("idauxpedido");
                System.out.println(idpedido);
                showSimplePopUp(idpedido);

                return true;
            }

        });

    }



    ////////////////////**************MENU ACTUALIZAR**********************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    ////////////////////////////////*************BOTON DE SINCRONIZACION DE BD*******************////////////////////


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        send_remito();

        int id = item.getItemId();
        if (id == R.id.refresh) {
            syncSQLiteMySQLDB();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    ////////////////*****************CARGA BASE DE DATOS SQLITE************************
    public void cargabdl(String idusuar)
    {
        ArrayList<HashMap<String, String>> userList =  controller.get_auxped(idusuar);
        if(userList.size()!=0){
            ListAdapter adapter = new SimpleAdapter( Pedidos.this,userList, R.layout.view_pedidos, new String[] { "cliente","descripcion"}, new int[] {R.id.clieteid, R.id.detalleid});
            ListView myList=(ListView)findViewById(android.R.id.list);
            myList.setAdapter(adapter);
        }
        //Initialize Progress Dialog properties

    }




    //////////////************************OBTIENE DATOS DE PHP************************


    public void syncSQLiteMySQLDB() {
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Sincronizando Pedidos, espere un momento............");
        prgDialog.setCancelable(false);
        prgDialog.show();

        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        params.put("idusuar", idusuar);
        // prgDialog.show();
        // Make Http call to getusers.php
        client.post("http://elca.sytes.net:2122/app_elca/detalles_pedidov7/get_pedido.php", params, new AsyncHttpResponseHandler() {


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
                //prgDialog.hide();
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
            //System.out.println(response);
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
            }else {
                Toast.makeText(getApplicationContext(), "No Tiene Pedidos Para Sincronizar",
                        Toast.LENGTH_LONG).show();
                prgDialog.hide();
                // send_remito();
                //System.out.println("hola");
            }
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



    /////////****************ESTO ES PARA DEVOLVERSE*****************///////////////

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


    //////////////***********actualiza status del estado*************************//////////////


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
            client.post("http://elca.sytes.net:2122/app_elca/detalles_pedidov7/updatesyncsts.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Toast.makeText(getApplicationContext(), "Se ha informado al supervisor de la sincronización", Toast.LENGTH_LONG).show();
                    // prgDialog.hide()
                }

                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
                    prgDialog.hide();
                    reloadActivity();
                    Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();

                }


            });
        } else {
            Toast.makeText(getApplicationContext(), "No tiene Pedidos pendientes", Toast.LENGTH_LONG).show();


        }

    }


    //////////////////////////////******************ENVIO DE REMITOS PENDIENTES Y AUXILIARES*************///////////////////////////


    public void send_remito()
    {

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Enviando y Recibiendo Pedidos Pendientes, espere un momento............");
        prgDialog.setCancelable(false);

        Gson gson = new GsonBuilder().create();
        prgDialog.show();

        ArrayList<HashMap<String, String>> aux_pen= controller.conidsop();
        if(aux_pen.size()!=0)
        {
            String new_ped = gson.toJson(aux_pen);
            //System.out.println(new_ped);
            send_aux_ped(new_ped);


        }



        ArrayList<HashMap<String, String>> pendiente= controller.consulrem();
        // Create GSON object

        if(pendiente.size()!=0 ) {


            for (HashMap<String, String> hashMap : pendiente) {
                // System.out.println(hashMap.get("fkidauxpedido"));
                //System.out.println("esta enviado los pedidos");

                ArrayList<HashMap<String, String>> dispList = controller.getdisp(hashMap.get("fkidauxpedido"));
                ArrayList<HashMap<String, String>> remlist = controller.getremito(hashMap.get("fkidauxpedido"));
                dispList.addAll(remlist);
                //controller.elim_aux(idped);
                String nn = gson.toJson(dispList);
                //System.out.println(nn);
                // System.out.println(userList);
                send_remito(nn,hashMap.get("fkidauxpedido"));
                //controller.elim_aux(hashMap.get("fkidauxpedido"));
                //reloadActivity();
            }




        }
        //else System.out.println("no tiene");

        boolean resp= contadores();
        if(resp)
        {
            prgDialog.hide();
        }

    }




    //////////////***********ENVIO DE REMITOS*************************//////////////


    // Method to inform remote MySQL DB about completion of Sync activity
    public void send_remito(String json, final String pedido) {
        // System.out.println("holaaaa aqui es :"+pedido);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        // prgDialog.show();

        params.put("remito", json);

        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Users
        client.post("http://elca.sytes.net:2122/app_elca/detalles_pedidov7/remito_envia.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                pendiente(response,pedido);
            }

            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                prgDialog.hide();
                reloadActivity();
                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();

            }


        });
    }




    //////////////***********ENVIA AUX_PEDIDOS NUEVOS*************************//////////////


    // Method to inform remote MySQL DB about completion of Sync activity
    public void send_aux_ped(String json) {
        //System.out.println(json);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        // prgDialog.show();

        params.put("aux_ped", json);
        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Users
        client.post("http://elca.sytes.net:2122/app_elca/detalles_pedidov7/aux_pedidos.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {


                // System.out.println(response);


            }

            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
                prgDialog.hide();
            }


        });
    }

///////////////********************CONTADOR DE REMITOS*************////////////////


    public boolean contadores ()
    {
        ArrayList<HashMap<String, String>> pendiente= controller.consulrem();
        // Create GSON object

        int i=0;
        for (HashMap<String, String> hashMap : pendiente) {
            i++;
        }
        String con=""+i;
        contador.setText(con);
        if(i==0)
        {
            //System.out.println("sizas");
            return true;

        }else{return false;}
    }


////////////////////*************************ELIMINAR PENDIENTES****************//////////////

    public void pendiente(String respon,String pedido)
    {
        //System.out.println(respon);
        //System.out.println("hola");
        if(respon.contains("recibido"))
        {
            controller.elim_aux(pedido);
        }else{

            Toast.makeText(getApplicationContext(), "No se logro Enviar Pedido",
                           Toast.LENGTH_LONG).show();
        }

        reloadActivity();
    }


    //////////******************POP UP**************//////////////
    private void showSimplePopUp(final String idped) {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Eliminar");
        helpBuilder.setMessage("Realmente desea elimiar el pedido");
        helpBuilder.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        System.out.println("si");
                        //controller.elim_aux(idped);
                        updatestado(idped);
                        //reloadActivity();
                    }
                });
        helpBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                System.out.println("no");
            }
        });

        // Remember, create doesn't show the dialog
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }




    //////////////***********actualiza status del estado*************************//////////////


    // Method to inform remote MySQL DB about completion of Sync activity
    public void updatestado(final String idped) {

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Eliminando Pedido............");
        prgDialog.setCancelable(false);
        prgDialog.show();

        //prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        Gson gson = new GsonBuilder().create();
        ArrayList<HashMap<String, String>> usersynclist;
        usersynclist = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        // Add status for each User in Hashmap
        map.put("Id", idped);
        // map.put("status", "0");
        usersynclist.add(map);
        String json = gson.toJson(usersynclist);
        System.out.println(json);




        params.put("estado", json);

        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Users
        client.post("http://elca.sytes.net:2122/app_elca/detalles_pedidov7/deletepedido.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getApplicationContext(), "Se ha informado al supervisor de la sincronización", Toast.LENGTH_LONG).show();
                prgDialog.hide();
                controller.elim_aux(idped);
                reloadActivity();

            }

            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
                prgDialog.hide();
            }


        });
    }



}



