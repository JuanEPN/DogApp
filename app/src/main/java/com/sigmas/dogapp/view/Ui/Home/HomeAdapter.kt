package com.sigmas.dogapp.view.Ui.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sigmas.dogapp.R
import com.sigmas.dogapp.view.Data.Model.Cita
import de.hdodenhof.circleimageview.CircleImageView
import android.widget.TextView

class HomeAdapter(
    private var citas: List<Cita>,
    private val onItemClick: (Cita) -> Unit
) : RecyclerView.Adapter<HomeAdapter.CitaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cita, parent, false)
        return CitaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.bind(citas[position])
    }

    override fun getItemCount(): Int = citas.size

    inner class CitaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgMascota: CircleImageView = itemView.findViewById(R.id.imgMascota)
        private val tvNombreMascota: TextView = itemView.findViewById(R.id.tvNombreMascota)
        private val tvSintoma: TextView = itemView.findViewById(R.id.tvSintoma)
        private val tvTurno: TextView = itemView.findViewById(R.id.tvTurno)

        fun bind(cita: Cita) {
            // Aquí llenamos los datos
            tvNombreMascota.text = cita.nombreMascota
            tvSintoma.text = cita.sintoma
            tvTurno.text = "#${cita.turno}"

            // Opcionalmente cargar imagen (después usando Glide o Picasso)
            imgMascota.setImageResource(R.drawable.lg_dog)

            itemView.setOnClickListener {
                onItemClick(cita)
            }
        }
    }

    fun updateList(newList: List<Cita>) {
        citas = newList
        notifyDataSetChanged()
    }
}