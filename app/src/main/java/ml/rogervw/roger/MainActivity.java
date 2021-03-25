package ml.rogervw.roger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
/* Created by Roger van Wyk on 23/03/2021 */
public class MainActivity extends AppCompatActivity {

     TextInputEditText editText;
     ImageButton imageButton;
     TextView textNoDataFound;
     RecyclerView recyclerView;
     BooksAdapter adapter;
     static final String SEARCH_RESULTS = "booksSearchResults";

     @Override

     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);

          editText = (TextInputEditText) findViewById(R.id.editText);
          imageButton = (ImageButton) findViewById(R.id.imageButton);
          textNoDataFound = (TextView) findViewById(R.id.text_no_data_found);

          adapter = new BooksAdapter(this, -1);

          recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
          RecyclerView.Adapter booksAdapter = null;
          recyclerView.setAdapter(booksAdapter);

          imageButton.setOnClickListener(new View.OnClickListener() {
               class BookAsyncTask {
                    public void execute() {

                    }
               }

               @Override
               public void onClick(View v) {
                    if (isInternetConnectionAvailable()) {
                         BookAsyncTask task = new BookAsyncTask();
                         task.execute();
                    }else{
                         Toast.makeText(MainActivity.this, "R.string.error_no_internet", Toast.LENGTH_SHORT).show();
                    }
                    
               }

               private boolean isInternetConnectionAvailable() {
                    return false;
               }
          });
          if (savedInstanceState != null){
               Book[] books = (Book[]) savedInstanceState.getParcelableArray(SEARCH_RESULTS);
               adapter.addAll(Arrays.asList(books));
          }
     }

     private void updateUi(List<Book> books){
          if (books.isEmpty()){
               // Show a message if no books are found
               textNoDataFound.setVisibility(View.VISIBLE);
          }else{
               textNoDataFound.setVisibility(View.GONE);
          }
          adapter.clear();
          adapter.addAll(books);
     }

     private String getUserInput(){
          return editText.getText().toString();
     }

     private String getUrlForHttpRequest(){
          final String baseUrl = "https://www.googleapis.com/books/v1/volumes?q=search+";
          String formatUserInput = getUserInput().trim().replaceAll("\\s+","+");
          String url = baseUrl + formatUserInput;
          return url;
     }

     private class BookAsyncTask extends AsyncTask<URL, Void, List<Book>> {

          @Override
          protected List<Book> doInBackground(URL... urls) {
               URL url = createURL(getUrlForHttpRequest());
               String jsonResponse = "";

               try {
                    jsonResponse = makeHttpRequest(url);
               } catch (IOException e) {
                    e.printStackTrace();
               }

               List<Book> books = parseJson(jsonResponse);
               return books;
          }

          @Override
          protected void onPostExecute(List<Book> books) {
               if (books == null) {
                    return;
               }
               updateUi(books);
          }

          private URL createURL(String stringUrl) {
               try {
                    return new URL(stringUrl);
               } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
               }
          }

          private String makeHttpRequest(URL url) throws IOException {
               String jsonResponse = "";

               if (url == null) {
                    return jsonResponse;
               }

               HttpURLConnection urlConnection = null;
               InputStream inputStream = null;

               try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(10000 /* milliseconds */);
                    urlConnection.setConnectTimeout(25000 /* milliseconds */);
                    urlConnection.connect();
                    if (urlConnection.getResponseCode() == 200) {
                         inputStream = urlConnection.getInputStream();
                         jsonResponse = readFromStream(inputStream);
                    } else {
                         Log.e("mainActivity", "Error response code: " + urlConnection.getResponseCode());
                    }
               } catch (IOException | IOExeption e) {
                    e.printStackTrace();
               } finally {
                    if (urlConnection != null) {
                         urlConnection.disconnect();
                    }
                    if (inputStream != null) {
                         inputStream.close();
                    }
               }
               return jsonResponse;
          }

          private String readFromStream(InputStream inputStream) throws IOExeption, IOException {
               StringBuilder output = new StringBuilder();
               if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    String line = reader.
                           readLine();
               }

               return output.toString();
          }

          private List<Book> parseJson(String json) {

               if (json == null) {
                    return null;
               }

               List<Book> books = QueryUtils.extractBooks(json);
               return books;
          }
     }

     @Override
     public void onSaveInstanceState(Bundle outState) {
          super.onSaveInstanceState(outState);
          Book[] books = new Book[adapter.getCount()];
          for (int i = 0; i < books.length; i++) {
               books[i] = adapter.getItem(i);
          }
          outState.putParcelableArray(SEARCH_RESULTS, (Parcelable[]) books);
     }
          private class IOExeption extends Exception {
          }
     }