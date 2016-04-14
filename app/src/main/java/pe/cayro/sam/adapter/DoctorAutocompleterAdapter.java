package pe.cayro.sam.adapter;

import android.content.Context;
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
        //Log.d(TAG, uuid);

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

        viewHolder.name.setText(new StringBuilder().append(doctor.getFirstname()).
                append(Constants.SPACE).append(doctor.getLastname()).
                append(Constants.SPACE).append(doctor.getSurname()).toString());

        viewHolder.code.setText(Constants.CMP_FIELD+ doctor.getCode()+" ("+
                doctor.getScore()+") "+doctor.getSpecialty().getName());

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

                if(constraint != null) {
                    RealmResults<Doctor> realmResults = realm.where(Doctor.class).beginGroup().
                            contains(Constants.FIRSTNAME, constraint.toString().toUpperCase()).or().
                            contains(Constants.LASTNAME, constraint.toString().toUpperCase()).or().
                            contains(Constants.SURNAME, constraint.toString().toUpperCase()).or().
                            contains(Constants.CODE, constraint.toString().toUpperCase()).
                            endGroup().
                            findAll();

                    int counter = 0;

                    for (Doctor doctor : realmResults) {
                        counter++;
                        data.add(doctor.getUuid());
                        if (counter == 10) {
                            break;
                        }
                    }
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