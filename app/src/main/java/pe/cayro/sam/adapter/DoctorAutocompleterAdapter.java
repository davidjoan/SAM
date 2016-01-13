package pe.cayro.sam.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import pe.cayro.sam.R;
import pe.cayro.sam.model.Doctor;

/**
 * Created by David on 12/01/16.
 */
public class DoctorAutocompleterAdapter extends ArrayAdapter<Integer> implements Filterable {
    private static String TAG = DoctorAutocompleterAdapter.class.getSimpleName();

    public DoctorAutocompleterAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    private class ViewHolder {
        ImageView image;
        TextView name;
        TextView code;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Integer id = getItem(position);

        Realm realm = Realm.getDefaultInstance();
        Doctor doctor = realm.where(Doctor.class).equalTo("id", id).findFirst();
        realm.close();

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.doctor_autocomplete_item, parent, false);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.doctor_image);
            viewHolder.name = (TextView) convertView.findViewById(R.id.doctor_name);
            viewHolder.code = (TextView) convertView.findViewById(R.id.doctor_code);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText((doctor.getName().length() > 24)?
                doctor.getName().substring(0, 24)+"...":
                doctor.getName());
        viewHolder.code.setText("cmp: "+ doctor.getCode());

        Picasso.with(getContext()).
                load("http://200.48.13.46/cmp/fotos/"+ String.format("%05d", Integer.parseInt(doctor.getCode()))+".jpg").
                error(R.drawable.avatar).
                into(viewHolder.image);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Doctor> temp = new ArrayList<Doctor>();
                ArrayList<Integer> data = new ArrayList<Integer>();
                FilterResults filterResults = new FilterResults();

                Realm  realm = Realm.getDefaultInstance();

                RealmResults<Doctor> realmResults = realm.where(Doctor.class).contains("name",
                        constraint.toString()).findAll();

                for(Doctor doctor : realmResults){
                    data.add(Integer.valueOf(doctor.getId()));
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