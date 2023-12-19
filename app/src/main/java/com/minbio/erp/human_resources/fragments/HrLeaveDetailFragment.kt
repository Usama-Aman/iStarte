package com.minbio.erp.human_resources.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
import com.minbio.erp.utils.PermissionKeys
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject

class HrLeaveDetailFragment : Fragment(), ResponseCallBack {

    private lateinit var v: View

    private lateinit var tvUser: TextView
    private lateinit var tvType: TextView
    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var tvNoOfDays: TextView
    private lateinit var tvNote: TextView
    private lateinit var tvRequestedBy: TextView
    private lateinit var tvApprovedBy: TextView
    private lateinit var tvCreationDate: TextView
    private lateinit var tvEmployeeId: TextView
    private lateinit var btnText1: TextView
    private lateinit var btnText2: TextView
    private lateinit var btnSave: LinearLayout
    private lateinit var btnDelete: LinearLayout
    private lateinit var back: ImageView
    private lateinit var customer_image: CircleImageView

    private lateinit var leaveData: LeavesListData
    private lateinit var leaveCompanyUser: ArrayList<LeaveCompanyUser>

    private lateinit var loginModel: LoginModel
    private lateinit var permissionsList: List<String>

    private var isSaveAccept = 0 /*0 for Save , 1 for Accept*/
    private var isDeleteReject = 0 /*0 for delete ,  1 for reject*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hr_leave_detail, container, false)

        loginModel = AppUtils.getLoginModel(context!!)
        permissionsList = loginModel.data.permissions.hr_management.split(",")

        initViews()

        return v
    }

    private fun initViews() {
        leaveData = arguments?.getParcelable("leaveData")!!
        leaveCompanyUser = arguments?.getParcelableArrayList("leaveCompanyUsers")!!

        customer_image = v.findViewById(R.id.customer_image)
        Glide.with(context!!).load(loginModel.data.image_path).placeholder(R.drawable.ic_plc)
            .into(customer_image)
        back = v.findViewById(R.id.back)
        back.setOnClickListener { (parentFragment as HumanResourceFragment).leaveTab.callOnClick() }

        tvEmployeeId = v.findViewById(R.id.tvEmployeeId)
        tvUser = v.findViewById(R.id.tvUser)
        tvType = v.findViewById(R.id.tvType)
        tvStartDate = v.findViewById(R.id.tvStartDate)
        tvEndDate = v.findViewById(R.id.tvEndDate)
        tvNoOfDays = v.findViewById(R.id.tvNoOfDays)
        tvNote = v.findViewById(R.id.tvNote)
        tvRequestedBy = v.findViewById(R.id.tvRequestedBy)
        tvApprovedBy = v.findViewById(R.id.tvApprovedBy)
        tvCreationDate = v.findViewById(R.id.tvCreationDate)
        btnSave = v.findViewById(R.id.btnSave)
        btnDelete = v.findViewById(R.id.btnDelete)
        btnText1 = v.findViewById(R.id.btnText1)
        btnText2 = v.findViewById(R.id.btnText2)

        tvEmployeeId.text = leaveData.company_user_name.toString()
        tvUser.text = leaveData.company_user_name
        tvType.text = leaveData.leave_type
        tvStartDate.text = "${leaveData.start_date}(${leaveData.start_half})"
        tvEndDate.text = "${leaveData.end_date}(${leaveData.end_half})"
        tvNoOfDays.text = leaveData.days.toString()
        tvNote.text = leaveData.note
        tvRequestedBy.text = leaveData.company_user_name
        tvApprovedBy.text = leaveData.aproved_by
        tvCreationDate.text = leaveData.date

        btnSave.setOnClickListener {
            if (isSaveAccept == 1) {
                acceptRejectLeave("approved")
            }
        }
        btnDelete.setOnClickListener {
            if (isDeleteReject == 0) {
                deleteLeave()
            } else {
                acceptRejectLeave("refused")
            }
        }


        checksForButtonPermissions()

    }

    private fun checksForButtonPermissions() {
        if (leaveData.company_user_id == loginModel.data.id && leaveData.status == "Pending") {
            btnSave.visibility = View.GONE
            btnDelete.visibility = View.VISIBLE
            btnText1.text = context!!.resources.getString(R.string.hrLDBtnSave)
            btnText2.text = context!!.resources.getString(R.string.hrLDBtnDelete)

            isSaveAccept = -1
            isDeleteReject = 0

            btnText1.setCompoundDrawables(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_pro_save
                )                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                , null, null, null
            )
            btnText2.setCompoundDrawables(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_delete
                )                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                , null, null, null
            )

            if (permissionsList.contains(PermissionKeys.delete_leaves))
                btnDelete.visibility = View.VISIBLE
            else
                btnDelete.visibility = View.GONE

        } else if (leaveData.company_user_id == loginModel.data.id && leaveData.status == "Assigned") {
            btnSave.visibility = View.GONE
            btnDelete.visibility = View.GONE
            btnText1.text = context!!.resources.getString(R.string.hrLDBtnSave)
            btnText2.text = context!!.resources.getString(R.string.hrLDBtnDelete)

            isDeleteReject = -1
            isSaveAccept = -1
        } else if (leaveData.company_user_id != loginModel.data.id && leaveData.status == "Assigned" && leaveData.assigned_to_id == loginModel.data.id) {
            btnSave.visibility = View.VISIBLE
            btnDelete.visibility = View.VISIBLE
            btnText1.text = context!!.resources.getString(R.string.hrLDBtnAccept)
            btnText2.text = context!!.resources.getString(R.string.hrLDBtnReject)

            btnText1.setCompoundDrawables(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_accept
                )?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                , null, null, null
            )
            btnText2.setCompoundDrawables(
                ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_reject_
                )                    ?.apply { setBounds(0, 0, intrinsicWidth, intrinsicHeight) }
                , null, null, null
            )

            isDeleteReject = 1
            isSaveAccept = 1

            if (permissionsList.contains(PermissionKeys.update_leaves_status)) {
                btnDelete.visibility = View.VISIBLE
                btnSave.visibility = View.VISIBLE
            } else {
                btnDelete.visibility = View.GONE
                btnSave.visibility = View.GONE
            }
        } else {
            btnSave.visibility = View.GONE
            btnDelete.visibility = View.GONE
            isDeleteReject = -1
            isSaveAccept = -1
        }
    }

    private fun acceptRejectLeave(status: String) {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.acceptRejectLeave(leaveData.id, status)
        RetrofitClient.apiCall(call, this, "AcceptRejectLeave")
    }


    private fun deleteLeave() {
        AppUtils.showDialog(context!!)
        val api = RetrofitClient.getClient(context!!).create(Api::class.java)
        val call = api.deleteLeave(leaveData.id)
        RetrofitClient.apiCall(call, this, "DeleteLeave")
    }


    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        AppUtils.dismissDialog()
        AppUtils.showToast(activity, jsonObject.getString("message"), true)
        when (tag) {
            "DeleteLeave" -> {
                (parentFragment as HumanResourceFragment).leaveTab.callOnClick()
            }
            "AcceptRejectLeave" -> {
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