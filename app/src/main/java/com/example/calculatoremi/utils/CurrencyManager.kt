package com.example.calculatoremi.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.calculatoremi.model.CurrencyItem
import java.text.DecimalFormat

object CurrencyManager {

    private const val PREF_NAME = "currency_preferences"
    private const val KEY_CURRENCY_CODE = "selected_currency_code"
    private const val KEY_COUNTRY_NAME = "selected_country_name"

    val DEFAULT_CURRENCY = CurrencyItem("United States", "US Dollar", "USD", "\$", "North America", "🇺🇸")

    val ALL_CURRENCIES: List<CurrencyItem> by lazy {
        listOf(
            // Asia (49)
            CurrencyItem("Afghanistan", "Afghan Afghani", "AFN", "؋", "Asia", "🇦🇫"),
            CurrencyItem("Armenia", "Armenian Dram", "AMD", "֏", "Asia", "🇦🇲"),
            CurrencyItem("Azerbaijan", "Azerbaijani Manat", "AZN", "₼", "Asia", "🇦🇿"),
            CurrencyItem("Bahrain", "Bahraini Dinar", "BHD", "BD", "Asia", "🇧🇭"),
            CurrencyItem("Bangladesh", "Bangladeshi Taka", "BDT", "৳", "Asia", "🇧🇩"),
            CurrencyItem("Bhutan", "Bhutanese Ngultrum", "BTN", "Nu.", "Asia", "🇧🇹"),
            CurrencyItem("Brunei", "Brunei Dollar", "BND", "B\$", "Asia", "🇧🇳"),
            CurrencyItem("Cambodia", "Cambodian Riel", "KHR", "៛", "Asia", "🇰🇭"),
            CurrencyItem("China", "Chinese Yuan", "CNY", "¥", "Asia", "🇨🇳"),
            CurrencyItem("Cyprus", "Euro", "EUR", "€", "Asia", "🇨🇾"),
            CurrencyItem("Georgia", "Georgian Lari", "GEL", "₾", "Asia", "🇬🇪"),
            CurrencyItem("India", "Indian Rupee", "INR", "₹", "Asia", "🇮🇳"),
            CurrencyItem("Indonesia", "Indonesian Rupiah", "IDR", "Rp", "Asia", "🇮🇩"),
            CurrencyItem("Iran", "Iranian Rial", "IRR", "﷼", "Asia", "🇮🇷"),
            CurrencyItem("Iraq", "Iraqi Dinar", "IQD", "IQD", "Asia", "🇮🇶"),
            CurrencyItem("Israel", "Israeli New Shekel", "ILS", "₪", "Asia", "🇮🇱"),
            CurrencyItem("Japan", "Japanese Yen", "JPY", "¥", "Asia", "🇯🇵"),
            CurrencyItem("Jordan", "Jordanian Dinar", "JOD", "JD", "Asia", "🇯🇴"),
            CurrencyItem("Kazakhstan", "Kazakhstani Tenge", "KZT", "₸", "Asia", "🇰🇿"),
            CurrencyItem("Kuwait", "Kuwaiti Dinar", "KWD", "KD", "Asia", "🇰🇼"),
            CurrencyItem("Kyrgyzstan", "Kyrgyzstani Som", "KGS", "som", "Asia", "🇰🇬"),
            CurrencyItem("Laos", "Lao Kip", "LAK", "₭", "Asia", "🇱🇦"),
            CurrencyItem("Lebanon", "Lebanese Pound", "LBP", "L£", "Asia", "🇱🇧"),
            CurrencyItem("Malaysia", "Malaysian Ringgit", "MYR", "RM", "Asia", "🇲🇾"),
            CurrencyItem("Maldives", "Maldivian Rufiyaa", "MVR", "Rf", "Asia", "🇲🇻"),
            CurrencyItem("Mongolia", "Mongolian Tögrög", "MNT", "₮", "Asia", "🇲🇳"),
            CurrencyItem("Myanmar", "Myanmar Kyat", "MMK", "K", "Asia", "🇲🇲"),
            CurrencyItem("Nepal", "Nepalese Rupee", "NPR", "Rs", "Asia", "🇳🇵"),
            CurrencyItem("North Korea", "North Korean Won", "KPW", "₩", "Asia", "🇰🇵"),
            CurrencyItem("Oman", "Omani Rial", "OMR", "OR", "Asia", "🇴🇲"),
            CurrencyItem("Pakistan", "Pakistani Rupee", "PKR", "Rs", "Asia", "🇵🇰"),
            CurrencyItem("Palestine", "Israeli New Shekel", "ILS", "₪", "Asia", "🇵🇸"),
            CurrencyItem("Philippines", "Philippine Peso", "PHP", "₱", "Asia", "🇵🇭"),
            CurrencyItem("Qatar", "Qatari Riyal", "QAR", "QR", "Asia", "🇶🇦"),
            CurrencyItem("Saudi Arabia", "Saudi Riyal", "SAR", "SR", "Asia", "🇸🇦"),
            CurrencyItem("Singapore", "Singapore Dollar", "SGD", "S\$", "Asia", "🇸🇬"),
            CurrencyItem("South Korea", "South Korean Won", "KRW", "₩", "Asia", "🇰🇷"),
            CurrencyItem("Sri Lanka", "Sri Lankan Rupee", "LKR", "Rs", "Asia", "🇱🇰"),
            CurrencyItem("Syria", "Syrian Pound", "SYP", "LS", "Asia", "🇸🇾"),
            CurrencyItem("Taiwan", "New Taiwan Dollar", "TWD", "NT\$", "Asia", "🇹🇼"),
            CurrencyItem("Tajikistan", "Tajikistani Somoni", "TJS", "SM", "Asia", "🇹🇯"),
            CurrencyItem("Thailand", "Thai Baht", "THB", "฿", "Asia", "🇹🇭"),
            CurrencyItem("Timor-Leste", "US Dollar", "USD", "\$", "Asia", "🇹🇱"),
            CurrencyItem("Turkey", "Turkish Lira", "TRY", "₺", "Asia", "🇹🇷"),
            CurrencyItem("Turkmenistan", "Turkmenistan Manat", "TMT", "T", "Asia", "🇹🇲"),
            CurrencyItem("United Arab Emirates", "UAE Dirham", "AED", "AED", "Asia", "🇦🇪"),
            CurrencyItem("Uzbekistan", "Uzbekistani Som", "UZS", "soʻm", "Asia", "🇺🇿"),
            CurrencyItem("Vietnam", "Vietnamese Đồng", "VND", "₫", "Asia", "🇻🇳"),
            CurrencyItem("Yemen", "Yemeni Rial", "YER", "YR", "Asia", "🇾🇪"),

            // Europe (44)
            CurrencyItem("Albania", "Albanian Lek", "ALL", "L", "Europe", "🇦🇱"),
            CurrencyItem("Andorra", "Euro", "EUR", "€", "Europe", "🇦🇩"),
            CurrencyItem("Austria", "Euro", "EUR", "€", "Europe", "🇦🇹"),
            CurrencyItem("Belarus", "Belarusian Ruble", "BYN", "Br", "Europe", "🇧🇾"),
            CurrencyItem("Belgium", "Euro", "EUR", "€", "Europe", "🇧🇪"),
            CurrencyItem("Bosnia and Herzegovina", "Convertible Mark", "BAM", "KM", "Europe", "🇧🇦"),
            CurrencyItem("Bulgaria", "Bulgarian Lev", "BGN", "лв", "Europe", "🇧🇬"),
            CurrencyItem("Croatia", "Euro", "EUR", "€", "Europe", "🇭🇷"),
            CurrencyItem("Czech Republic", "Czech Koruna", "CZK", "Kč", "Europe", "🇨🇿"),
            CurrencyItem("Denmark", "Danish Krone", "DKK", "kr", "Europe", "🇩🇰"),
            CurrencyItem("Estonia", "Euro", "EUR", "€", "Europe", "🇪🇪"),
            CurrencyItem("Finland", "Euro", "EUR", "€", "Europe", "🇫🇮"),
            CurrencyItem("France", "Euro", "EUR", "€", "Europe", "🇫🇷"),
            CurrencyItem("Germany", "Euro", "EUR", "€", "Europe", "🇩🇪"),
            CurrencyItem("Greece", "Euro", "EUR", "€", "Europe", "🇬🇷"),
            CurrencyItem("Hungary", "Hungarian Forint", "HUF", "Ft", "Europe", "🇭🇺"),
            CurrencyItem("Iceland", "Icelandic Króna", "ISK", "kr", "Europe", "🇮🇸"),
            CurrencyItem("Ireland", "Euro", "EUR", "€", "Europe", "🇮🇪"),
            CurrencyItem("Italy", "Euro", "EUR", "€", "Europe", "🇮🇹"),
            CurrencyItem("Kosovo", "Euro", "EUR", "€", "Europe", "🇽🇰"),
            CurrencyItem("Latvia", "Euro", "EUR", "€", "Europe", "🇱🇻"),
            CurrencyItem("Liechtenstein", "Swiss Franc", "CHF", "CHF", "Europe", "🇱🇮"),
            CurrencyItem("Lithuania", "Euro", "EUR", "€", "Europe", "🇱🇹"),
            CurrencyItem("Luxembourg", "Euro", "EUR", "€", "Europe", "🇱🇺"),
            CurrencyItem("Malta", "Euro", "EUR", "€", "Europe", "🇲🇹"),
            CurrencyItem("Moldova", "Moldovan Leu", "MDL", "L", "Europe", "🇲🇩"),
            CurrencyItem("Monaco", "Euro", "EUR", "€", "Europe", "🇲🇨"),
            CurrencyItem("Montenegro", "Euro", "EUR", "€", "Europe", "🇲🇪"),
            CurrencyItem("Netherlands", "Euro", "EUR", "€", "Europe", "🇳🇱"),
            CurrencyItem("North Macedonia", "Macedonian Denar", "MKD", "den", "Europe", "🇲🇰"),
            CurrencyItem("Norway", "Norwegian Krone", "NOK", "kr", "Europe", "🇳🇴"),
            CurrencyItem("Poland", "Polish Złoty", "PLN", "zł", "Europe", "🇵🇱"),
            CurrencyItem("Portugal", "Euro", "EUR", "€", "Europe", "🇵🇹"),
            CurrencyItem("Romania", "Romanian Leu", "RON", "lei", "Europe", "🇷🇴"),
            CurrencyItem("Russia", "Russian Ruble", "RUB", "₽", "Europe", "🇷🇺"),
            CurrencyItem("San Marino", "Euro", "EUR", "€", "Europe", "🇸🇲"),
            CurrencyItem("Serbia", "Serbian Dinar", "RSD", "din.", "Europe", "🇷🇸"),
            CurrencyItem("Slovakia", "Euro", "EUR", "€", "Europe", "🇸🇰"),
            CurrencyItem("Slovenia", "Euro", "EUR", "€", "Europe", "🇸🇮"),
            CurrencyItem("Spain", "Euro", "EUR", "€", "Europe", "🇪🇸"),
            CurrencyItem("Sweden", "Swedish Krona", "SEK", "kr", "Europe", "🇸🇪"),
            CurrencyItem("Switzerland", "Swiss Franc", "CHF", "CHF", "Europe", "🇨🇭"),
            CurrencyItem("Ukraine", "Ukrainian Hryvnia", "UAH", "₴", "Europe", "🇺🇦"),
            CurrencyItem("United Kingdom", "British Pound", "GBP", "£", "Europe", "🇬🇧"),

            // North America (23)
            CurrencyItem("Antigua and Barbuda", "East Caribbean Dollar", "XCD", "EC\$", "North America", "🇦🇬"),
            CurrencyItem("Bahamas", "Bahamian Dollar", "BSD", "B\$", "North America", "🇧🇸"),
            CurrencyItem("Barbados", "Barbadian Dollar", "BBD", "Bds\$", "North America", "🇧🇧"),
            CurrencyItem("Belize", "Belize Dollar", "BZD", "BZ\$", "North America", "🇧🇿"),
            CurrencyItem("Canada", "Canadian Dollar", "CAD", "CA\$", "North America", "🇨🇦"),
            CurrencyItem("Costa Rica", "Costa Rican Colón", "CRC", "₡", "North America", "🇨🇷"),
            CurrencyItem("Cuba", "Cuban Peso", "CUP", "\$", "North America", "🇨🇺"),
            CurrencyItem("Dominica", "East Caribbean Dollar", "XCD", "EC\$", "North America", "🇩🇲"),
            CurrencyItem("Dominican Republic", "Dominican Peso", "DOP", "RD\$", "North America", "🇩🇴"),
            CurrencyItem("El Salvador", "US Dollar", "USD", "\$", "North America", "🇸🇻"),
            CurrencyItem("Grenada", "East Caribbean Dollar", "XCD", "EC\$", "North America", "🇬🇩"),
            CurrencyItem("Guatemala", "Guatemalan Quetzal", "GTQ", "Q", "North America", "🇬🇹"),
            CurrencyItem("Haiti", "Haitian Gourde", "HTG", "G", "North America", "🇭🇹"),
            CurrencyItem("Honduras", "Honduran Lempira", "HNL", "L", "North America", "🇭🇳"),
            CurrencyItem("Jamaica", "Jamaican Dollar", "JMD", "J\$", "North America", "🇯🇲"),
            CurrencyItem("Mexico", "Mexican Peso", "MXN", "MX\$", "North America", "🇲🇽"),
            CurrencyItem("Nicaragua", "Nicaraguan Córdoba", "NIO", "C\$", "North America", "🇳🇮"),
            CurrencyItem("Panama", "Panamanian Balboa", "PAB", "B/.", "North America", "🇵🇦"),
            CurrencyItem("Saint Kitts and Nevis", "East Caribbean Dollar", "XCD", "EC\$", "North America", "🇰🇳"),
            CurrencyItem("Saint Lucia", "East Caribbean Dollar", "XCD", "EC\$", "North America", "🇱🇨"),
            CurrencyItem("Saint Vincent and the Grenadines", "East Caribbean Dollar", "XCD", "EC\$", "North America", "🇻🇨"),
            CurrencyItem("Trinidad and Tobago", "Trinidad & Tobago Dollar", "TTD", "TT\$", "North America", "🇹🇹"),
            CurrencyItem("United States", "US Dollar", "USD", "\$", "North America", "🇺🇸"),

            // South America (12)
            CurrencyItem("Argentina", "Argentine Peso", "ARS", "\$", "South America", "🇦🇷"),
            CurrencyItem("Bolivia", "Bolivian Boliviano", "BOB", "Bs.", "South America", "🇧🇴"),
            CurrencyItem("Brazil", "Brazilian Real", "BRL", "R\$", "South America", "🇧🇷"),
            CurrencyItem("Chile", "Chilean Peso", "CLP", "CLP\$", "South America", "🇨🇱"),
            CurrencyItem("Colombia", "Colombian Peso", "COP", "COL\$", "South America", "🇨🇴"),
            CurrencyItem("Ecuador", "US Dollar", "USD", "\$", "South America", "🇪🇨"),
            CurrencyItem("Guyana", "Guyanese Dollar", "GYD", "G\$", "South America", "🇬🇾"),
            CurrencyItem("Paraguay", "Paraguayan Guaraní", "PYG", "₲", "South America", "🇵🇾"),
            CurrencyItem("Peru", "Peruvian Sol", "PEN", "S/", "South America", "🇵🇪"),
            CurrencyItem("Suriname", "Surinamese Dollar", "SRD", "Sr\$", "South America", "🇸🇷"),
            CurrencyItem("Uruguay", "Uruguayan Peso", "UYU", "\$U", "South America", "🇺🇾"),
            CurrencyItem("Venezuela", "Venezuelan Bolívar", "VES", "Bs.S", "South America", "🇻🇪"),

            // Africa (54)
            CurrencyItem("Algeria", "Algerian Dinar", "DZD", "DA", "Africa", "🇩🇿"),
            CurrencyItem("Angola", "Angolan Kwanza", "AOA", "Kz", "Africa", "🇦🇴"),
            CurrencyItem("Benin", "West African CFA Franc", "XOF", "CFA", "Africa", "🇧🇯"),
            CurrencyItem("Botswana", "Botswana Pula", "BWP", "P", "Africa", "🇧🇼"),
            CurrencyItem("Burkina Faso", "West African CFA Franc", "XOF", "CFA", "Africa", "🇧🇫"),
            CurrencyItem("Burundi", "Burundian Franc", "BIF", "FBu", "Africa", "🇧🇮"),
            CurrencyItem("Cabo Verde", "Cape Verdean Escudo", "CVE", "Esc", "Africa", "🇨🇻"),
            CurrencyItem("Cameroon", "Central African CFA Franc", "XAF", "FCFA", "Africa", "🇨🇲"),
            CurrencyItem("Central African Republic", "Central African CFA Franc", "XAF", "FCFA", "Africa", "🇨🇫"),
            CurrencyItem("Chad", "Central African CFA Franc", "XAF", "FCFA", "Africa", "🇹🇩"),
            CurrencyItem("Comoros", "Comorian Franc", "KMF", "CF", "Africa", "🇰🇲"),
            CurrencyItem("Democratic Republic of the Congo", "Congolese Franc", "CDF", "FC", "Africa", "🇨🇩"),
            CurrencyItem("Republic of the Congo", "Central African CFA Franc", "XAF", "FCFA", "Africa", "🇨🇬"),
            CurrencyItem("Djibouti", "Djiboutian Franc", "DJF", "Fdj", "Africa", "🇩🇯"),
            CurrencyItem("Egypt", "Egyptian Pound", "EGP", "E£", "Africa", "🇪🇬"),
            CurrencyItem("Equatorial Guinea", "Central African CFA Franc", "XAF", "FCFA", "Africa", "🇬🇶"),
            CurrencyItem("Eritrea", "Eritrean Nakfa", "ERN", "Nfk", "Africa", "🇪🇷"),
            CurrencyItem("Eswatini", "Swazi Lilangeni", "SZL", "E", "Africa", "🇸🇿"),
            CurrencyItem("Ethiopia", "Ethiopian Birr", "ETB", "Br", "Africa", "🇪🇹"),
            CurrencyItem("Gabon", "Central African CFA Franc", "XAF", "FCFA", "Africa", "🇬🇦"),
            CurrencyItem("Gambia", "Gambian Dalasi", "GMD", "D", "Africa", "🇬🇲"),
            CurrencyItem("Ghana", "Ghanaian Cedi", "GHS", "GH₵", "Africa", "🇬🇭"),
            CurrencyItem("Guinea", "Guinean Franc", "GNF", "FG", "Africa", "🇬🇳"),
            CurrencyItem("Guinea-Bissau", "West African CFA Franc", "XOF", "CFA", "Africa", "🇬🇼"),
            CurrencyItem("Ivory Coast (Côte d'Ivoire)", "West African CFA Franc", "XOF", "CFA", "Africa", "🇨🇮"),
            CurrencyItem("Kenya", "Kenyan Shilling", "KES", "KSh", "Africa", "🇰🇪"),
            CurrencyItem("Lesotho", "Lesotho Loti", "LSL", "L", "Africa", "🇱🇸"),
            CurrencyItem("Liberia", "Liberian Dollar", "LRD", "L\$", "Africa", "🇱🇷"),
            CurrencyItem("Libya", "Libyan Dinar", "LYD", "LD", "Africa", "🇱🇾"),
            CurrencyItem("Madagascar", "Malagasy Ariary", "MGA", "Ar", "Africa", "🇲🇬"),
            CurrencyItem("Malawi", "Malawian Kwacha", "MWK", "MK", "Africa", "🇲🇼"),
            CurrencyItem("Mali", "West African CFA Franc", "XOF", "CFA", "Africa", "🇲🇱"),
            CurrencyItem("Mauritania", "Mauritanian Ouguiya", "MRU", "UM", "Africa", "🇲🇷"),
            CurrencyItem("Mauritius", "Mauritian Rupee", "MUR", "Rs", "Africa", "🇲🇺"),
            CurrencyItem("Morocco", "Moroccan Dirham", "MAD", "DH", "Africa", "🇲🇦"),
            CurrencyItem("Mozambique", "Mozambican Metical", "MZN", "MT", "Africa", "🇲🇿"),
            CurrencyItem("Namibia", "Namibian Dollar", "NAD", "N\$", "Africa", "🇳🇦"),
            CurrencyItem("Niger", "West African CFA Franc", "XOF", "CFA", "Africa", "🇳🇪"),
            CurrencyItem("Nigeria", "Nigerian Naira", "NGN", "₦", "Africa", "🇳🇬"),
            CurrencyItem("Rwanda", "Rwandan Franc", "RWF", "FRw", "Africa", "🇷🇼"),
            CurrencyItem("São Tomé and Príncipe", "São Tomé & Príncipe Dobra", "STN", "Db", "Africa", "🇸🇹"),
            CurrencyItem("Senegal", "West African CFA Franc", "XOF", "CFA", "Africa", "🇸🇳"),
            CurrencyItem("Seychelles", "Seychellois Rupee", "SCR", "SR", "Africa", "🇸🇨"),
            CurrencyItem("Sierra Leone", "Sierra Leonean Leone", "SLE", "Le", "Africa", "🇸🇱"),
            CurrencyItem("Somalia", "Somali Shilling", "SOS", "Sh.So.", "Africa", "🇸🇴"),
            CurrencyItem("South Africa", "South African Rand", "ZAR", "R", "Africa", "🇿🇦"),
            CurrencyItem("South Sudan", "South Sudanese Pound", "SSP", "SSP", "Africa", "🇸🇸"),
            CurrencyItem("Sudan", "Sudanese Pound", "SDG", "SDG", "Africa", "🇸🇩"),
            CurrencyItem("Tanzania", "Tanzanian Shilling", "TZS", "TSh", "Africa", "🇹🇿"),
            CurrencyItem("Togo", "West African CFA Franc", "XOF", "CFA", "Africa", "🇹🇬"),
            CurrencyItem("Tunisia", "Tunisian Dinar", "TND", "DT", "Africa", "🇹🇳"),
            CurrencyItem("Uganda", "Ugandan Shilling", "UGX", "USh", "Africa", "🇺🇬"),
            CurrencyItem("Zambia", "Zambian Kwacha", "ZMW", "K", "Africa", "🇿🇲"),
            CurrencyItem("Zimbabwe", "Zimbabwe Gold", "ZWG", "ZiG", "Africa", "🇿🇼"),

            // Oceania (14)
            CurrencyItem("Australia", "Australian Dollar", "AUD", "AU\$", "Oceania", "🇦🇺"),
            CurrencyItem("Fiji", "Fijian Dollar", "FJD", "FJ\$", "Oceania", "🇫🇯"),
            CurrencyItem("Kiribati", "Australian Dollar", "AUD", "AU\$", "Oceania", "🇰🇮"),
            CurrencyItem("Marshall Islands", "US Dollar", "USD", "\$", "Oceania", "🇲🇭"),
            CurrencyItem("Micronesia", "US Dollar", "USD", "\$", "Oceania", "🇫🇲"),
            CurrencyItem("Nauru", "Australian Dollar", "AUD", "AU\$", "Oceania", "🇳🇷"),
            CurrencyItem("New Zealand", "New Zealand Dollar", "NZD", "NZ\$", "Oceania", "🇳🇿"),
            CurrencyItem("Palau", "US Dollar", "USD", "\$", "Oceania", "🇵🇼"),
            CurrencyItem("Papua New Guinea", "Papua New Guinean Kina", "PGK", "K", "Oceania", "🇵🇬"),
            CurrencyItem("Samoa", "Samoan Tālā", "WST", "WS\$", "Oceania", "🇼🇸"),
            CurrencyItem("Solomon Islands", "Solomon Islands Dollar", "SBD", "SI\$", "Oceania", "🇸🇧"),
            CurrencyItem("Tonga", "Tongan Paʻanga", "TOP", "T\$", "Oceania", "🇹🇴"),
            CurrencyItem("Tuvalu", "Australian Dollar", "AUD", "AU\$", "Oceania", "🇹🇻"),
            CurrencyItem("Vanuatu", "Vanuatu Vatu", "VUV", "VT", "Oceania", "🇻🇺")
        )
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getSelectedCurrency(context: Context): CurrencyItem {
        val prefs = getPrefs(context)
        val savedCountry = prefs.getString(KEY_COUNTRY_NAME, null)
        val code = prefs.getString(KEY_CURRENCY_CODE, "INR") ?: "INR"

        if (!savedCountry.isNullOrEmpty()) {
            val matchedCountry = ALL_CURRENCIES.find { it.countryName.equals(savedCountry, ignoreCase = true) }
            if (matchedCountry != null) return matchedCountry
        }

        return ALL_CURRENCIES.find { it.currencyCode == code }
            ?: ALL_CURRENCIES.find { it.currencyCode == "USD" }
            ?: DEFAULT_CURRENCY
    }

    fun getCurrencySymbol(context: Context): String {
        return getSelectedCurrency(context).symbol
    }

    fun getCurrencyCode(context: Context): String {
        return getSelectedCurrency(context).currencyCode
    }

    fun setCurrency(context: Context, currencyItem: CurrencyItem) {
        getPrefs(context).edit()
            .putString(KEY_COUNTRY_NAME, currencyItem.countryName)
            .putString(KEY_CURRENCY_CODE, currencyItem.currencyCode)
            .apply()
    }

    fun formatAmount(context: Context, amount: Double): String {
        val symbol = getCurrencySymbol(context)
        val formatter = DecimalFormat("#,##,###.##")
        val formattedNumber = formatter.format(amount)
        return if (amount < 0) "-$symbol$formattedNumber" else "$symbol$formattedNumber"
    }

    fun formatAmountLong(context: Context, amount: Long): String {
        val symbol = getCurrencySymbol(context)
        val formatter = DecimalFormat("#,##,###")
        val formattedNumber = formatter.format(amount)
        return if (amount < 0) "-$symbol$formattedNumber" else "$symbol$formattedNumber"
    }
}
