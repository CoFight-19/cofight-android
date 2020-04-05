package net.miksoft.covidcofight.presentation.verify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_verify.*
import net.miksoft.covidcofight.R
import net.miksoft.covidcofight.presentation.common.showGenericErrorToast
import net.miksoft.covidcofight.presentation.verify.VerifyFragmentArgs

class VerifyFragment : Fragment() {

    private val args: VerifyFragmentArgs by navArgs()
    private val model by viewModels<VerifyViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_verify, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpModelObservers()
        setUpViewListeners()
    }

    private fun setUpView() {
        rootRefreshView.isEnabled = false
        verifyExplainView.text = getString(R.string.verify_explain) + " ${args.telephone}"
    }

    private fun setUpModelObservers() {
        model.isLoading.observe(viewLifecycleOwner, Observer {
            rootRefreshView.isRefreshing = it
            codeView.isEnabled = !it
        })
        model.navigation.observe(viewLifecycleOwner, Observer { view?.findNavController()?.navigate(it) })
        model.genericError.observe(viewLifecycleOwner, Observer { context?.showGenericErrorToast() })
    }

    private fun setUpViewListeners() {
        codeView.addTextChangedListener {
            if (it.toString().length != 4) return@addTextChangedListener
            model.onCodeEntered(args.telephone, it.toString())
        }
    }
}
