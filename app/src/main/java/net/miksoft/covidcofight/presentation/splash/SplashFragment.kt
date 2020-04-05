package net.miksoft.covidcofight.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.miksoft.covidcofight.R
import net.miksoft.covidcofight.domain.UserController


class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            view?.findNavController()?.navigate(
                if (UserController.getLoggedInUser() != null)
                    SplashFragmentDirections.actionSplashFragmentToMainFragment()
                else
                    SplashFragmentDirections.actionSplashFragmentToIntroFragment()

            )
        }
    }
}
