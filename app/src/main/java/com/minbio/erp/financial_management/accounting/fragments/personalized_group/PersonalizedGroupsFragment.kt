package com.minbio.erp.financial_management.accounting.fragments.personalized_group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.minbio.erp.R
import com.minbio.erp.auth.models.CountriesData
import com.minbio.erp.auth.models.CountriesModel
import com.minbio.erp.financial_management.FinancialManagementFragment
import com.minbio.erp.financial_management.accounting.fragments.personalized_group.models.PersonalizedGroupData
import com.minbio.erp.financial_management.accounting.fragments.personalized_group.models.PersonalizedGroupModel
import com.minbio.erp.main.MainActivity
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class PersonalizedGroupsFragment : Fragment(), ResponseCallBack {

    private var countryId: Int = 0
    private lateinit var api: Api
    private lateinit var v: View
    private lateinit var addPersonalizedGroup: ImageView
    private lateinit var personalizedGroupAdapter: PersonalizedGroupAdapter
    private lateinit var fapgRecyclerView: RecyclerView
    private lateinit var pageTitle: TextView
    private lateinit var spinnerPersonalizedGroupCountry: CustomSearchableSpinner
    private lateinit var search: ImageView

    private var LAST_PAGE: Int = 1
    private var CURRENT_PAGE: Int = 1
    private var isLoading: Boolean = false

    private var itemPosition = -1

    private var personalizedGroupData: MutableList<PersonalizedGroupData?> = ArrayList()
    private var countriesList: MutableList<CountriesData?> = ArrayList()
    private lateinit var countriesModel: CountriesModel
    private lateinit var tvNoData: TextView
    private lateinit var pullToRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_financial_personalizzed_group, container, false)
        api = RetrofitClient.getClient(context!!).create(Api::class.java)

        initViews()
        setUpAdapter()
        initScrollListener()
        setUpCountrySpinner()


        personalizedGroupData.clear()
        AppUtils.showDialog(context!!)
        getPersonalizedGroups()

        return v
    }

    private fun initViews() {
        tvNoData = v.findViewById(R.id.tvNoData)
        pullToRefresh = v.findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            CURRENT_PAGE = 1
            LAST_PAGE = 1

            personalizedGroupData.clear()
            getPersonalizedGroups()
        }
        search = v.findViewById(R.id.search)
        search.setOnClickListener {
            CURRENT_PAGE = 1
            LAST_PAGE = 1

            personalizedGroupData.clear()
            AppUtils.showDialog(context!!)
            getPersonalizedGroups()
        }
        spinnerPersonalizedGroupCountry = v.findViewById(R.id.spinnerPersonalizedGroupCountry)
        pageTitle = v.findViewById(R.id.article_code_number)
        pageTitle.text = context!!.resources.getString(R.string.fapgPageTitle)
        fapgRecyclerView = v.findViewById(R.id.fapgRecyclerView)
        addPersonalizedGroup = v.findViewById(R.id.addPersonalizedGroup)
        addPersonalizedGroup.setOnClickListener {
            (parentFragment as FinancialManagementFragment).launchRightFragment(
                AddPersonalizedGroupFragment()
            )
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
        spinnerPersonalizedGroupCountry.adapter = positionAdapter
        spinnerPersonalizedGroupCountry.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        spinnerPersonalizedGroupCountry.setTitle(resources.getString(R.string.countriesSpinnerTitle))
        spinnerPersonalizedGroupCountry.onItemSelectedListener =
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

    private fun setUpAdapter() {
        personalizedGroupAdapter = PersonalizedGroupAdapter(this, personalizedGroupData)
        fapgRecyclerView.layoutManager = LinearLayoutManager(context!!)
        fapgRecyclerView.adapter = personalizedGroupAdapter
    }

    private fun initScrollListener() {
        fapgRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == personalizedGroupData.size - 1) {
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
                personalizedGroupData.add(null)
                personalizedGroupAdapter.notifyItemInserted(personalizedGroupData.size - 1)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        CURRENT_PAGE++
                        getPersonalizedGroups()
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getPersonalizedGroups() {
        val call = api.getPersonalizedGroupData(countryId, CURRENT_PAGE)
        RetrofitClient.apiCall(call, this, "GetPersonalizedGroups")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "GetPersonalizedGroups" -> {
                val model =
                    Gson().fromJson(jsonObject.toString(), PersonalizedGroupModel::class.java)

                if (personalizedGroupData.size > 0) {
                    personalizedGroupData.removeAt(personalizedGroupData.size - 1)
                    personalizedGroupAdapter.notifyItemRemoved(personalizedGroupData.size)
                }


                CURRENT_PAGE = model.meta?.current_page!!
                LAST_PAGE = model.meta?.last_page!!

                personalizedGroupData.addAll(model.data!!)

                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false

                if (personalizedGroupData.isNullOrEmpty()) {
                    tvNoData.visibility = View.VISIBLE
                    fapgRecyclerView.visibility = View.GONE
                } else {
                    tvNoData.visibility = View.GONE
                    fapgRecyclerView.visibility = View.VISIBLE
                }

                personalizedGroupAdapter.notifyDataSetChanged()
                isLoading = false


            }
            "DeletePersonalizedGroup" -> {
                personalizedGroupData.removeAt(itemPosition)
                personalizedGroupAdapter.notifyDataSetChanged()

                AppUtils.showToast(activity!!, jsonObject.getString("message"), true)


            }
        }
    }

    fun deletePersonalizedGroups(id: Int, position: Int) {
        itemPosition = position

        AppUtils.showDialog(context!!)
        val call = api.deletePersonalizedGroups(id)
        RetrofitClient.apiCall(call, this, "DeletePersonalizedGroup")
    }

    fun updateStatus(id: Int, position: Int) {
        itemPosition = position

        val call = api.updatePersonalizedGroupStatus(id, personalizedGroupData[position]?.status!!)
        RetrofitClient.apiCall(call, this, "UpdateStatus")
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


}