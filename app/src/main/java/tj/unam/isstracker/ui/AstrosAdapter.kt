package tj.unam.isstracker.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_astros.view.*
import tj.unam.isstracker.R
import tj.unam.isstracker.data.model.Person

class AstrosAdapter(private val mAstrosList: MutableList<Person>, private val context: Context) :
    RecyclerView.Adapter<AstrosAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_astros, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int = mAstrosList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //which called each time rows refreshed with the data object
        val mPeople = mAstrosList[position]
        holder.itemView.astroTv.text = mPeople.name
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}