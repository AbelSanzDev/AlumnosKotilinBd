package com.sanchezurrea.appmenubutton.database

import android.os.Parcel
import android.os.Parcelable

data class Alumno(
    var id: Int = 0,
    var matricula: String = "",
    var nombre: String = "",
    var domicilio: String = "",
    var especialidad: String = "",
    var foto: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(matricula)
        parcel.writeString(nombre)
        parcel.writeString(domicilio)
        parcel.writeString(especialidad)
        parcel.writeString(foto)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Alumno> {
        override fun createFromParcel(parcel: Parcel): Alumno {
            return Alumno(parcel)
        }

        override fun newArray(size: Int): Array<Alumno?> {
            return arrayOfNulls(size)
        }
    }
}
