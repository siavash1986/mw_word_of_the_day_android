package me.siavash.android.wotd.tasks;

import android.os.AsyncTask;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import me.siavash.android.wotd.GlobalInfo;
import me.siavash.android.wotd.entities.Word;
import me.siavash.android.wotd.listeners.WordDownloaderListener;

public class DownloadWordTask extends AsyncTask<String, String, Map<String, Word>> {

  private WordDownloaderListener listener;

  @Override
  protected Map<String, Word> doInBackground(String[] params) {
    Map<String, Word> latestWords;
    String serverAddress = GlobalInfo.REST_SERVER_ADDRESS;
    String endpoint = GlobalInfo.RESET_SERVER_BATCH_ENDPOINT;
    String url = "http://" + serverAddress + endpoint + "?dateBegin=" + params[0];
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    String result = null;
    try {
      result = restTemplate.getForObject(url, String.class);
    } catch (RestClientException e) {
      FirebaseCrash.report(e);
    }
    Gson gson = new Gson();
    if (result != null) {
      try {
        latestWords = gson.fromJson(result, new TypeToken<Map<String, Word>>() {
        }.getType());
      } catch (JsonSyntaxException e) {
        FirebaseCrash.report(e);
        latestWords = new HashMap<>();
      }
      return latestWords;
    } else {
      return new HashMap<>();
    }

  }

  @Override
  protected void onPostExecute(Map<String, Word> wordCollection) {
    synchronized (this) {
      if (this.listener != null) {
        listener.wordDownloadingComplete(wordCollection);
      }
    }
  }

  public synchronized void setDownloaderListener(WordDownloaderListener listener) {
    this.listener = listener;
  }
}
