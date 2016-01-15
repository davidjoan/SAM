package pe.cayro.sam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import pe.cayro.sam.R;
import pe.cayro.sam.model.Specialty;
import util.Constants;

/**
 * Created by David on 12/01/16.
 */
public class SpecialtyAutocompleterAdapter extends ArrayAdapter<Integer> implements Filterable {
    private static String TAG = SpecialtyAutocompleterAdapter.class.getSimpleName();

    public SpecialtyAutocompleterAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    private class ViewHolder {
        TextView name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Integer id = getItem(position);

        Realm realm = Realm.getDefaultInstance();
        Specialty specialty = realm.where(Specialty.class).equalTo(Constants.ID, id).findFirst();
        realm.close();

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.specialty_autocomplete_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.specialty_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText((specialty.getName().length() > 30) ?
                specialty.getName().substring(0, 30) + Constants.ELLIPSIS :
                specialty.getName());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Integer> data = new ArrayList<Integer>();
                FilterResults filterResults = new FilterResults();

                Realm  realm = Realm.getDefaultInstance();

                RealmResults<Specialty> realmResults = realm.where(Specialty.class).
                        contains(Constants.NAME, constraint.toString().toUpperCase()).findAll();

                for(Specialty specialty : realmResults){
                    data.add(Integer.valueOf(specialty.getId()));
                }

                filterResults.values = data;
                filterResults.count = data.size();
                realm.close();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    clear();
                    addAll((ArrayList<Integer>) results.values);
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}