package ru.vlasov.vkcupapp.features

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.vlasov.vkcupapp.databinding.FragmentNewsBinding
import ru.vlasov.vkcupapp.databinding.FragmentTaxiBinding

class FragmentTaxi  : Fragment() {

    var binding : FragmentTaxiBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaxiBinding.inflate(inflater)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}