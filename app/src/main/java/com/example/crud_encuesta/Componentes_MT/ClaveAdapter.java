package com.example.crud_encuesta.Componentes_MT;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crud_encuesta.DatabaseAccess;
import com.example.crud_encuesta.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClaveAdapter extends BaseAdapter implements AdapterView.OnItemSelectedListener{
    private LayoutInflater inflater = null;
    private int id_area;
    private int id_clave_seleccion;
    private List<Integer> id_areas = new ArrayList<>();
    List<String> claves= new ArrayList<>();
    private List<String> ides= new ArrayList<String>();
    Context context;
    int[] incons;

    public ClaveAdapter(List<String> claves, List<String> ides, Context context, int[] incons) {
        this.claves = claves;
        this.ides = ides;
        this.context = context;
        this.incons = incons;

        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        id_clave_seleccion=position;

        final View mView = inflater.inflate(R.layout.elemento_list_clave, null);
        final TextView num_clave = (TextView)mView.findViewById(R.id.msj_clave);
        ImageView informacion = (ImageView)mView.findViewById(R.id.info_clave);
        ImageView agregar = (ImageView)mView.findViewById(R.id.add_clave);
        ImageView editar = (ImageView)mView.findViewById(R.id.edit_clave);
        ImageView eliminar = (ImageView)mView.findViewById(R.id.delete_calve);

        num_clave.setText("Clave "+claves.get(position));
        informacion.setImageResource(incons[0]);
        agregar.setImageResource(incons[1]);
        editar.setImageResource(incons[2]);
        eliminar.setImageResource(incons[3]);

        /*if(mostrar_icono_info()){
            informacion.setVisibility(View.VISIBLE);
            informacion.setTag(position);
        }else{
            informacion.setVisibility(View.INVISIBLE);

        }*/

        informacion.setTag(position);
        agregar.setTag(position);
        editar.setTag(position);
        eliminar.setTag(position);

        informacion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final int id_seleccion = Integer.parseInt(v.getTag().toString());

                Intent i = new Intent(context, VerAreasActivity.class);
                i.putStringArrayListExtra("ides", (ArrayList<String>)ides);
                i.putStringArrayListExtra("claves", (ArrayList<String>)claves);
                i.putExtra("id", id_seleccion);
                context.startActivity(i);

            }
        });

        agregar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                final int id_seleccion = Integer.parseInt(v.getTag().toString());
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                final View mView = inflater.inflate(R.layout.relacion_clave_area, null);
                cargarAreas(mView);

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        agregar_relacion_clave_area(mView, id_seleccion);
                    }
                });

                mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();

            }
        });

        editar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                final int i = Integer.parseInt(v.getTag().toString());
                final View vEditar = inflater.inflate(R.layout.modal_area, null);
                EditText edt = (EditText)vEditar.findViewById(R.id.etArea);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);

                TextView txt = vEditar.findViewById(R.id.msj);
                txt.setText("Editar");
                edt.setText(claves.get(i));
                mBuilder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editar_clave(i, vEditar);
                        Toast.makeText(context, "Resgistro actualizado", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(context, ClaveActivity.class);
                        context.startActivity(i);
                    }
                });

                mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                mBuilder.setView(vEditar);
                AlertDialog dialog = mBuilder.create();
                dialog.show();

            }
        });

        eliminar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final int i = Integer.parseInt(v.getTag().toString());

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);

                mBuilder.setMessage("¿Desea eliminar esta clave?");
                mBuilder.setIcon(R.drawable.ic_delete);
                mBuilder.setTitle("Eliminar");

                mBuilder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminar_clave(i);
                        Toast.makeText(context, "Se ha eliminado con éxito", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(context, ClaveActivity.class);
                        context.startActivity(i);

                    }
                });

                mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = mBuilder.create();
                dialog.show();

            }
        });

        return mView;
    }

    @Override
    public int getCount() {
        return claves.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public List<String> areas(){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        SQLiteDatabase db = databaseAccess.open();

        List<String> areas = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT titulo FROM area", null);

        if(cursor!=null) {
            while (cursor.moveToNext()) {
                areas.add(cursor.getString(0));
            }
        }
        cursor.close();
        databaseAccess.close();

        return areas;
    }

    public void cargarAreas(View va){
        ArrayAdapter<String> comboAdapter;
        Spinner spAreas = (Spinner)va.findViewById(R.id.spAreas);
        spAreas.setOnItemSelectedListener(this);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        SQLiteDatabase db = databaseAccess.open();

        List<String> areas = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT id_area, titulo FROM area", null);

        while (cursor.moveToNext()) {
            id_areas.add(cursor.getInt(0));
            areas.add(cursor.getString(1));
        }
        cursor.close();

        databaseAccess.close();

        comboAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, areas);
        spAreas.setAdapter(comboAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spAreas:

                //Toast.makeText(context, "Modalidad: "+position, Toast.LENGTH_SHORT).show();
                id_area=position;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void agregar_relacion_clave_area(View v, int id_clave){
        EditText cant_preguntas = v.findViewById(R.id.etPreguntas);
        EditText peso_area = v.findViewById(R.id.etPeso);
        CheckBox aleatorio_cb = v.findViewById(R.id.cbAleatorio);

        int aleatorio=0;
        int peso = 0;
        int cantidad = 0;

        cantidad = Integer.parseInt(cant_preguntas.getText().toString());
        peso = Integer.parseInt(peso_area.getText().toString());
        int id = id_areas.get(id_area);
        int idClave = Integer.parseInt(claves.get(id_clave));
        if(aleatorio_cb.isChecked()) aleatorio=1;

        ContentValues registro = new ContentValues();

        registro.put("id_area", id);
        registro.put("id_clave", idClave);
        registro.put("numero_preguntas", cantidad);
        registro.put("aleatorio", aleatorio);
        registro.put("peso", peso);

        if(cantidad>0 && peso>0){
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
            SQLiteDatabase db = databaseAccess.open();

            db.insert("clave_area", null, registro);
            Toast.makeText(context, "Registro insertado con éxito", Toast.LENGTH_SHORT).show();
            Cursor cursor = db.rawQuery("SELECT id_clave_area FROM clave_area ORDER BY id_clave_area DESC LIMIT 1;", null);
            cursor.moveToFirst();

            Log.d("hey", cursor.getString(0));

            agregar_relacion_clave_area_pregunta(cantidad, cursor.getInt(0));
            cursor.close();
            databaseAccess.close();
        }else{
            Toast.makeText(context, "Debe completar todos los compos", Toast.LENGTH_SHORT).show();
        }

    }

    public void agregar_relacion_clave_area_pregunta(int cantidad, int id_clave_area){
        int aleatorio;
        int i=0;
        List<Integer> claves_areas=new ArrayList<>();
        Set<Integer> generados = new HashSet<>();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        SQLiteDatabase db = databaseAccess.open();

        Cursor cursor = db.rawQuery("SELECT id_pregunta FROM pregunta", null);
        ContentValues registro = new ContentValues();

        while (cursor.moveToNext()){
            claves_areas.add(cursor.getInt(0));
        }

        while (i<cantidad){
            aleatorio = -1;
            boolean generado = false;
            while (!generado) {
                int posible = (int) (Math.random() * cantidad) + 1;
                if (!generados.contains(posible)) {
                    generados.add(posible);
                    aleatorio = posible;
                    generado = true;
                }
            }
            registro.put("id_pregunta", claves_areas.get(aleatorio));
            registro.put("id_clave_area", id_clave_area);
            db.insert("clave_area_pregunta", null, registro );
            i++;
        }

        databaseAccess.close();
    }

    public void editar_clave(int id, View v){
        EditText edt = (EditText)v.findViewById(R.id.etArea);
        int ide = Integer.parseInt(ides.get(id));

        DatabaseAccess database = DatabaseAccess.getInstance(context);
        SQLiteDatabase db = database.open();

        ContentValues registro = new ContentValues();
        int numero_clave = Integer.parseInt(edt.getText().toString());

        if(!edt.getText().toString().isEmpty()){
            registro.put("numero_clave", numero_clave);
            db.update("clave", registro, "id_clave="+ide, null);
        }else{
            Toast.makeText(context, "La clave debe tener un número", Toast.LENGTH_SHORT).show();
        }
        database.close();
    }

    public void eliminar_clave(int id){
        int ide = Integer.parseInt(ides.get(id));
        DatabaseAccess database = DatabaseAccess.getInstance(context);
        SQLiteDatabase db = database.open();

        Cursor cursor = db.rawQuery("SELECT id_clave_area FROM clave_area WHERE id_clave="+ide, null);
        //cursor.close();

        while (cursor.moveToNext()){
            db.delete("clave_area_pregunta","id_clave_area="+cursor.getInt(0), null);
        }

        db.delete("clave","id_clave="+ide, null);

        Cursor cursor2 = db.rawQuery("SELECT * FROM clave_area WHERE id_clave="+ide, null);
        cursor2.moveToFirst();

        if(cursor2!=null){
            db.delete("clave_area", "id_clave="+ide, null);
        }

        database.close();
        cursor.close();
        cursor2.close();

    }

  /*  public boolean mostrar_icono_info(){

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        SQLiteDatabase db = databaseAccess.open();

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM clave_area WHERE id_clave="+Integer.parseInt(ides.get(id_clave_seleccion)), null);
        cursor.moveToFirst();
        int cantidad = cursor.getInt(0);
        databaseAccess.close();

        if(cantidad>0){
            return true;
        }else{
            return false;
        }
    }*/

}
