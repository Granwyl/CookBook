package id.ac.cookbook.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.ac.cookbook.DetailActivity;
import id.ac.cookbook.R;
import id.ac.cookbook.data.Recipe;
import id.ac.cookbook.data.RecipeAdapter;
import id.ac.cookbook.data.User;
import id.ac.cookbook.volley.DbContract;
import id.ac.cookbook.volley.VolleyConnection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookmarkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookmarkFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private User me;
    RecyclerView rvData;
    ArrayList<Recipe> listRecipe;
    RecipeAdapter recipeAdapter;

    ProgressDialog progressDialog;

    public BookmarkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BookmarkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookmarkFragment newInstance(User param1) {
        BookmarkFragment fragment = new BookmarkFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            me = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        rvData = view.findViewById(R.id.rvFrBookmarkData);

        getMyBookmark(me.getId()+"");
    }

    public void getMyBookmark(final String idUser){
        listRecipe = new ArrayList<>(); // reset
        if (checkNetworkConnection()){
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_MASTER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.getInt("code");
                        String message = jsonObject.getString("message");
                        if (code < 0){
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                        if (code == 1){
                            JSONArray jsonArray = jsonObject.getJSONArray("datarecipe");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject recipeObj = jsonArray.getJSONObject(i);
                                String kategori;
                                if (recipeObj.getInt("id_category") == 1){
                                    kategori = "Food";
                                }else if (recipeObj.getInt("id_category") == 2){
                                    kategori = "Beverage";
                                }else {
                                    kategori = "Side Dish";
                                }
                                Recipe recipe = new Recipe(
                                        recipeObj.getInt("id"),
                                        recipeObj.getString("title"),
                                        recipeObj.getString("username"),
                                        kategori,
                                        recipeObj.getString("ingredients"),
                                        recipeObj.getString("steps"),
                                        recipeObj.getInt("status_publish"),
                                        recipeObj.getDouble("rate")
                                );
                                listRecipe.add(recipe);
                            }
                            setUpRecyclerView(listRecipe);
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("function", "getMyBookmark");
                    params.put("id_user", idUser);
                    return params;
                }
            };

            VolleyConnection.getInstance(getActivity()).addToRequestQueue(stringRequest);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                }
            }, 1000);
        }else{
            Toast.makeText(getActivity(), "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    void setUpRecyclerView(ArrayList<Recipe> listRecipe){
        rvData.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvData.setHasFixedSize(true);

        recipeAdapter = new RecipeAdapter(listRecipe);
        recipeAdapter.setOnItemClickCallback(new RecipeAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(Recipe recipe) {
                Intent toDetail = new Intent(getActivity(), DetailActivity.class);
                toDetail.putExtra("user", me);
                toDetail.putExtra("recipe", recipe);
                startActivity(toDetail);
            }
        });
        rvData.setAdapter(recipeAdapter);
    }
}