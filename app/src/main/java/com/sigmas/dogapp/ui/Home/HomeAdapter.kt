package com.sigmas.dogapp.ui.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sigmas.dogapp.R
import com.sigmas.dogapp.Data.Model.Cita
import com.sigmas.dogapp.Data.Model.ImagenRazaResponse
import com.sigmas.dogapp.Network.DogApiService
import com.sigmas.dogapp.network.RetrofitRazas
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// [Adaptador del RecyclerView] (Muestra la lista de citas en el Home, permite clics sobre cada item)
class HomeAdapter(
    private var citas: List<Cita>,
    private val onItemClick: (Cita) -> Unit
) : RecyclerView.Adapter<HomeAdapter.CitaViewHolder>() {

    // [Crear ViewHolder] (Infla el layout XML de cada ítem del RecyclerView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_lista_citas, parent, false)
        return CitaViewHolder(view)
    }

    // [Vincular datos al ViewHolder] (Llama al método bind() para llenar los datos del item)
    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.bind(citas[position])
    }

    // [Cantidad de elementos] (Devuelve cuántas citas se van a mostrar en el RecyclerView)
    override fun getItemCount(): Int = citas.size

    // [Clase interna del ViewHolder] (Define cómo se muestra cada item y su comportamiento)
    inner class CitaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgMascota: CircleImageView = itemView.findViewById(R.id.imgMascota)
        private val tvNombreMascota: TextView = itemView.findViewById(R.id.tvNombreMascota)
        private val tvSintoma: TextView = itemView.findViewById(R.id.tvSintoma)
        private val tvTurno: TextView = itemView.findViewById(R.id.tvTurno)

        // [Llenar vistas con datos] (Muestra los datos de la cita y carga imagen desde API)
        fun bind(cita: Cita) {
            tvNombreMascota.text = cita.nombreMascota
            tvSintoma.text = cita.sintomas ?: "No especificado"
            tvTurno.text = "#${cita.id}"

            val razaApi = normalizarRazaParaApi(cita.raza)

            // [Llamada a la API] (Solicita una imagen aleatoria de la raza usando Retrofit)
            val apiService = RetrofitRazas.instance.create(DogApiService::class.java)
            val call = apiService.obtenerImagenPorRaza(razaApi)

            // [Respuesta de la API] (Carga la imagen con Glide o usa un ícono por defecto)
            call.enqueue(object : Callback<ImagenRazaResponse> {
                override fun onResponse(
                    call: Call<ImagenRazaResponse>,
                    response: Response<ImagenRazaResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        val url = response.body()?.message
                        Glide.with(itemView.context)
                            .load(url)
                            .placeholder(R.drawable.ic_dog)
                            .error(R.drawable.ic_dog)
                            .into(imgMascota)
                    } else {
                        Glide.with(itemView.context).load(R.drawable.ic_dog).into(imgMascota)
                    }
                }

                override fun onFailure(call: Call<ImagenRazaResponse>, t: Throwable) {
                    Glide.with(itemView.context).load(R.drawable.ic_dog).into(imgMascota)
                }
            })

            // [Evento de clic en item] (Ejecuta la acción definida al hacer clic en una cita)
            itemView.setOnClickListener {
                onItemClick(cita)
            }
        }

        // [Normalizar nombre de raza] (Elimina tildes y caracteres especiales para que funcione con la API)
        private fun normalizarRazaParaApi(raza: String): String {
            return raza.lowercase()
                .replace("[áàäâ]".toRegex(), "a")
                .replace("[éèëê]".toRegex(), "e")
                .replace("[íìïî]".toRegex(), "i")
                .replace("[óòöô]".toRegex(), "o")
                .replace("[úùüû]".toRegex(), "u")
                .replace("[^a-z/]".toRegex(), "")
                .replace(" ", "")
        }
    }

    // [Actualizar lista de citas] (Reemplaza los datos antiguos por nuevos y refresca la vista)
    fun updateList(newList: List<Cita>) {
        citas = newList
        notifyDataSetChanged()
    }
}
