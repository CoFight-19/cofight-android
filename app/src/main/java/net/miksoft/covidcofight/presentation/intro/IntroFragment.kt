package net.miksoft.covidcofight.presentation.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_intro.*
import net.miksoft.covidcofight.R
import net.miksoft.covidcofight.presentation.intro.IntroFragmentDirections

class IntroFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nextButtonView.setOnClickListener {
            view.findNavController().navigate(
                IntroFragmentDirections.actionIntroFragmentToIntroMessageFragment()
            )
        }
    }
}
