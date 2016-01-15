package pe.cayro.sam.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import pe.cayro.sam.R;
import pe.cayro.sam.model.Doctor;
import util.Constants;

/**
 * Created by David on 12/01/16.
 */
public class DoctorAutocompleterAdapter extends ArrayAdapter<String> implements Filterable {

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
        String uuid = getItem(position);
        Log.d(TAG, uuid);

        /* TODO: Check if exist another way to load a information of doctor using Realm. */
        Realm realm = Realm.getDefaultInstance();
        Doctor doctor = realm.where(Doctor.class).equalTo(Constants.UUID, uuid).findFirst();
        realm.close();

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.doctor_autocomplete_item, parent, false);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.doctor_image);
            viewHolder.name  = (TextView)  convertView.findViewById(R.id.doctor_name);
            viewHolder.code  = (TextView)  convertView.findViewById(R.id.doctor_code);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText((doctor.getName().length() > 30) ?
                doctor.getName().substring(0, 30) + Constants.ELLIPSIS : doctor.getName());

        viewHolder.code.setText(Constants.CMP_FIELD+ doctor.getCode());

        Picasso.with(getContext()).
                load(new StringBuilder().append(Constants.CMP_PHOTO_SERVER).
                        append(String.format("%05d", Integer.parseInt(doctor.getCode()))).
                        append(Constants.DOT_JPG).toString()).
                error(R.drawable.avatar).
                into(viewHolder.image);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<String> data = new ArrayList<String>();

                FilterResults filterResults = new FilterResults();

                Realm  realm = Realm.getDefaultInstance();

                RealmResults<Doctor> realmResults = realm.where(Doctor.class).beginGroup().
                        contains(Constants.NAME, constraint.toString().toUpperCase()).or().
                        contains(Constants.CODE, constraint.toString().toUpperCase()).
                        endGroup().findAll();


                /*
                * RealmResults<Record> result = realm.where(Record.class).beginGroup()
                                .contains("doctor.name", data)
                                .or()
                                .contains("patient.name", data)
                                .or()
                                .contains("doctor.code", data)
                                .or()
                                .contains("patient.code", data)
                                .endGroup().findAll();
                * */

                for(Doctor doctor : realmResults){
                    data.add(doctor.getUuid());
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
                    addAll((ArrayList<String>) results.values);
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}