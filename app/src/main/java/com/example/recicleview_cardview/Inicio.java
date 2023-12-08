package com.example.recicleview_cardview;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Inicio extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 1000; // Tiempo de espera en milisegundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_inicio);
       //agregar animación
        Animation animacion1= AnimationUtils.loadAnimation(this,R.anim.desplazamiento_arriba);
        Animation animacion2= AnimationUtils.loadAnimation(this,R.anim.desplazamiento_abajo);

        TextView tv1=findViewById(R.id.tv1);
        TextView tv2=findViewById(R.id.tv2);
        ImageView logo= findViewById(R.id.logo);

        tv1.setAnimation(animacion2);
        tv2.setAnimation(animacion2);
        logo.setAnimation(animacion1);

        // Retraso para mostrar la pantalla de bienvenida
        new Handler().postDelayed( new Runnable()
        {
            @Override
            public void run()
            {
                // Iniciar la actividad principal después del tiempo de espera
                Intent intent = new Intent(Inicio.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);

    }

}