import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import java.util.Locale

class CustomArrayAdapter(context: Context, private val items: List<String>) :
    ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, items) {

    private var filteredItems: List<String> = items

    override fun getCount(): Int {
        return filteredItems.size
    }

    override fun getItem(position: Int): String? {
        return filteredItems[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val query = constraint?.toString()?.replace(" ", "")?.lowercase(Locale.getDefault()) ?: ""

                filteredItems = if (query.isEmpty()) {
                    items
                } else {
                    items.filter {
                        it.replace(" ", "").lowercase(Locale.getDefault()).contains(query)
                    }
                }

                filterResults.values = filteredItems
                filterResults.count = filteredItems.size
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = results?.values as List<String>? ?: items
                notifyDataSetChanged()
            }
        }
    }
}
