package com.minbio.erp.human_resources.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.human_resources.HumanResourceFragment
import com.minbio.erp.human_resources.models.VatsData
import com.minbio.erp.human_resources.models.VatsModel
import com.minbio.erp.human_resources.models.ExpensesCompanyUser
import com.minbio.erp.human_resources.models.ExpensesListData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import com.minbio.erp.utils.PermissionKeys
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_hr_expense.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class HrAddExpenseFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View

    private lateinit var tvStartDate: TextView
    private lateinit var etAmount: EditText
    private lateinit var etNote: EditText
    private lateinit var userResponsibleSpinner: CustomSearchableSpinner
    private lateinit var spinnerVat: CustomSearchableSpinner
    private lateinit var btnSave: LinearLayout
    private lateinit var btnDelete: LinearLayout
    private lateinit var approvedByLinear: LinearLayout
    private lateinit var back: ImageView
    private lateinit var customer_image: CircleImageView

    private var responsibleUserId = 0
    private var vatAccountId = 0
    private var dateId = ""

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    private lateinit var expenseData: ExpensesListData
    private lateinit var expensesCompanyUser: ArrayList<ExpensesCompanyUser>

    private var expenseVATAccountsData: MutableList<VatsData> = ArrayList()

    private var isHR = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hr_expense, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.hr_management.split(",")

        initViews()

        getVatAccounts()

        return v
    }

    private fun initViews() {
        isHR = arguments?.getBoolean("isHR", false)!!
        if (isHR) {
            expenseData = arguments?.getParcelable("expenseData")!!
            expensesCompanyUser = arguments?.getParcelableArrayList("expenseCompanyUser")!!
        }

        customer_image = v.findViewById(R.id.customer_image)
        tvStartDate = v.findViewById(R.id.tvStartDate)
        etAmount = v.findViewById(R.id.etAmount)
        etNote = v.findViewById(R.id.etNote)
        userResponsibleSpinner = v.findViewById(R.id.userResponsibleSpinner)
        spinnerVat = v.findViewById(R.id.spinnerVat)
        approvedByLinear = v.findViewById(R.id.approvedByLinear)


        tvStartDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePicker = DatePickerDialog(
                activity!!,
                { _, year, monthOfYear, dayOfMonth ->

                    tvStartDate.text =
                        AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)
                    dateId = "$day-${monthOfYear + 1}-$year"
                },
                year,
                month,
                day
            )
            datePicker.show()
        }

        btnSave = v.findViewById(R.id.btnSave)
        btnDelete = v.findViewById(R.id.btnDelete)
        back = v.findViewById(R.id.back)
        back.setOnClickListener { (parentFragment as HumanResourceFragment).expenseTab.callOnClick() }
        btnSave.setOnClickListener {
            if (isHR)
                assignExpenseData()
            else
                validate()

        }
        btnDelete.setOnClickListener { deleteExpenseData() }

        if (!isHR)
            Glide
                .with(context!!)
                .load(loginModel.data.image_path)
                .centerCrop()
                .placeholder(R.drawable.ic_plc)
                .into(customer_image)
        else
            customer_image.visibility = View.GONE

    }

    private fun getVatAccounts() {
        AppUtils.showDialog(context!!)
        val call =
            RetrofitClient.getClient(context!!).create(Api::class.java).getCountryBaseVatList()
        RetrofitClient.apiCall(call, this, "VatAccountList")
    }

    private fun enableDisableViews(b: Boolean) {
        if (isHR) {
            btnDelete.visibility = View.VISIBLE
            approvedByLinear.visibility = View.VISIBLE
        } else {
            btnDelete.visibility = View.GONE
            approvedByLinear.visibility = View.GONE
        }

        etNote.isEnabled = b
        etAmount.isEnabled = b
        tvStartDate.isEnabled = b
        spinnerVat.isEnabled = b

        if (isHR) {

            if (permissionsList.contains(PermissionKeys.assign_expenses))
                btnSave.visibility = View.VISIBLE
            else
                btnSave.visibility = View.GONE

            if (permissionsList.contains(PermissionKeys.delete_expenses))
                btnDelete.visibility = View.VISIBLE
            else
                btnDelete.visibility = View.GONE

            tvStartDate.text = expenseData.date
            etNote.setText(expenseData.note)
            etAmount.setText(expenseData.amount)

            for (i in expenseVATAccountsData.indices)
                if (expenseVATAccountsData[i].id == expenseData.vat_account_id)
                    spinnerVat.setSelection(i)

            val approvedStrings: MutableList<String> = ArrayList()
            for (i in expensesCompanyUser.indices)
                approvedStrings.add("${expensesCompanyUser[i].first_name} ${expensesCompanyUser[i].last_name}")

            val approvedAdapter: ArrayAdapter<String> =
                ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, approvedStrings)
            userResponsibleSpinner.adapter = approvedAdapter
            userResponsibleSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
            userResponsibleSpinner.setTitle(resources.getString(R.string.approvedBySpinnerTitle))
            userResponsibleSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        responsibleUserId = expensesCompanyUser[parent?.selectedItemPosition!!].id
                    }
                }


        }

    }

    private fun validate() {

        if (tvStartDate.text.toString().isBlank()) {
            errorStartDate.visibility = View.VISIBLE
            errorStartDate.setText(R.string.hrExpenseErrorStartDate);
            AppUtils.setBackground(tvStartDate, R.drawable.input_border_bottom_red)
            tvStartDate.requestFocus()
            return
        } else {
            AppUtils.setBackground(tvStartDate, R.drawable.input_border_bottom)
            errorStartDate.visibility = View.INVISIBLE
        }

        if (etAmount.text.toString().isBlank()) {
            errorAmount.visibility = View.VISIBLE
            errorAmount.setText(R.string.hrExpenseErrorAmount);
            AppUtils.setBackground(etAmount, R.drawable.input_border_bottom_red)
            etAmount.requestFocus()
            return
        } else {
            AppUtils.setBackground(etAmount, R.drawable.input_border_bottom)
            errorAmount.visibility = View.INVISIBLE
        }

        if (etNote.text.toString().isBlank()) {
            errorLeaveNote.visibility = View.VISIBLE
            errorLeaveNote.setText(R.string.hrExpenseErrorNote);
            AppUtils.setBackground(etNote, R.drawable.round_box_stroke_red)
            etNote.requestFocus()
            return
        } else {
            AppUtils.setBackground(etNote, R.drawable.round_box_light_stroke)
            errorLeaveNote.visibility = View.INVISIBLE
        }

        postExpenseData()
    }


    private fun assignExpenseData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.assignExpense(
            expenseData.id, responsibleUserId
        )
        RetrofitClient.apiCall(call, this, "AssignExpenseData")
    }

    private fun deleteExpenseData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.deleteExpense(expenseData.id)
        RetrofitClient.apiCall(call, this, "DeleteExpense")
    }


    private fun postExpenseData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.postExpenseData(
            dateId, etAmount.text.toString(), etNote.text.toString(), vatAccountId
        )
        RetrofitClient.apiCall(call, this, "PostExpenseData")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        when (tag) {
            "PostExpenseData" -> {
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
                (parentFragment as HumanResourceFragment).expenseTab.callOnClick()
            }
            "AssignExpenseData" -> {
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
                (parentFragment as HumanResourceFragment).expenseTab.callOnClick()
            }
            "DeleteExpense" -> {
                AppUtils.showToast(activity, jsonObject.getString("message"), true)
                (parentFragment as HumanResourceFragment).expenseTab.callOnClick()
            }
            "VatAccountList" -> {
                val model =
                    Gson().fromJson(jsonObject.toString(), VatsModel::class.java)

                expenseVATAccountsData.clear()
                expenseVATAccountsData.addAll(model.data)

                setUpVatSpinner()
                enableDisableViews(!isHR)
            }
        }
    }

    private fun setUpVatSpinner() {
        val vatStrings: MutableList<String> = ArrayList()
        for (i in expenseVATAccountsData.indices)
            vatStrings.add(expenseVATAccountsData[i].rate)

        val vatAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, vatStrings)
        spinnerVat.adapter = vatAdapter
        spinnerVat.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        spinnerVat.setTitle(resources.getString(R.string.vatSpinnerTitle))
        spinnerVat.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    vatAccountId = expenseVATAccountsData[parent?.selectedItemPosition!!].id
                }
            }
    }

    override fun onError(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), false)
    }

    override fun onException(message: String?, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, message!!, true)
    }


}