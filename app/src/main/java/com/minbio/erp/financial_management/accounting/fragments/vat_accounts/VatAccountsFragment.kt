package com.minbio.erp.financial_management.accounting.fragments.vat_accounts

import android.os.Bundle
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
import com.minbio.erp.financial_management.accounting.fragments.vat_accounts.models.VatAccountsData
import com.minbio.erp.financial_management.accounting.fragments.vat_accounts.models.VatAccountsModel
import com.minbio.erp.main.MainActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import kotlinx.android.synthetic.main.item_sales_order.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class VatAccountsFragment : Fragment(), ResponseCallBack {

    private lateinit var api: Api
    private lateinit var v: View
    private lateinit var pageTile: TextView
    private lateinit var addVatAccount: ImageView
    private lateinit var accountingVatAccountAdapter: VatAccountAdapter
    private lateinit var favaRecyclerView: RecyclerView
    private lateinit var spinnerCountry: CustomSearchableSpinner
    private lateinit var search: ImageView

    private var LAST_PAGE: Int = 1
    private var CURRENT_PAGE: Int = 1
    private var isLoading: Boolean = false

    private var vatAccountData: MutableList<VatAccountsData?> = ArrayList()

    private var countryId: Int = 0
    private lateinit var codeSearchValue: EditText

    private var countriesList: MutableList<CountriesData?> = ArrayList()
    private lateinit var countriesModel: CountriesModel
    private var itemPosition = -1
    private lateinit var tvNoData: TextView
    private lateinit var pullToRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_accounting_vat_accounts, container, false)
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()
        setUpAdapter()
        initScrollListener()
        setUpCountrySpinner()

        vatAccountData.clear()
        AppUtils.showDialog(context!!)
        getVatAccounts()

        return v
    }

    private fun initViews() {
        tvNoData = v.findViewById(R.id.tvNoData)
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            CURRENT_PAGE = 1
            LAST_PAGE = 1

            vatAccountData.clear()
            getVatAccounts()
        }
        favaRecyclerView = v.findViewById(R.id.favaRecyclerView)
        codeSearchValue = v.findViewById(R.id.codeSearchValue)
        addVatAccount = v.findViewById(R.id.addVatAccount)
        addVatAccount.setOnClickListener {
            (parentFragment as FinancialManagementFragment).launchRightFragment(
                AddVatAccountFragment()
            )
        }
        spinnerCountry = v.findViewById(R.id.spinnerCountry)
        pageTile = v.findViewById(R.id.article_code_number)
        pageTile.text = context!!.resources.getString(R.string.favaPageTitle)

        search = v.findViewById(R.id.search)
        search.setOnClickListener {
            CURRENT_PAGE = 1
            LAST_PAGE = 1

            vatAccountData.clear()
            AppUtils.showDialog(context!!)
            getVatAccounts()
        }


    }

    private fun setUpAdapter() {
        accountingVatAccountAdapter = VatAccountAdapter(this, vatAccountData)
        favaRecyclerView.layoutManager = LinearLayoutManager(context)
        favaRecyclerView.adapter = accountingVatAccountAdapter
    }

    private fun initScrollListener() {
        favaRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == vatAccountData.size - 1) {
                        recyclerView.post { loadMore() }
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        try {
            if (CURRENT_PAGE < LAST_PAGE) {
                vatAccountData.add(null)
                accountingVatAccountAdapter.notifyItemInserted(vatAccountData.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        CURRENT_PAGE++
                        getVatAccounts()
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    private fun getVatAccounts() {
        val call = api.getVatAccountsList(countryId, codeSearchValue.text.toString(), CURRENT_PAGE)
        RetrofitClient.apiCall(call, this, "GetVatAccounts")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "GetVatAccounts" -> {
                val model =
                    Gson().fromJson(jsonObject.toString(), VatAccountsModel::class.java)

                if (vatAccountData.size > 0) {
                    vatAccountData.removeAt(vatAccountData.size - 1)
                    accountingVatAccountAdapter.notifyItemRemoved(vatAccountData.size)
                }

                CURRENT_PAGE = model.meta.current_page
                LAST_PAGE = model.meta.last_page

                vatAccountData.addAll(model.data)

                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false

                if (vatAccountData.isNullOrEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    favaRecyclerView.visibility = View.GONE
                } else {
                    tvNoData.visibility = View.GONE
                    favaRecyclerView.visibility = View.VISIBLE
                }

                accountingVatAccountAdapter.notifyDataSetChanged()
                isLoading = false
            }
            "DeleteVatAccount" -> {
                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)
                vatAccountData.removeAt(itemPosition)
                accountingVatAccountAdapter.notifyDataSetChanged()
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

    fun deleteVatAccount(id: Int, position: Int) {
        itemPosition = position

        AppUtils.showDialog(context!!)
        val call = api.deleteVatAccount(id)
        RetrofitClient.apiCall(call, this, "DeleteVatAccount")
    }

    fun updateStatus(id: Int, position: Int) {
        itemPosition = position

        val call = api.updateVatAccountStatus(id, vatAccountData[position]?.status!!)
        RetrofitClient.apiCall(call, this, "UpdateStatus")
    }


}