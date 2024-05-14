package unpsjb.ing.tntpm2024.detalle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import unpsjb.ing.tntpm2024.R

class DetailFragment : Fragment() {

    val args:DetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvDetailTitle = view.findViewById<TextView>(R.id.detailTitle)
        val tvDetailDesc = view.findViewById<TextView>(R.id.detailDesc)

        //val getData = intent.getParcelableExtra<DataClass>("android")
        //if (getData != null) {
            //val detailTitle: TextView = findViewById(R.id.detailTitle)
            //val detailDesc: TextView = findViewById(R.id.detailDesc)
            //val detailImage: ImageView = findViewById(R.id.detailImage)

            tvDetailTitle.text = args.title
            tvDetailDesc.text = args.desc
            //detailImage.setImageResource(getData.dataDetailImage)


        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

}