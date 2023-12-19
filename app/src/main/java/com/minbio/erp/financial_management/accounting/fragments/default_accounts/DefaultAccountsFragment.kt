package com.minbio.erp.financial_management.accounting.fragments.default_accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.financial_management.model.ParentAccountsData
import com.minbio.erp.financial_management.model.ParentAccountsModel
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import org.json.JSONObject

class DefaultAccountsFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View
    private lateinit var api: Api

    private lateinit var btnModify: LinearLayout
    private lateinit var spinner1: CustomSearchableSpinner
    private lateinit var spinner2: CustomSearchableSpinner
    private lateinit var spinner3: CustomSearchableSpinner
    private lateinit var spinner4: CustomSearchableSpinner
    private lateinit var spinner5: CustomSearchableSpinner
    private lateinit var spinner6: CustomSearchableSpinner
    private lateinit var spinner7: CustomSearchableSpinner
    private lateinit var spinner8: CustomSearchableSpinner
    private lateinit var spinner9: CustomSearchableSpinner
    private lateinit var spinner10: CustomSearchableSpinner
    private lateinit var spinner11: CustomSearchableSpinner
    private lateinit var spinner12: CustomSearchableSpinner
    private lateinit var spinner13: CustomSearchableSpinner
    private lateinit var spinner14: CustomSearchableSpinner
    private lateinit var spinner15: CustomSearchableSpinner
    private lateinit var spinner16: CustomSearchableSpinner
    private lateinit var spinner17: CustomSearchableSpinner
    private lateinit var spinner18: CustomSearchableSpinner
    private lateinit var spinner19: CustomSearchableSpinner
    private lateinit var spinner20: CustomSearchableSpinner

    var allAccountList: MutableList<ParentAccountsData?> = ArrayList()
    var defaultAccountsData: DefaultAccountData? = null

    private var account_for_customer_third_parties: Int = 0
    private var account_for_vendor_third_parties: Int = 0
    private var account_for_user_third_parties: Int = 0
    private var account_for_sold_products: Int = 0
    private var account_for_products_sold_and_exported_out_of_eec: Int = 0
    private var account_for_bought_products: Int = 0
    private var account_for_bought_products_and_imported_out_of_ecc: Int = 0
    private var account_for_sold_services: Int = 0
    private var account_for_services_sold_and_exported_out_of_ecc: Int = 0
    private var account_for_bought_services: Int = 0
    private var account_for_bought_services_and_imported_out_of_ecc: Int = 0
    private var account_for_vat_on_purchases: Int = 0
    private var account_for_vat_on_sales: Int = 0
    private var account_for_paying_vat: Int = 0
    private var account_of_wait: Int = 0
    private var account_of_transitional_bank_transfer: Int = 0
    private var account_to_register_donations: Int = 0
    private var account_capital: Int = 0
    private var account_interest: Int = 0
    private var account_insurance: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(
            R.layout.fragment_financial_accounting_default_accounts,
            container,
            false
        )
        api = RetrofitClient.getClient(context!!).create(Api::class.java)


        initViews()

        getAllAccounts()

        return v
    }

    private fun getAllAccounts() {
        AppUtils.showDialog(context!!)
        val call = api.getParentAccountList()
        RetrofitClient.apiCall(call, this, "AllAccounts")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        when (tag) {

            "AllAccounts" -> {
                val model = Gson().fromJson(jsonObject.toString(), ParentAccountsModel::class.java)
                allAccountList.add(
                    ParentAccountsData(
                        "",
                        0,
                        ""
                    )
                )
                allAccountList.addAll(model.data)

                setUpSpinner1()
                setUpSpinner2()
                setUpSpinner3()
                setUpSpinner4()
                setUpSpinner5()
                setUpSpinner6()
                setUpSpinner7()
                setUpSpinner8()
                setUpSpinner9()
                setUpSpinner10()
                setUpSpinner11()
                setUpSpinner12()
                setUpSpinner13()
                setUpSpinner14()
                setUpSpinner15()
                setUpSpinner16()
                setUpSpinner17()
                setUpSpinner18()
                setUpSpinner19()
                setUpSpinner20()


                val call = api.getDefaultAccounts()
                RetrofitClient.apiCall(call, this, "GetDefaultAccounts")
            }
            "GetDefaultAccounts" -> {
                AppUtils.dismissDialog()

                val model = Gson().fromJson(jsonObject.toString(), DefaultAccountModel::class.java)
                defaultAccountsData = model.data

                updateSpinners()
            }
            "UpdateDefaultAccounts" -> {
                AppUtils.dismissDialog()
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)
            }
        }
    }

    private fun updateSpinners() {

        for (i in allAccountList.indices) {

            if (allAccountList[i]?.id == defaultAccountsData?.account_for_customer_third_parties)
                spinner1.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_for_vendor_third_parties)
                spinner2.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_for_user_third_parties)
                spinner3.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_for_sold_products)
                spinner4.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_for_products_sold_and_exported_out_of_eec)
                spinner5.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_for_bought_products)
                spinner6.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_for_bought_products_and_imported_out_of_ecc)
                spinner7.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_for_sold_services)
                spinner8.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_for_services_sold_and_exported_out_of_ecc)
                spinner9.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_for_bought_services)
                spinner10.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_for_bought_services_and_imported_out_of_ecc)
                spinner11.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_for_vat_on_purchases)
                spinner12.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_for_vat_on_sales)
                spinner13.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_for_paying_vat)
                spinner14.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_of_wait)
                spinner15.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_of_transitional_bank_transfer)
                spinner16.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_to_register_donations)
                spinner17.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_capital)
                spinner18.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_interest)
                spinner19.setSelection(i)

            if (allAccountList[i]?.id == defaultAccountsData?.account_insurance)
                spinner20.setSelection(i)

        }


    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, false)
    }

    private fun initViews() {
        btnModify = v.findViewById(R.id.btnModify)
        btnModify.setOnClickListener {
            AppUtils.showDialog(context!!)
            val call = api.updateDefaultAccounts(
                account_for_customer_third_parties
                , account_for_vendor_third_parties
                , account_for_user_third_parties
                , account_for_sold_products
                , account_for_products_sold_and_exported_out_of_eec
                , account_for_bought_products
                , account_for_bought_products_and_imported_out_of_ecc
                , account_for_sold_services
                , account_for_services_sold_and_exported_out_of_ecc
                , account_for_bought_services
                , account_for_bought_services_and_imported_out_of_ecc
                , account_for_vat_on_purchases
                , account_for_vat_on_sales
                , account_for_paying_vat
                , account_of_wait
                , account_of_transitional_bank_transfer
                , account_to_register_donations
                , account_capital
                , account_interest
                , account_insurance
            )
            RetrofitClient.apiCall(call, this, "UpdateDefaultAccounts")
        }

        spinner1 = v.findViewById(R.id.spinner1)
        spinner2 = v.findViewById(R.id.spinner2)
        spinner3 = v.findViewById(R.id.spinner3)
        spinner4 = v.findViewById(R.id.spinner4)
        spinner5 = v.findViewById(R.id.spinner5)
        spinner6 = v.findViewById(R.id.spinner6)
        spinner7 = v.findViewById(R.id.spinner7)
        spinner8 = v.findViewById(R.id.spinner8)
        spinner9 = v.findViewById(R.id.spinner9)
        spinner10 = v.findViewById(R.id.spinner10)
        spinner11 = v.findViewById(R.id.spinner11)
        spinner12 = v.findViewById(R.id.spinner12)
        spinner13 = v.findViewById(R.id.spinner13)
        spinner14 = v.findViewById(R.id.spinner14)
        spinner15 = v.findViewById(R.id.spinner15)
        spinner16 = v.findViewById(R.id.spinner16)
        spinner17 = v.findViewById(R.id.spinner17)
        spinner18 = v.findViewById(R.id.spinner18)
        spinner19 = v.findViewById(R.id.spinner19)
        spinner20 = v.findViewById(R.id.spinner20)

    }

    private fun setUpSpinner1() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner1.adapter = positionAdapter
        spinner1.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner1.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner1.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_for_customer_third_parties = allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner2() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner2.adapter = positionAdapter
        spinner2.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner2.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner2.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_for_vendor_third_parties = allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner3() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner3.adapter = positionAdapter
        spinner3.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner3.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner3.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_for_user_third_parties = allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner4() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner4.adapter = positionAdapter
        spinner4.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner4.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner4.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_for_sold_products = allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner5() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner5.adapter = positionAdapter
        spinner5.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner5.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner5.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_for_products_sold_and_exported_out_of_eec =
                        allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner6() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner6.adapter = positionAdapter
        spinner6.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner6.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner6.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_for_bought_products = allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner7() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner7.adapter = positionAdapter
        spinner7.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner7.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner7.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_for_bought_products_and_imported_out_of_ecc =
                        allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner8() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner8.adapter = positionAdapter
        spinner8.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner8.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner8.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_for_sold_services = allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner9() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner9.adapter = positionAdapter
        spinner9.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner9.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner9.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_for_services_sold_and_exported_out_of_ecc =
                        allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner10() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner10.adapter = positionAdapter
        spinner10.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner10.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner10.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_for_bought_services = allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner11() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner11.adapter = positionAdapter
        spinner11.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner11.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner11.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_for_bought_services_and_imported_out_of_ecc =
                        allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner12() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner12.adapter = positionAdapter
        spinner12.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner12.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner12.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_for_vat_on_purchases = allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner13() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner13.adapter = positionAdapter
        spinner13.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner13.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner13.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_for_vat_on_sales = allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner14() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner14.adapter = positionAdapter
        spinner14.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner14.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner14.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_for_paying_vat = allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner15() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner15.adapter = positionAdapter
        spinner15.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner15.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner15.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_of_wait = allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner16() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner16.adapter = positionAdapter
        spinner16.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner16.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner16.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_of_transitional_bank_transfer = allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner17() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner17.adapter = positionAdapter
        spinner17.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner17.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner17.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_to_register_donations = allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner18() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner18.adapter = positionAdapter
        spinner18.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner18.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner18.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_capital = allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner19() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner19.adapter = positionAdapter
        spinner19.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner19.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner19.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_interest = allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }

    private fun setUpSpinner20() {
        val strings = ArrayList<String>()
        for (i in allAccountList.indices) {
            if (i > 0)
                strings.add(allAccountList[i]?.account_number!! + " - " + allAccountList[i]?.label)
            else
                strings.add(allAccountList[i]?.account_number!! + allAccountList[i]?.label)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinner20.adapter = positionAdapter
        spinner20.setPositiveButton(context!!.resources.getString(R.string.spinnerBtnClose))
        spinner20.setTitle(context!!.resources.getString(R.string.parentAccountSpinnerTitle))
        spinner20.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    account_insurance = allAccountList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }


}