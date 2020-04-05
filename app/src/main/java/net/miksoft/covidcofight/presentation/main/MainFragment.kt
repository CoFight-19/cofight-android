package net.miksoft.covidcofight.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_main.*
import net.miksoft.covidcofight.R
import net.miksoft.covidcofight.domain.EntriesController
import net.miksoft.covidcofight.presentation.common.showConfirmationDialog
import net.miksoft.covidcofight.presentation.common.showGenericErrorToast
import net.miksoft.covidcofight.presentation.common.showInfoDialog


class MainFragment : Fragment() {

    private val model by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpViewModelObservers()
        setUpViewListeners()
        setUpDebug()
    }

    private fun setUpView() {
        rootRefreshView.isEnabled = false
    }

    private fun setUpViewListeners() {
        scanningStatusView.setOnClickListener {
            model.onScanningStatusClicked()
        }
        uploadButtonView.setOnClickListener {
            showUploadDataConfirmation()
        }
        settingButtonView.setOnClickListener {
            val options =
                arrayOf(
                    getString(R.string.share_app),
                    getString(R.string.upload_data),
                    getString(R.string.log_out_delete)
                )
            val builder =
                AlertDialog.Builder(context!!)
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> shareApp()
                    1 -> showUploadDataConfirmation()
                    2 -> showDeleteLogOutConfirmation()
                }
            }
            builder.show()
        }
        shareButtonView.setOnClickListener {
            shareApp()
        }
    }

    private fun setUpViewModelObservers() {
        model.isEnabled.observe(viewLifecycleOwner, Observer {
            statusLabelView.text =
                getString(if (it) R.string.scanning_on else R.string.scanning_off)
            statusLabelView.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    if (it) R.color.green else R.color.red
                )
            )
            statusIconView.setImageDrawable(
                ContextCompat.getDrawable(
                    context!!,
                    if (it) R.drawable.scanning_on else R.drawable.scanning_off
                )
            )
        })
        model.isLoading.observe(viewLifecycleOwner, Observer {
            rootRefreshView.isRefreshing = it
            uploadButtonView.isEnabled = !it
            settingButtonView.isEnabled = !it
        })
        model.genericError.observe(
            viewLifecycleOwner,
            Observer { context?.showGenericErrorToast() })
        model.navigation.observe(
            viewLifecycleOwner,
            Observer { view?.findNavController()?.navigate(it) })
        model.infoDialog.observe(
            viewLifecycleOwner,
            Observer { context?.showInfoDialog(it) }
        )
    }

    private fun showUploadDataConfirmation() {
        context?.showConfirmationDialog(
            titleResId = R.string.upload_data,
            messageResId = R.string.upload_confirmation,
            action = { model.onUploadDataClicked() }
        )
    }

    private fun showDeleteLogOutConfirmation() {
        context?.showConfirmationDialog(
            titleResId = R.string.log_out_delete,
            messageResId = R.string.log_out_delete_confirmation,
            action = { model.onLogOutDeleteClicked() }
        )
    }

    private fun shareApp() {
        ShareCompat.IntentBuilder.from(activity!!)
            .setType("text/plain")
            .setText("http://play.google.com/store/apps/details?id=" + activity!!.packageName)
            .startChooser();
    }

    private fun setUpDebug() {
        debugButtonView.setOnClickListener {
            val entries = EntriesController.logEntries()
                .map { it.toString() }
                .toTypedArray()
            AlertDialog.Builder(context!!)
                .setTitle("Log entries (${entries.size})")
                .setItems(entries) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}
