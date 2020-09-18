package net.shybaieva.cocktail_db;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class CategoryActivity extends AppCompatActivity {

    TextView tv;
    RecyclerView categoryList;
    Button applyBtn;
    //CheckBox checkBox;
    int i=0;
    final String [] temp = {""};
    String [] arr=new String[11];
    String [] checkedCategories = new String[11];
    String result="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        init();

        String url = "https://www.thecocktaildb.com/api/json/v1/1/list.php?c=list";
        GetCategory getCategory = new GetCategory();
        try {
            temp [0] = getCategory.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                for(int i=0; i<checkedCategories.length; i++){
                    if(checkedCategories[i]!=null){
                       // Log.i("Meow", checkedCategories[i] + " FILTERS  " + i);
                    }
                }
                intent.putExtra("categories", checkedCategories);
                startActivity(intent);

            }
        });
    }

    private void init(){
        applyBtn = findViewById(R.id.applyCategoryBtn);
        categoryList = findViewById(R.id.categoriesList);
        tv = findViewById(R.id.textView);
        //checkBox = findViewById(R.id.checkBox);
    }

    class GetCategory extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    result = result + line;
                }
                //Log.i("meow", result);
                return null;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(String s) {

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String data = null;
            try {
                data = jsonObject.getString("drinks");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Log.i("Meow", data);
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject tempArray = null;
                try {
                    tempArray = jsonArray.getJSONObject(i);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    arr[i] = tempArray.getString("strCategory");
                  //  Log.i("Meow", arr[i] + " ");
                   // tv.setText(category );

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            CategoryAdapter adapter = new CategoryAdapter(getApplicationContext());
            categoryList.setAdapter(adapter);
            categoryList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
        Context context;
        public CategoryAdapter(Context context){
            this.context = context;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.category_list_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.categoryName.setText(arr[position]);
            holder.checkBox.setText(arr[position]);
           if(holder.checkBox.isSelected()) {
               holder.checkBox.setChecked(true);
               checkedCategories[position] = holder.checkBox.getText().toString();
               tv.setText(checkedCategories[position]= " ");
           }
           else
               holder.checkBox.setChecked(false);

        }

        @Override
        public int getItemCount() {
            return arr.length;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView categoryName;
            CheckBox checkBox;

            public MyViewHolder(@NonNull final View itemView) {
                super(itemView);
                categoryName = itemView.findViewById(R.id.categoryName);
                checkBox = itemView.findViewById(R.id.checkBox);

                //itemView.setOnClickListener((View.OnClickListener) this);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            if(compoundButton.isChecked()){
                            Toast.makeText(CategoryActivity.this, "selected item is " + checkBox.getText(), Toast.LENGTH_SHORT).show();
                            checkedCategories[i] = checkBox.getText().toString();
                            i++;}
                        }
                    }
                });

            }

        }
    }
}
