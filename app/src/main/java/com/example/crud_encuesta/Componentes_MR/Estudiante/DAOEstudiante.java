package com.example.crud_encuesta.Componentes_MR.Estudiante;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.crud_encuesta.Componentes_AP.Models.Usuario;
import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class DAOEstudiante implements Response.Listener<JSONObject>, Response.ErrorListener{

    private SQLiteDatabase cx;
    private ArrayList<Estudiante> lista = new ArrayList<>();
    private Estudiante estudiante;
    private Usuario usuario;
    private Context ct;
    private ProgressDialog progreso;
    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;

    public DAOEstudiante(Context ct){
        this.ct = ct;
        DatabaseAccess dba = DatabaseAccess.getInstance(ct);
        cx = dba.open();

        request = Volley.newRequestQueue(ct);
    }

    public boolean insertarUsuario(Usuario usuario){
        ContentValues contenedor = new ContentValues();
        contenedor.put("NOMUSUARIO",usuario.getNOMUSUARIO());
        contenedor.put("CLAVE",usuario.getCLAVE());
        contenedor.put("ROL",usuario.getROL());

        long resultado = cx.insert("USUARIO", null,contenedor);
        boolean respuesta = false;

        if(resultado > 0){
            respuesta = true;

            String url = "https://eisi.fia.ues.edu.sv/encuestas/pdm115_ws/ws_crear_usuario_estudiante.php?" +
                    "idusuario="+usuario.getIDUSUARIO()+"&" +
                    "nomusuario="+usuario.getNOMUSUARIO()+"&" +
                    "clave="+usuario.getCLAVE()+"&" +
                    "rol="+usuario.getROL();

            cargarWebService(0, url);
        }
        return respuesta;
    }

    public boolean insertar(Estudiante estd){
        ContentValues contenedor = new ContentValues();
        contenedor.put("CARNET", estd.getCarnet());
        contenedor.put("NOMBRE", estd.getNombre());
        contenedor.put("ACTIVO", estd.getActivo());
        contenedor.put("ANIO_INGRESO", estd.getAnio_ingreso());
        contenedor.put("IDUSUARIO",estd.getId_usuario());

        long resultado = cx.insert("ESTUDIANTE", null, contenedor);
        boolean respuesta = false;

        if(resultado > 0){
            respuesta = true;

            String url = "https://eisi.fia.ues.edu.sv/encuestas/pdm115_ws/ws_insertar_estudiante.php?" +
                    "id_est="+estd.getId()+"&" +
                    "idusuario="+estd.getId_usuario()+"&" +
                    "carnet="+estd.getCarnet()+"&" +
                    "nombre="+estd.getNombre()+"&" +
                    "activo="+estd.getActivo()+"&" +
                    "anio_ingreso="+estd.getAnio_ingreso();

            cargarWebService(1, url);
        }
        return respuesta;
    }

    private void cargarWebService(int i, String url) {
        progreso = new ProgressDialog(ct);
        String cargando = ct.getResources().getString(R.string.ws_cargando);
        progreso.setMessage(cargando);
        progreso.show();

        url = url.replace(" ","%20");
        Log.d("URL: ", url);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,null,this,this);
        request.add(jsonObjectRequest);
    }

    public boolean eliminar(int id){
        return (cx.delete("ESTUDIANTE", "ID_EST="+id, null))>0;
    }

    public boolean editarUsuario(Usuario usuario){
        ContentValues contenedor = new ContentValues();
        contenedor.put("IDUSUARIO",usuario.getIDUSUARIO());
        contenedor.put("NOMUSUARIO",usuario.getNOMUSUARIO());
        contenedor.put("CLAVE",usuario.getCLAVE());
        contenedor.put("ROL",usuario.getROL());
        return (cx.update("USUARIO", contenedor, "IDUSUARIO="+usuario.getIDUSUARIO(),null))>0;
    }

    public boolean editar(Estudiante estd){
        ContentValues contenedor = new ContentValues();
        contenedor.put("ID_EST", estd.getId());
        contenedor.put("CARNET", estd.getCarnet());
        contenedor.put("NOMBRE", estd.getNombre());
        contenedor.put("ACTIVO", estd.getActivo());
        contenedor.put("ANIO_INGRESO", estd.getAnio_ingreso());
        return (cx.update("ESTUDIANTE", contenedor, "ID_EST="+estd.getId(), null))>0;
    }

    public ArrayList<Estudiante> verTodos(){
        lista.clear();
        Cursor cursor = cx.rawQuery("SELECT * FROM ESTUDIANTE",null);

        if(cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                lista.add(new Estudiante(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getInt(5)));
            }while (cursor.moveToNext());
        }
        return lista;
    }

    public ArrayList<Estudiante> verBusqueda(String parametro){
        lista.clear();
        Cursor cursor = cx.rawQuery("SELECT * FROM ESTUDIANTE WHERE NOMBRE LIKE '%"+parametro+"%' OR CARNET LIKE'%"+parametro+"%'",null);
        if (cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                lista.add(new Estudiante(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getInt(5)));
            }while(cursor.moveToNext());
        }
        return lista;
    }

    public Estudiante verUno(int id){
        Cursor cursor = cx.rawQuery("SELECT * FROM ESTUDIANTE", null);
        cursor.moveToPosition(id);
        estudiante = new Estudiante(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getString(4),
                cursor.getInt(5));
        return estudiante;
    }

    public Usuario usuarioNombre(String nombre){
        Cursor cursor = cx.rawQuery("SELECT * FROM USUARIO WHERE NOMUSUARIO = '" +nombre+"'", null);
        cursor.moveToPosition(0);
        usuario = new Usuario(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3));
        return usuario;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(ct, R.string.ws_error, Toast.LENGTH_SHORT).show();
        progreso.hide();
    }

    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(ct, R.string.ws_exito, Toast.LENGTH_SHORT).show();
        progreso.hide();
    }
}