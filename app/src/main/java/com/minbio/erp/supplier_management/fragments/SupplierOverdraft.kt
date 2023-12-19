package com.minbio.erp.supplier_management.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.minbio.erp.R
import com.minbio.erp.utils.AppUtils
import kotlinx.android.synthetic.main.fragment_supplier_overdraft.*

class SupplierOverdraft : Fragment() {

    private lateinit var v: View
    private lateinit var btnSave: LinearLayout
    private lateinit var assuranceScoringSpinner: Spinner
    private lateinit var profileStatusSpinner: Spinner
    private lateinit var payMethodSpinner: Spinner
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_supplier_overdraft, container, false)

        initViews()

        return v
    }

    private fun initViews() {
        btnSave = v.findViewById(R.id.btn_sov_save)
        btnSave.setOnClickListener { validate() }

        //assuranceScoring Spinner
        assuranceScoringSpinner = v.findViewById(R.id.spinner_sov_assurance_scoring)
        val assuranceScoringItems = arrayOf("lorem", "ipsum", "Test")
        val assuranceScoringAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, assuranceScoringItems)
        assuranceScoringSpinner.adapter = assuranceScoringAdapter


        //profileStatus Spinner
        profileStatusSpinner = v.findViewById(R.id.spinner_sov_profile_status)
        val profileStatusItems = arrayOf("lorem", "ipsum", "Test")
        val profileStatusAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, profileStatusItems)
        profileStatusSpinner.adapter = profileStatusAdapter

        //payMethod Spinner
        payMethodSpinner = v.findViewById(R.id.spinner_sov_payment_method)
        val payMethodItems = arrayOf("lorem", "ipsum", "Test")
        val payMethodAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, payMethodItems)
        payMethodSpinner.adapter = payMethodAdapter

    }

    private fun validate() {
        if (et_sov_overdraft_allow.text.toString().isBlank()) {
            error_sov_overdraft_allow.visibility = View.VISIBLE
            error_sov_overdraft_allow.setText(R.string.sovErrorOverdraftAllow);
            AppUtils.setBackground(et_sov_overdraft_allow, R.drawable.input_border_bottom_red)
            et_sov_overdraft_allow.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_sov_overdraft_allow, R.drawable.input_border_bottom)
            error_sov_overdraft_allow.visibility = View.INVISIBLE
        }

        if (et_sov_total_overdue.text.toString().isBlank()) {
            error_sov_total_overdue.visibility = View.VISIBLE
            error_sov_total_overdue.setText(R.string.sovErrorTotalOverdue);
            AppUtils.setBackground(et_sov_total_overdue, R.drawable.input_border_bottom_red)
            et_sov_total_overdue.requestFocus()
            return
        } else {
            AppUtils.setBackground(et_sov_total_overdue, R.drawable.input_border_bottom)
            error_sov_total_overdue.visibility = View.INVISIBLE
        }

    }
    

}