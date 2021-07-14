package ru.vlasov.vkcupapp.features

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.vlasov.vkcupapp.databinding.FragmentNetworkErrorBinding
import ru.vlasov.vkcupapp.features.news.FragmentUnauthenticated

class FragmentNetworkError() : Fragment() {

    private var networkButtonListener : NetworkButtonListener? = null

    companion object{
        fun newInstance(networkButtonListener: NetworkButtonListener): FragmentNetworkError {
            return FragmentNetworkError(networkButtonListener)
        }
    }

    private constructor(networkButtonListener: NetworkButtonListener) : this() {
        this.networkButtonListener = networkButtonListener
    }

    interface NetworkButtonListener{
        fun onNetworkButtonClick()
    }

    var binding : FragmentNetworkErrorBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNetworkErrorBinding.inflate(inflater)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.buttonTryAgain?.setOnClickListener{
            networkButtonListener?.onNetworkButtonClick()
        }
        super.onViewCreated(view, savedInstanceState)
    }
}