package com.minbio.erp.financial_management.accounting.fragments.personalized_group.models

import android.os.Parcel
import android.os.Parcelable
import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class PersonalizedGroupModel(
    var `data`: List<PersonalizedGroupData>?,
    var links: Links?,
    var message: String?,
    var meta: Meta?,
    var status: Boolean?
)

data class PersonalizedGroupData(
    var calculated: Int?,
    var chart_account: List<ChartAccount>?,
    var code: String?,
    var comment: String?,
    var country: Country?,
    var country_id: Int?,
    var formula: String?,
    var id: Int?,
    var label: String?,
    var position: Int?,
    var status: Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.createTypedArrayList(ChartAccount),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Country::class.java.classLoader),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(calculated)
        parcel.writeTypedList(chart_account)
        parcel.writeString(code)
        parcel.writeString(comment)
        parcel.writeParcelable(country, flags)
        parcel.writeValue(country_id)
        parcel.writeString(formula)
        parcel.writeValue(id)
        parcel.writeString(label)
        parcel.writeValue(position)
        parcel.writeValue(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PersonalizedGroupData> {
        override fun createFromParcel(parcel: Parcel): PersonalizedGroupData {
            return PersonalizedGroupData(parcel)
        }

        override fun newArray(size: Int): Array<PersonalizedGroupData?> {
            return arrayOfNulls(size)
        }
    }
}

data class ChartAccount(
    var account_group: String?,
    var account_number: String?,
    var chart_accounts_model_id: Int?,
    var company_id: Int?,
    var created_by: Int?,
    var id: Int?,
    var label: String?,
    var parent_id: Int?,
    var personalized_group_id: Int?,
    var short_label: String?,
    var status: Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(account_group)
        parcel.writeString(account_number)
        parcel.writeValue(chart_accounts_model_id)
        parcel.writeValue(company_id)
        parcel.writeValue(created_by)
        parcel.writeValue(id)
        parcel.writeString(label)
        parcel.writeValue(parent_id)
        parcel.writeValue(personalized_group_id)
        parcel.writeString(short_label)
        parcel.writeValue(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChartAccount> {
        override fun createFromParcel(parcel: Parcel): ChartAccount {
            return ChartAccount(parcel)
        }

        override fun newArray(size: Int): Array<ChartAccount?> {
            return arrayOfNulls(size)
        }
    }
}

data class Country(
    var id: Int?,
    var image_path: String?,
    var name: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(image_path)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Country> {
        override fun createFromParcel(parcel: Parcel): Country {
            return Country(parcel)
        }

        override fun newArray(size: Int): Array<Country?> {
            return arrayOfNulls(size)
        }
    }
}