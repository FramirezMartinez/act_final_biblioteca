package com.example.recicleview_cardview;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity implements MyAdapter.OnItemClickListener {
    private  GridLayoutManager layoutManager;
        private EditText editTextSearch;
        private RecyclerView recyclerView;
        private MyAdapter adapter;
        private List<MyAdapter.DataItem> dataList;
    
        private List<MyAdapter.DataItem> videojuegos;
        private List<MyAdapter.DataItem> ciencia;
        private List<MyAdapter.DataItem> tecnologia;
        private List<MyAdapter.DataItem> literatura;
        private List<MyAdapter.DataItem> historia;
        private List<MyAdapter.DataItem> otros;
        private MyAdapter.DataItem item;
    
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            editTextSearch = findViewById(R.id.editTextSearch);
            recyclerView = findViewById(R.id.recyclerView);

            int col = getResources().getInteger(R.integer.columnas);
            // Configuración del LinearLayoutManager predeterminado
            AtomicReference<LinearLayoutManager> linearLayoutManager = new AtomicReference<>(new LinearLayoutManager(this));
            recyclerView.setLayoutManager(linearLayoutManager.get());



            adapter = new MyAdapter(dataList, this); // Inicializar el adaptador
            dataList = generateDataList(); // Generar la lista de datos
            recyclerView.setAdapter(adapter); // Asignar el adaptador al RecyclerView

            // Detectar la orientación actual
            int orientation = getResources().getConfiguration().orientation;

            // Configurar el GridLayoutManager con 3 columnas en orientación horizontal y columnas predeterminadas en vertical
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                layoutManager = new GridLayoutManager(this, 2);
            } else {
                layoutManager = new GridLayoutManager(this, col);
            }

            recyclerView.setLayoutManager(layoutManager); // Asignar el GridLayoutManager al RecyclerView


    
    // Configurar el RecyclerView
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
    
    // Inicializar el adaptador y asignarle los datos generados
            adapter = new MyAdapter(dataList, this);
    
    // Generar la lista de datos utilizando el método generateDataList()
            dataList = generateDataList();
    
    // Asignar los datos al adaptador después de la inicialización
            adapter.setData(dataList);
    
    // Notificar al adaptador sobre los cambios en los datos
            adapter.notifyDataSetChanged();
    
    // Establecer el adaptador en el RecyclerView
            recyclerView.setAdapter(adapter);
    
    // Configurar el Spinner para cambiar entre categorías
            Spinner spinner = findViewById(R.id.spinner);
            configureSpinner(spinner);
    
            // Buscador
             editTextSearch = findViewById(R.id.editTextSearch);
    
            editTextSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Filtrar el RecyclerView cuando el texto cambia
                    adapter.getFilter().filter(charSequence);
                }
    
                @Override
                public void afterTextChanged(Editable s) {

                }
            });
    
            editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    Log.d("EditorAction", "Action ID: " + actionId);
                    if (EditorInfo.IME_ACTION_DONE == actionId) {
                        // Ocultar el teclado cuando se presiona "Enter"
                        hideKeyboard();
                        return false;
                    }
                    return false;
                }
            });
    
            // Oculta el teclado cuando se toca fuera del EditText
            editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard();
                    }
                }
            });
    
    

    
    
            // Inicializar el adaptador y asignarle los datos generados
            adapter = new MyAdapter(dataList, this);
            // Generar la lista de datos utilizando el método generateDataList()
            dataList = generateDataList();
    
    // Asignar los datos al adaptador después de la inicialización
            adapter.setData(dataList);
    
    // Notificar al adaptador sobre los cambios en los datos
            adapter.notifyDataSetChanged();
    
    
            // Inicializar las listas de categorías y distribuir los libros
            videojuegos = new ArrayList<>();
            ciencia = new ArrayList<>();
            tecnologia = new ArrayList<>();
            literatura = new ArrayList<>();
            historia = new ArrayList<>();
            otros = new ArrayList<>();
            distributeBooksIntoCategories(dataList);
    
    
            // Configurar el RecyclerView
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
    
    
        }
    
    
    
        private void hideKeyboard() {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        }
    
    
    
        private void configureSpinner(Spinner spinner) {
            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.spinner_items,
                    android.R.layout.simple_spinner_item
            );
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);
    
            spinner.setSelection(0);
    
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = parent.getItemAtPosition(position).toString();
    
                    switch (selectedItem) {
                        case "Video juegos":
                            adapter.setData(videojuegos);
                            break;
                        case "Ciencia":
                            adapter.setData(ciencia);
                            break;
                        case "Tecnología":
                            adapter.setData(tecnologia);
                            break;
                        case "Literatura":
                            adapter.setData(literatura);
                            break;
                        case "Historia":
                            adapter.setData(historia);
                            break;
                        case "Otros":
                            adapter.setData(otros);
                            break;
                        default:
                            adapter.setData(dataList); // Mostrar todos los libros si no se selecciona ninguna categoría
                            break;
                    }
                    adapter.notifyDataSetChanged(); // Notificar al adaptador sobre los cambios en los datos
                }
    
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Acción cuando no se selecciona nada en el Spinner
                }
            });
        }
    
    
    
        private List<MyAdapter.DataItem> generateDataList() {
            List<MyAdapter.DataItem> dataList = new ArrayList<>();
    
            // Agregar libros a la lista
            dataList.add(adapter.new DataItem("El hombre anumérico", "De: Jon Alle APulos", R.drawable.uno, "el_hombre_anumerico.pdf", "Videojuegos"));
            dataList.add(adapter.new DataItem("Elon Musk", "De:Ashlee Vance", R.drawable.dos, "Elon_Musk.pdf", "Videojuegos"));
            dataList.add(adapter.new DataItem("Blockchain", "De:Alexander", R.drawable.tres, "Blockchain.pdf", "Videojuegos"));
            dataList.add(adapter.new DataItem("Inteligencia Artificial", "De: Pablo Rodriguez", R.drawable.cuatro, "Inteligencia_Artificial.pdf", "Videojuegos"));
            dataList.add(adapter.new DataItem("Mayoritario", "De: Sareeta Amurete", R.drawable.cinco, "Mayoritario.pdf", "Videojuegos"));
    
            //libros categoria  Ciencia
    
            dataList.add(adapter.new DataItem("Conociendo Big Data", "De: Universidad de Colombia", R.drawable.seis, "universidad.pdf", "Ciencia"));
           //cambiar
            dataList.add(adapter.new DataItem("Aprende Python", "De:Sergio Delgado Quintero", R.drawable.siete, "aprendepython.pdf", "Ciencia"));
            dataList.add(adapter.new DataItem("Conociendo Big Data", "De:CARI", R.drawable.ocho, "ciberdefensa_riesgos_amenazas.pdf", "Ciencia"));
    //cambiar
            dataList.add(adapter.new DataItem("Ciber seguridad", "De:Ariel", R.drawable.nueve, "Ciberseguridad.pdf", "Ciencia"));
            dataList.add(adapter.new DataItem("Cuarta Revolución Industrial", "De:BERAUD MARTÍNEZ, IGOR PIOTR", R.drawable.diez, "Cuarta Revolución Industrial.pdf", "Ciencia"));
    
    
           //Libros categoria Tecnología
    
            dataList.add(adapter.new DataItem("El efecto de Facebook", "De:David Kirkpatrick", R.drawable.once, "El efecto Facebook.pdf", "Tecnología"));
            dataList.add(adapter.new DataItem("El poder del ahora", "De:Eckhart Tolle", R.drawable.doce, "El poder del ahora.pdf", "Tecnología"));
    //cambiar porque tiene muchas páginas
            dataList.add(adapter.new DataItem("El hombre duplicado", "De:José Saramago", R.drawable.trece, "El hombre duplicado.pdf", "Tecnología"));
            dataList.add(adapter.new DataItem("el monje que vendio su ferrari", "De:ROBIN S. SHARMA", R.drawable.catorce, "el-monje-que-vendio-su-ferrari.pdf", "Tecnología"));
    //Libros cortos
            dataList.add(adapter.new DataItem("guia_aviones_vfinal_ES", "De:Grupo Empty Leg", R.drawable.diez6, "guia_aviones_vfinal_ES.pdf", "Tecnología"));
    
    
            //Libros categoria literatura
    
    
            //libros muchas paginas
            dataList.add(adapter.new DataItem("Homo Deus", "De:YUVAL NOAH HARARI", R.drawable.diez7, "Homo-Deus.pdf", "Literatura"));
    
    //iniccia sin probar
            dataList.add(adapter.new DataItem("Interet de las cosas", "De:JORDI SALAZAR Y SANTIAGO SILVESTRE", R.drawable.diez8, "Interet de las cosas.pdf", "Literatura"));
    //revisado lento
            dataList.add(adapter.new DataItem("La cuarta revolucion industrial", "De:Klaus Schwab ", R.drawable.diez9, "La cuarta revolucion industrial - Klaus Schwab.pdf", "Literatura"));
            dataList.add(adapter.new DataItem("La revolución de la realidad virtual y aumentada", "De:JOSU OTEGUI CASTILLO", R.drawable.veinte, "La revolución de la realidad virtual y aumentada.pdf", "Literatura"));
    
    //lento y no abre
            dataList.add(adapter.new DataItem("Las-mil-y-una-noches", "De:Juan Vernedt", R.drawable.veinte1, "Las-mil-y-una-noches.pdf", "Literatura"));
    
    
            //Libros categoria Historia
    
            //lentos
            dataList.add(adapter.new DataItem("La-tercera-ola", "De:Juan Vernedt", R.drawable.veinte2, "La-tercera-ola.pdf", "Historia"));
            dataList.add(adapter.new DataItem("Manual de Super vivencia Hacker", "De:José Manuel Redondo López", R.drawable.veinte3, "ManualdeSupervivenciaHacker.pdf", "Historia"));
    //Excelente
            dataList.add(adapter.new DataItem("Máquina del tiempo", "De: Herbert George Wells", R.drawable.veinte4, "maquina_del_tiempo.pdf", "Historia"));
    //lento
            dataList.add(adapter.new DataItem("Menton Seymour El Cuento Hispanoamericano", "De: SEYMOUR MENTON ", R.drawable.veinte5, "Menton-Seymour-El-Cuento-Hispanoamericano.pdf", "Historia"));
    //Excelente
            dataList.add(adapter.new DataItem("Nikola tesla", "De:Fundación Telefónica Madrid", R.drawable.veinte6, "Nikola tesla.pdf", "Historia"));
    
            //Libros categoria Otros
    
            //lento
            dataList.add(adapter.new DataItem("Steve Jobs", "De:Walter Isaacson", R.drawable.veinte7, "Steve Jobs.pdf", "Otros"));
    //bien
            dataList.add(adapter.new DataItem("T-3385", "De:MIGUEL ÁNGEL PATZI MAMANI ", R.drawable.veinte8, "T-3385.pdf", "Otros"));
    
    //bien
            dataList.add(adapter.new DataItem("Tecnologias digitales para la nueva era", "De:CEPAL ", R.drawable.veinte9, "Tecnologias digitales para la nueva era.pdf", "Otros"));
    //lento
            dataList.add(adapter.new DataItem("Wiener Norbert Cibernetica y sociedad 1958", "De: NORBERT WIENER ", R.drawable.treinta, "Wiener_Norbert_Cibernetica_y_sociedad_1958.pdf", "Otros"));
            dataList.add(adapter.new DataItem("fuente ovejuna ", "De: FERNANDEZ ESCOBES", R.drawable.quince, "fuenteovejuna.pdf", "Otros"));
    
    
            return dataList;
        }
    
        private void distributeBooksIntoCategories(List<MyAdapter.DataItem> dataList) {
            for (MyAdapter.DataItem item : dataList) {
                // Aquí deberías implementar la lógica para asignar cada libro a su categoría correspondiente
    
                String category = item.getCategoria(); // Obtener la categoría del libro
    
                switch (category.toLowerCase()) {
                    case "videojuegos":
                        videojuegos.add(item);
                        break;
                    case "ciencia":
                        ciencia.add(item);
                        break;
                    case "tecnología":
                        tecnologia.add(item);
                        break;
                    case "literatura":
                        literatura.add(item);
                        break;
                    case "historia":
                        historia.add(item);
                        break;
                    case "otros":
                        otros.add(item);
                        break;
                    default:
                        // Manejo para una categoría desconocida (opcional)
                        break;
                }
            }
        }
    
        @Override
        public void onItemClick(MyAdapter.DataItem item) {
    
                // Obtener el nombre del archivo PDF del elemento clicado
                String pdfFileName = item.getPdfFileName();
    
                // Crear la intención para abrir la actividad que muestra el PDF
                Intent intent = new Intent(this, Libros.class);
                intent.putExtra("pdfFileName", pdfFileName);
    
                // Iniciar la actividad del visor de PDF
                startActivity(intent);
            }
        }
