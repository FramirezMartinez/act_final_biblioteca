package com.example.recicleview_cardview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Filterable {

    private List<DataItem> dataList;  // Lista de elementos para mostrar en el RecyclerView
    private List<DataItem> dataListFull;  // Lista para almacenar todos los elementos sin filtrar
    private OnItemClickListener onItemClickListener; // Interfaz para manejar clics en elementos del RecyclerView

    // Método para asignar datos a la lista de elementos
    public void setData(List<DataItem> dataList) {
        this.dataList = dataList;
        this.dataListFull = new ArrayList<>(dataList); // Copia de seguridad de la lista completa original
        notifyDataSetChanged(); // Actualizar RecyclerView al cambiar los datos
    }

    // Interfaz para manejar clics en elementos del RecyclerView
    public interface OnItemClickListener {
        void onItemClick(DataItem item);
    }

    // Clase que representa un elemento de datos individual en el RecyclerView
    public class DataItem {
        private String title; // Título del elemento
        private String content; // Contenido del elemento
        private int imageResourceId; // ID de recurso de imagen para el elemento
        private String pdfFileName; // Nombre del archivo PDF asociado con el elemento
        private String categoria; // Categoría del elemento

        // Constructor para inicializar los valores del elemento
        public DataItem(String title, String content, int imageResourceId, String pdfFileName, String categoria) {
            this.title = title;
            this.content = content;
            this.imageResourceId = imageResourceId;
            this.pdfFileName = pdfFileName;
            this.categoria = categoria;
        }

        // Métodos para obtener valores de los atributos del elemento
        public String getCategoria() {
            return categoria;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public int getImageResourceId() {
            return imageResourceId;
        }

        public String getPdfFileName() {
            return pdfFileName;
        }
    }

    // Constructor de MyAdapter
    public MyAdapter(List<DataItem> dataList, OnItemClickListener onItemClickListener) {
        this.dataList = dataList;
        this.onItemClickListener = onItemClickListener;
    }

    // Método para crear una nueva vista (invocado por el layout manager)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout para cada elemento del RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myadapter, parent, false);
        return new MyViewHolder(view);
    }

    // Método para reemplazar el contenido de una vista (invocado por el layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final DataItem currentItem = dataList.get(position);

        // Asignar datos a las vistas en el CardView
        holder.textViewTitle.setText(currentItem.getTitle());
        holder.textViewContent.setText(currentItem.getContent());
        holder.imageView.setImageResource(currentItem.getImageResourceId());

        // Configurar clic en la imagen
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(currentItem);
                }
            }
        });
    }

    // Método para obtener la cantidad de elementos en el RecyclerView
    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    // Implementación de Filterable para filtrar los elementos del RecyclerView
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String filtro = charSequence.toString().toLowerCase().trim();

                List<DataItem> resultList = new ArrayList<>();

                // Filtrar la lista completa basada en el texto ingresado
                for (DataItem item : dataListFull) {
                    if (item.getTitle().toLowerCase().contains(filtro) ||
                            item.getContent().toLowerCase().contains(filtro) ||
                            item.getCategoria().toLowerCase().contains(filtro)) {
                        resultList.add(item);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = resultList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                // Limpiar la lista actual y agregar los resultados filtrados
                dataList.clear();
                dataList.addAll((List<DataItem>) filterResults.values);
                notifyDataSetChanged(); // Notificar al RecyclerView sobre los cambios en los datos
            }
        };
    }

    // Clase estática que representa las vistas individuales de los elementos del RecyclerView
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView; // Vista de imagen
        private TextView textViewTitle; // Vista de título
        private TextView textViewContent; // Vista de contenido

        // Constructor para inicializar las vistas
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewContent = itemView.findViewById(R.id.textViewContent);
        }
    }
}