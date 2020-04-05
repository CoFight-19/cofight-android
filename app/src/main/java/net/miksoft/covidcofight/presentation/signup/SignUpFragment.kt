package net.miksoft.covidcofight.presentation.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_sign_up.*
import net.miksoft.covidcofight.R
import net.miksoft.covidcofight.domain.ConfigurationController
import net.miksoft.covidcofight.presentation.common.showGenericErrorToast


class SignUpFragment : Fragment() {

    private val model by viewModels<SignUpViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpModelObservers()
        setUpViewListeners()
    }

    private fun setUpView() {
        rootRefreshView.isEnabled = false
        buttonGroup.visibility = View.GONE
        telephonePrefixView.text = ConfigurationController.TELEPHONE_COUNTRY_CODE
        telephoneView.requestFocus()
    }

    private fun setUpModelObservers() {
        model.isLoading.observe(viewLifecycleOwner, Observer {
            rootRefreshView.isRefreshing = it
            telephoneView.isEnabled = !it
            doneButtonView.isEnabled = !it
        })
        model.navigation.observe(
            viewLifecycleOwner,
            Observer { view?.findNavController()?.navigate(it) })
        model.genericError.observe(
            viewLifecycleOwner,
            Observer { context?.showGenericErrorToast() })
    }

    private fun setUpViewListeners() {
        telephoneView.addTextChangedListener {
            buttonGroup.visibility = if (isValidNumber(it.toString())) View.VISIBLE else View.GONE
        }
        doneButtonView.setOnClickListener {
            model.onDoneClicked(telephoneView.text.toString())
        }
    }

    private fun isValidNumber(numberText: String): Boolean =
        numberText.count() == ConfigurationController.TELEPHONE_LENGTH && numberText.startsWith(ConfigurationController.TELEPHONE_PREFIX)
}
