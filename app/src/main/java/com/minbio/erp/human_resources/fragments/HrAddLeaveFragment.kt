package com.minbio.erp.human_resources.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.auth.models.LoginModel
import com.minbio.erp.human_resources.HumanResourceFragment
import com.minbio.erp.human_resources.models.LeaveCompanyUser
import com.minbio.erp.human_resources.models.LeavesListData
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.CustomSearchableSpinner
import com.minbio.erp.utils.PermissionKeys
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_hr_profile.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class HrAddLeaveFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View

    private lateinit var etNote: EditText
    private lateinit var leaveTypeSpinner: CustomSearchableSpinner
    private lateinit var leaveStartTimeSpinner: CustomSearchableSpinner
    private lateinit var leaveEndTimeSpinner: CustomSearchableSpinner
    private lateinit var approvedBySpinner: CustomSearchableSpinner
    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var customer_image: CircleImageView

    private var leaveTypeId = ""
    private var leaveStartTimeId = ""
    private var leaveEndTimeId = ""
    private var startDateId = ""
    private var endDateId = ""
    private var leaveCompanyUserId = 0

    private lateinit var approvedByLinear: LinearLayout
    private lateinit var btnSave: LinearLayout
    private lateinit var btnDelete: LinearLayout
    private lateinit var back: ImageView

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    private var isHR = false
    private lateinit var hrUserLeaveData: LeavesListData
    private lateinit var leaveCompanyUser: ArrayList<LeaveCompanyUser>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hr_profile, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.hr_management.split(",")

        initViews()

        return v
    }

    private fun initViews() {
        isHR = arguments?.getBoolean("isHR", false)!!
        if (isHR) {
            hrUserLeaveData = arguments?.getParcelable("leaveData")!!
            leaveCompanyUser = arguments?.getParcelableArrayList("leaveCompanyUsers")!!
        }

        back = v.findViewById(R.id.back)
        back.setOnClickListener { (parentFragment as HumanResourceFragment).leaveTab.callOnClick() }

        etNote = v.findViewById(R.id.etNote)
        leaveTypeSpinner = v.findViewById(R.id.leaveTypeSpinner)
        leaveStartTimeSpinner = v.findViewById(R.id.leaveStartTimeSpinner)
        leaveEndTimeSpinner = v.findViewById(R.id.leaveEndTimeSpinner)
        approvedBySpinner = v.findViewById(R.id.approvedBySpinner)
        tvStartDate = v.findViewById(R.id.tvLeaveStartDate)
        tvEndDate = v.findViewById(R.id.tvLeaveEndDate)
        approvedByLinear = v.findViewById(R.id.approvedByLinear)
        customer_image = v.findViewById(R.id.customer_image)

        tvStartDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePicker = DatePickerDialog(
                activity!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    tvStartDate.text =
                        AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)

                    startDateId = "$day-${monthOfYear + 1}-$year"
                },
                year,
                month,
                day
            )
            datePicker.show()
        }
        tvEndDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePicker = DatePickerDialog(
                activity!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    tvEndDate.text =
                        AppUtils.getDateFormat(context!!, dayOfMonth, monthOfYear + 1, year)

                    endDateId = "$day-${monthOfYear + 1}-$year"
                },
                year,
                month,
                day
            )
            datePicker.show()
        }

        btnSave = v.findViewById(R.id.btnSave)
        btnDelete = v.findViewById(R.id.btnDelete)
        btnSave.setOnClickListener {
            if (isHR)
                postLeaveApproval()
            else
                validate()

        }
        btnDelete.setOnClickListener { deleteLeaveData() }

        if (!isHR)
            Glide
                .with(context!!)
                .load(loginModel.data.image_path)
                .centerCrop()
                .placeholder(R.drawable.ic_plc)
                .into(customer_image)
        else
            customer_image.visibility = View.GONE

        setUpSpinners()
    }

    private fun postLeaveApproval() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.assignLeave(
            hrUserLeaveData.id, leaveCompanyUserId
        )
        RetrofitClient.apiCall(call, this, "AssignLeaveData")
    }

    private fun deleteLeaveData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.deleteLeave(hrUserLeaveData.id)
        RetrofitClient.apiCall(call, this, "DeleteLeave")
    }

    private fun setUpSpinners() {
        /*Leave Type Spinner*/
        val leaveTypes = arrayOf(
            context!!.resources.getString(R.string.hrLeaveCasual),
            context!!.resources.getString(R.string.hrLeaveSick)
        )

        val leaveTypesAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, leaveTypes)
        leaveTypeSpinner.adapter = leaveTypesAdapter
        leaveTypeSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        leaveTypeSpinner.setTitle(resources.getString(R.string.leaveTypeSpinnerTitle))
        leaveTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                leaveTypeId = leaveTypes[parent?.selectedItemPosition!!]
            }
        }

        /*Start Time  Spinner*/
        val startTimes = arrayOf(
            context!!.resources.getString(R.string.hrLeaveMorning),
            context!!.resources.getString(R.string.hrLeaveAfternoon),
            context!!.resources.getString(R.string.hrLeaveEvening)
        )

        val leaveStartTimeAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, startTimes)
        leaveStartTimeSpinner.adapter = leaveStartTimeAdapter
        leaveStartTimeSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        leaveStartTimeSpinner.setTitle(resources.getString(R.string.leaveTimeSpinnerTitle))
        leaveStartTimeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                leaveStartTimeId = startTimes[parent?.selectedItemPosition!!]
            }
        }
        /*End Time Spinner */

        val leaveEndTimeAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, startTimes)
        leaveEndTimeSpinner.adapter = leaveEndTimeAdapter
        leaveEndTimeSpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
        leaveEndTimeSpinner.setTitle(resources.getString(R.string.leaveTimeSpinnerTitle))
        leaveEndTimeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                leaveEndTimeId = startTimes[parent?.selectedItemPosition!!]
            }
        }

        enableDisableViews(!isHR)
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
        leaveTypeSpinner.isEnabled = b
        leaveStartTimeSpinner.isEnabled = b
        leaveEndTimeSpinner.isEnabled = b
        tvStartDate.isEnabled = b
        tvEndDate.isEnabled = b

        if (isHR) {
            if (permissionsList.contains(PermissionKeys.assign_leaves))
                btnSave.visibility = View.VISIBLE
            else
                btnSave.visibility = View.GONE

            if (permissionsList.contains(PermissionKeys.delete_leaves))
                btnDelete.visibility = View.VISIBLE
            else
                btnDelete.visibility = View.GONE

            tvStartDate.text = hrUserLeaveData.start_date
            tvEndDate.text = hrUserLeaveData.end_date
            etNote.setText(hrUserLeaveData.note)

            when (hrUserLeaveData.leave_type) {
                "Casual" -> {
                    leaveTypeSpinner.setSelection(0)
                }
                "Sick" -> {
                    leaveTypeSpinner.setSelection(1)
                }
            }

            when (hrUserLeaveData.start_half) {
                "Morning" -> {
                    leaveStartTimeSpinner.setSelection(0)
                }
                "Afternoon" -> {
                    leaveStartTimeSpinner.setSelection(1)
                }
                "Evening" -> {
                    leaveStartTimeSpinner.setSelection(2)
                }
            }

            when (hrUserLeaveData.end_half) {
                "Morning" -> {
                    leaveEndTimeSpinner.setSelection(0)
                }
                "Afternoon" -> {
                    leaveEndTimeSpinner.setSelection(1)
                }
                "Evening" -> {
                    leaveEndTimeSpinner.setSelection(2)
                }
            }

            val approvedStrings: MutableList<String> = ArrayList()
            for (i in leaveCompanyUser.indices)
                approvedStrings.add("${leaveCompanyUser[i].first_name} ${leaveCompanyUser[i].last_name}")

            val approvedAdapter: ArrayAdapter<String> =
                ArrayAdapter<String>(this.context!!, R.layout.dropdown_item, approvedStrings)
            approvedBySpinner.adapter = approvedAdapter
            approvedBySpinner.setPositiveButton(resources.getString(R.string.spinnerBtnClose))
            approvedBySpinner.setTitle(resources.getString(R.string.approvedBySpinnerTitle))
            approvedBySpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        leaveCompanyUserId = leaveCompanyUser[parent?.selectedItemPosition!!].id
                    }
                }
        }

    }

    private fun validate() {

        if (tvStartDate.text.toString().isBlank()) {
            errorLeaveStartDate.visibility = View.VISIBLE
            errorLeaveStartDate.setText(R.string.hrLeaveErrorStartDate);
            AppUtils.setBackground(tvStartDate, R.drawable.input_border_bottom_red)
            tvStartDate.requestFocus()
            return
        } else {
            AppUtils.setBackground(tvStartDate, R.drawable.input_border_bottom)
            errorLeaveStartDate.visibility = View.INVISIBLE
        }

        if (tvEndDate.text.toString().isBlank()) {
            errorLeaveEndDate.visibility = View.VISIBLE
            errorLeaveEndDate.setText(R.string.hrLeaveErrorEndDate);
            AppUtils.setBackground(tvEndDate, R.drawable.input_border_bottom_red)
            tvEndDate.requestFocus()
            return
        } else {
            AppUtils.setBackground(tvEndDate, R.drawable.input_border_bottom)
            errorLeaveEndDate.visibility = View.INVISIBLE
        }

        if (etNote.text.toString().isBlank()) {
            errorLeaveNote.visibility = View.VISIBLE
            errorLeaveNote.setText(R.string.hrLeaveErrorNote);
            AppUtils.setBackground(etNote, R.drawable.round_box_stroke_red)
            etNote.requestFocus()
            return
        } else {
            AppUtils.setBackground(etNote, R.drawable.round_box_light_stroke)
            errorLeaveNote.visibility = View.INVISIBLE
        }

        postLeaveData()

    }

    private fun postLeaveData() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.postLeaveData(
            leaveTypeId, startDateId, endDateId,
            leaveStartTimeId, leaveEndTimeId, etNote.text.toString()
        )
        RetrofitClient.apiCall(call, this, "PostLeaveData")
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), true)
        when (tag) {
            "PostLeaveData" -> {
                (parentFragment as HumanResourceFragment).leaveTab.callOnClick()
            }
            "AssignLeaveData" -> {
                (parentFragment as HumanResourceFragment).leaveTab.callOnClick()
            }
            "DeleteLeave" -> {
                (parentFragment as HumanResourceFragment).leaveTab.callOnClick()
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