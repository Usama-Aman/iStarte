package com.minbio.erp.cashier_desk.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.minbio.erp.network.Api
import com.minbio.erp.network.ResponseCallBack
import com.minbio.erp.network.RetrofitClient
import com.minbio.erp.R
import com.minbio.erp.cashier_desk.models.CDOrderDetail
import com.minbio.erp.customer_management.fragments.CustomerOverdraft
import com.minbio.erp.order_for_preparation.models.OrderDetails
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.AppUtils.Companion.hideKeyboard
import com.minbio.erp.utils.AppUtils.Companion.showToast
import com.minbio.erp.utils.Constants.mLastClickTime
import com.stripe.android.view.CardMultilineWidget
import org.json.JSONObject

class PaymentDialog(
    _context: Context,
    _orderDetails: CDOrderDetail?,
    _cashierDeskPayment: CashierDeskPayment?,
    _customerOverdraft: CustomerOverdraft?,
    _fromWhere: String /*customerOverdraftFragment , cashierDeskFragment*/
) : Dialog(_context) {

    private val orderDetails: CDOrderDetail? = _orderDetails
    private val cashierDeskPayment: CashierDeskPayment? = _cashierDeskPayment
    private val customerOverdraft: CustomerOverdraft? = _customerOverdraft
    private val fromWhere: String = _fromWhere


    private lateinit var cardMultilineWidget: CardMultilineWidget
    lateinit var btnPay: LinearLayout
    private lateinit var closeDialog: ImageView
    private lateinit var chargesLayout: ConstraintLayout
    private lateinit var paymentDialogText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_cashier_desk_payment)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        chargesLayout = findViewById(R.id.chargesLayout)
        paymentDialogText = findViewById(R.id.paymentDialogText)

        if (fromWhere == "customerOverdraftFragment") {
            chargesLayout.visibility = View.GONE
            paymentDialogText.text =
                context.resources.getString(R.string.cmoLabelPayOverdraftDialogTitle)
        } else {
            chargesLayout.visibility = View.VISIBLE
            paymentDialogText.text = context.resources.getString(R.string.cdpLabelDetails)

            findViewById<TextView>(R.id.totalHTAnswer).text =
                AppUtils.appendCurrency(
                    (orderDetails?.subtotal?.toDouble()!! - cashierDeskPayment?.overdraftAmount?.toDouble()!!).toString(),
                    context
                )
            findViewById<TextView>(R.id.totalTaxAnswer).text =
                AppUtils.appendCurrency(orderDetails.discount.toString(), context)
            findViewById<TextView>(R.id.deliveryAnswer).text =
                AppUtils.appendCurrency(orderDetails.delivery_charges, context)
            findViewById<TextView>(R.id.totalTAVAnswer).text =
                AppUtils.appendCurrency(orderDetails.vat, context)
            findViewById<TextView>(R.id.creditNoteAnswer).text =
                AppUtils.appendCurrency(orderDetails.credit_note, context)
            findViewById<TextView>(R.id.totalTTCAnswer).text =
                AppUtils.appendCurrency(
                    ((orderDetails.grandtotal.toDouble() - cashierDeskPayment.overdraftAmount.toDouble()) + orderDetails.delivery_charges.toDouble()).toString(),
                    context
                )
        }


        findViewById<ImageView>(R.id.closeDialog).setOnClickListener { dismiss() }

        cardMultilineWidget = findViewById(R.id.card_multiline_widget)
        btnPay = findViewById(R.id.btn_dialog_payment)

        btnPay.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            hideKeyboard(ownerActivity!!)

            if (fromWhere == "customerOverdraftFragment") {
                customerOverdraft?.initializedStripeToken(cardMultilineWidget.card)
            } else
                cashierDeskPayment?.initializedStripeToken(cardMultilineWidget.card)
            dismiss()
        }


    }


    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
        hideKeyboard(ownerActivity!!)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm =
                        ownerActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}