package com.minbio.erp.financial_management.accounting.fragments.tax_accounts

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.auth.models.CountriesData
import com.minbio.erp.auth.models.CountriesModel
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.accounting.fragments.tax_accounts.models.TaxAccountsData
import com.minbio.erp.financial_management.accounting.fragments.tax_accounts.models.TaxAccountsModel
import com.minbio.erp.main.MainActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import org.json.JSONObject
import java.util.*

class TaxAccountFragment : Fragment(), ResponseCallBack {

    private var LAST_PAGE: Int = 1
    private var CURRENT_PAGE: Int = 1

    private lateinit var v: View
    private lateinit var pageTitle: TextView
    private lateinit var addTaxAccount: ImageView
    private lateinit var taxAccountAdapter: TaxAccountAdapter
    private lateinit var fataRecyclerView: RecyclerView
    private lateinit var spinnerCountry: CustomSearchableSpinner
    private lateinit var etCodeSearch: EditText
    private lateinit var tvNoData: TextView
    private lateinit var search: ImageView
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var api: Api

    private var countriesList: MutableList<CountriesData?> = ArrayList()
    private lateinit var countriesModel: CountriesModel
    private var countryId = 0

    private var taxAccountsData: MutableList<TaxAccountsData?> = ArrayList()
    private var itemPosition = -1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_accounting_tax_accounts, container, false)
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()
        setUpAdapter()
        initScrollListener()
        setUpCountrySpinner()

        taxAccountsData.clear()
        AppUtils.showDialog(context!!)
        getTaxAccounts()

        return v
    }

    private fun initViews() {
        pageTitle = v.findViewById(R.id.article_code_number)
        spinnerCountry = v.findViewById(R.id.spinnerCountry)
        etCodeSearch = v.findViewById(R.id.etCodeSearch)
        tvNoData = v.findViewById(R.id.tvNoData)
        search = v.findViewById(R.id.search)
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        addTaxAccount = v.findViewById(R.id.addTaxAccount)
        fataRecyclerView = v.findViewById(R.id.fataRecyclerView)


        pullToRefresh.setOnRefreshListener {
            CURRENT_PAGE = 1
            LAST_PAGE = 1

            taxAccountsData.clear()
            getTaxAccounts()
        }

        addTaxAccount.setOnClickListener {
            (parentFragment as FinancialManagementFragment).launchRightFragment(
                AddTaxAccountFragment()
            )
        }

        search.setOnClickListener {
            CURRENT_PAGE = 1
            LAST_PAGE = 1

            taxAccountsData.clear()
            AppUtils.showDialog(context!!)
            getTaxAccounts()
        }
    }

    private fun setUpAdapter() {
        taxAccountAdapter = TaxAccountAdapter(taxAccountsData, this)
        fataRecyclerView.layoutManager = LinearLayoutManager(context!!)
        fataRecyclerView.adapter = taxAccountAdapter
    }

    private fun initScrollListener() {
        fataRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == taxAccountsData.size - 1) {
                    recyclerView.post {
                        try {
                            if (CURRENT_PAGE < LAST_PAGE) {
                                Handler().postDelayed({
                                    CURRENT_PAGE++
                                    getTaxAccounts()
                                }, 1000)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }
            }
        })
    }

    private fun setUpCountrySpinner() {
        countriesList.clear()
        countriesModel = (activity as MainActivity).countriesList()

        countriesList.add(CountriesData(0, "", "", ""))
        countriesList.addAll(countriesModel.data)

        val strings = ArrayList<String>()
        for (i in countriesList) {
            strings.add(i!!.name)
        }

        val positionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, strings)
        spinnerCountry.adapter = positionAdapter
        spinnerCountry.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        spinnerCountry.setTitle(resources.getString(R.string.countriesSpinnerTitle))
        spinnerCountry.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    countryId = countriesList[position]?.id!!
                    AppUtils.hideKeyboard(activity!!)
                }
            }
    }


    private fun getTaxAccounts() {
        val call = RetrofitClient.getClient(context!!).create(Api::class.java)
            .getTaxAccountsList(countryId, etCodeSearch.text.toString(), CURRENT_PAGE)
        RetrofitClient.apiCall(call, this, "GetVatAccounts")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "GetVatAccounts" -> {
                val model =
                    Gson().fromJson(jsonObject.toString(), TaxAccountsModel::class.java)

                if (taxAccountsData.size > 0) {
                    taxAccountsData.removeAt(taxAccountsData.size - 1)
                    taxAccountAdapter.notifyItemRemoved(taxAccountsData.size)
                }

                CURRENT_PAGE = model.meta.current_page
                LAST_PAGE = model.meta.last_page

                taxAccountsData.addAll(model.data)

                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false

                if (taxAccountsData.isNullOrEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    fataRecyclerView.visibility = View.GONE
                } else {
                    tvNoData.visibility = View.GONE
                    fataRecyclerView.visibility = View.VISIBLE
                }

                if (CURRENT_PAGE < LAST_PAGE)
                    taxAccountsData.add(null)

                taxAccountAdapter.notifyDataSetChanged()
            }
            "DeleteTaxAccount" -> {
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)
                taxAccountsData.removeAt(itemPosition)
                taxAccountAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        pullToRefresh.isRefreshing = false
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        pullToRefresh.isRefreshing = false
        AppUtils.showToast(activity, message!!, false)
    }

    fun launchEditFragment(fragment: Fragment) {
        (parentFragment as FinancialManagementFragment).launchRightFragment(fragment)
    }

    fun deleteTaxAccount(id: Int, position: Int) {
        itemPosition = position

        AppUtils.showDialog(context!!)
        val call = api.deleteTaxAccount(id)
        RetrofitClient.apiCall(call, this, "DeleteTaxAccount")
    }

    fun updateStatus(id: Int, position: Int) {
        itemPosition = position
        val call = api.updateTaxAccountStatus(id, taxAccountsData[position]?.status!!)
        RetrofitClient.apiCall(call, this, "UpdateStatus")
    }


}