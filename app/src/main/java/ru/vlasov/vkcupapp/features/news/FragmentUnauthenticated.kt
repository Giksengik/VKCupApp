package ru.vlasov.vkcupapp.features.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.vlasov.vkcupapp.databinding.FragmentUnauthorizedBinding

class FragmentUnauthenticated() : Fragment() {

    private var authButtonListener : AuthButtonListener? = null

    companion object{
        fun newInstance(authButtonListener: AuthButtonListener): FragmentUnauthenticated{
            return FragmentUnauthenticated(authButtonListener)
        }
    }

    private constructor(authButtonListener: AuthButtonListener) : this() {
        this.authButtonListener = authButtonListener
    }

    interface AuthButtonListener {
        fun onAuthButtonClick()
    }

    var binding : FragmentUnauthorizedBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUnauthorizedBinding.inflate(inflater)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authButtonListener?.let{
            binding?.buttonAuth?.setOnClickListener {
                authButtonListener?.onAuthButtonClick()
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }
}