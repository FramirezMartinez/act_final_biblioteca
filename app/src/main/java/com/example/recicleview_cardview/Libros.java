package com.example.recicleview_cardview;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Libros extends AppCompatActivity implements View.OnClickListener {
private ImageView regresar;
    private boolean isPinchGesture = false;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private int pageIndex;
    private ScrollView scrollView;
    private LinearLayout pdfContainer;
    private int totalPagesLoaded = 0;
    private static final int PAGES_TO_LOAD = 10; // Cantidad de páginas a cargar cada vez

    private float scaleFactor = 1.0f;
    private Matrix matrix;
    private ImageView imageView;

    private static final float ZOOM_IN_FACTOR = 1.25f;
    private static final float ZOOM_OUT_FACTOR = 0.8f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libros);

        ImageView regresar = findViewById(R.id.regresar);
        regresar.setOnClickListener(this);

        scrollView = findViewById(R.id.scrollView);
        pdfContainer = findViewById(R.id.pdfContainer);

        ImageButton masButton = findViewById(R.id.mas);
        ImageButton menosButton = findViewById(R.id.menos);

       // Listeners para los botones de zoom
        masButton.setOnClickListener(v -> zoomIn());
        menosButton.setOnClickListener(v -> zoomOut());

        // Configuración de ImageView para manejar el zoom
        imageView = new ImageView(this);
        matrix = new Matrix();
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        imageView.setImageMatrix(matrix);

        // Obtener el nombre del archivo PDF del intent
        Intent intent = getIntent();
        if (intent != null) {
            String pdfFileName = intent.getStringExtra("pdfFileName");

            Log.d("PDFFileName", "PDF File Name: " + pdfFileName); // Verifica si obtienes el nombre del archivo aquí
            if (pdfFileName != null && !pdfFileName.isEmpty()) {

                // Llamar a la función para abrir el PDF
                openRendererFromAssets(pdfFileName);
                loadNextPages(); // Cargar las primeras páginas
            }
        }

        scrollView.setOnTouchListener((v, event) -> {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    isPinchGesture = true;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    isPinchGesture = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (event.getPointerCount() == 2 && isPinchGesture) {
                        float newScaleFactor = calculateNewScaleFactor(event);
                        applyZoom(newScaleFactor);
                        return true;
                    }
                    break;
            }
            return false;
        });

        // Agregar un listener para detectar el final del scroll
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);

            int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

            if (diff <= 0) {
                // El usuario llegó al final del scroll, cargar más páginas
                loadNextPages();
            }
        });
    }

    private void zoomIn() {
        scrollView.post(() -> {
            float currentZoom = scrollView.getScaleY();
            scrollView.setScaleY(currentZoom * ZOOM_IN_FACTOR);
            scrollView.setScaleX(currentZoom * ZOOM_IN_FACTOR);
        });
    }

    private void zoomOut() {
        scrollView.post(() -> {
            float currentZoom = scrollView.getScaleY();
            scrollView.setScaleY(currentZoom * ZOOM_OUT_FACTOR);
            scrollView.setScaleX(currentZoom * ZOOM_OUT_FACTOR);
        });
    }

    // Método para calcular el nuevo factor de escala para el zoom
    private float calculateNewScaleFactor(MotionEvent event) {
        float newScaleFactor = scaleFactor;
        float x1 = event.getX(0);
        float y1 = event.getY(0);
        float x2 = event.getX(1);
        float y2 = event.getY(1);

        float deltaX = x1 - x2;
        float deltaY = y1 - y2;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        final float scaleFactorIncrement = 0.05f;
        float zoomDifference = distance / 1000;

        newScaleFactor += (zoomDifference * scaleFactorIncrement);

        final float minScaleFactor = 0.5f;
        final float maxScaleFactor = 2.0f;
        if (newScaleFactor < minScaleFactor) {
            newScaleFactor = minScaleFactor;
        } else if (newScaleFactor > maxScaleFactor) {
            newScaleFactor = maxScaleFactor;
        }

        return newScaleFactor;
    }
    // Aplicar el zoom a la imagen
    private void applyZoom(float newScaleFactor) {
        scaleFactor = newScaleFactor;
        matrix.setScale(scaleFactor, scaleFactor);
        imageView.setImageMatrix(matrix);
        imageView.invalidate();
    }
    // Abrir el archivo PDF desde la carpeta de activos
    private void openRendererFromAssets(String pdfFileName) {
        AssetManager assetManager = getAssets();
        try {
            // Obtener la ruta del archivo PDF basada en el nombre proporcionado
            String pdfPath = "libros/" + pdfFileName;
            InputStream inputStream = assetManager.open(pdfPath);

            // Resto del código para abrir el PDF y mostrar las páginas
            File file = createFileFromInputStream(inputStream);
            if (file != null) {
                ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                if (parcelFileDescriptor != null) {
                    pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Crear un archivo temporal a partir de un InputStream
    private File createFileFromInputStream(InputStream inputStream) {
        try {
            File file = new File(getCacheDir(), "temp_pdf_file.pdf");
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    // Cargar las próximas páginas del PDF
    private void loadNextPages() {
        int pageCount = pdfRenderer.getPageCount();
        int pagesRemaining = pageCount - totalPagesLoaded;

        if (pagesRemaining > 0) {
            int pagesToLoad = Math.min(PAGES_TO_LOAD, pagesRemaining);

            for (int i = totalPagesLoaded; i < totalPagesLoaded + pagesToLoad; i++) {
                currentPage = pdfRenderer.openPage(i);
                Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
                currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(bitmap);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.width = 830; // Ancho deseado en píxeles
                layoutParams.height = 1000; // Alto deseado en píxeles
                imageView.setLayoutParams(layoutParams);

                pdfContainer.addView(imageView);
                currentPage.close();
            }

            totalPagesLoaded += pagesToLoad;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pdfRenderer != null) {
            pdfRenderer.close();
        }
    }

    // Manejar el clic del botón de regreso
       @Override
        public void onClick(View v) {
            if (v.getId() == R.id.regresar) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // Cierra la actividad actual
            }
        }
}

