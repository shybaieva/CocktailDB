package net.shybaieva.cocktail_db;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    RecyclerView cocktailsRV;
    ImageButton filterBtn;
    TextView drinks;
    final String [] temp = {""};
    String [] drinksCollection;
    Uri[] drinksIMGCollection;
    Boolean isFirstLaunch=true;
    String result = "";
    String[] categories = {"Ordinary_Drink", "Cocktail",  "c",
            "Shot",  "Beer"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        String url = "https://www.thecocktaildb.com/api/json/v1/1/filter.php?c=";
        //Log.i("Meow", url);
        //+ categories[i];Ordinary Drink"
        //GetCocktails getCocktails =
        try {
            new GetCocktails().execute(url+categories[0]).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFirstLaunch= false;
                Intent i = new Intent(getApplicationContext(), CategoryActivity.class);
                startActivity(i);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        if(isFirstLaunch==false){
            drinks.setText("SecondTime");


        Bundle extras = getIntent().getExtras();

        String[] checkedCategories = extras.getStringArray("categories");
        Log.i( "Meow",checkedCategories[0]);

        String url = "https://www.thecocktaildb.com/api/json/v1/1/filter.php?c=Cocktail";
        GetCocktails getCocktails = new GetCocktails();
        try {
            temp [0] = getCocktails.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    }

    private void init(){
        cocktailsRV = findViewById(R.id.recyclerView);
        filterBtn = findViewById(R.id.filterBtn);
        drinks = findViewById(R.id.drinks);
    }

    class GetCocktails extends AsyncTask<String, Void, String> {

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
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(data);
                drinksIMGCollection = new Uri[jsonArray.length()];
                drinksCollection  = new String[jsonArray.length()];

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
                    drinksCollection[i] = tempArray.getString("strDrink");
                    drinksIMGCollection[i] = Uri.parse(tempArray.getString("strDrinkThumb"));
                   // Log.i("Meow", drinksIMGCollection[i] + " array");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            CocktailData adapter = new CocktailData(getApplicationContext());
            cocktailsRV.setAdapter(adapter);
            cocktailsRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
    }

   class CocktailData extends RecyclerView.Adapter<CocktailData.MViewHolder>{
       Context context;

       public CocktailData(Context context){
           this.context = context;
       }

       @NonNull
       @Override
       public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           LayoutInflater inflater = LayoutInflater.from(context);
           View view = inflater.inflate(R.layout.content_layout, parent, false);
           return new MViewHolder(view);
       }

       @Override
       public void onBindViewHolder(@NonNull MViewHolder holder, int position) {
            holder.tv.setText(drinksCollection[position]);

            Picasso.get().load(drinksIMGCollection[position]).into(holder.imageView);

            for(int i=0; i<categories.length; i++){
                holder.title.setText(categories[i]);
            }

       }

       @Override
       public int getItemCount() {
           return drinksCollection.length;
       }

       public class MViewHolder extends RecyclerView.ViewHolder {

           TextView tv, title;
           ImageView imageView;
           public MViewHolder(@NonNull View itemView) {
               super(itemView);
               tv = itemView.findViewById(R.id.cocktailName);
               imageView = itemView.findViewById(R.id.cocktailImg);
               title = itemView.findViewById(R.id.CategoryTitle);
           }
       }
   }

}
